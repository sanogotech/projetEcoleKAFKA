
# 📘 CAHIER DES CHARGES – GUCE CI 2.0  
## Architecture événementielle, DDD, microservices & plateforme d’apprentissage intégrée

---

## 1. CONTEXTE ET PRÉSENTATION DE L’ENTREPRISE

### 1.1 Le GUCE CI en bref

| Élément | Description |
| :--- | :--- |
| **Nom** | Guichet Unique du Commerce Extérieur de Côte d’Ivoire |
| **Création** | 1er juillet 2013 |
| **Statut** | Société d’État à majorité publique (depuis 2020) |
| **Mission** | Dématérialisation des procédures douanières, portuaires et commerciales |
| **Volume** | > 500 000 déclarations (DAU) par an, ~2000 transitaires actifs |
| **Parties prenantes** | DGDDI (Douanes), Port Autonome d’Abidjan, Ministère du Commerce, Trésor Public, opérateurs mobiles (Wave, MTN), consignataires, importateurs/exportateurs |

### 1.2 Problématiques actuelles

- **Monolithe centralisé** : évolutions lentes, fort couplage, difficulté à monter en charge.
- **Latence élevée** : délai de dédouanement 3 à 5 jours (objectif < 24h).
- **Manque de traçabilité** : audit difficile, rejeu impossible.
- **Intégrations fragiles** : appels synchrones (REST/SOAP) avec le PCS, les banques, Wave.
- **Absence de cache** : requêtes répétitives sur les référentiels (codes SH, tarifs).
- **Détection de fraude limitée** : règles codées en dur, pas de CEP.

### 1.3 Objectifs de la refonte

- **Passage à une architecture orientée événements** (Kafka comme colonne vertébrale).
- **Découpage en microservices** (DDD, contexte borné par domaine métier).
- **Temps réel** : mainlevée < 500 ms après paiement.
- **Traçabilité inaltérable** : audit complet via rejeu d’événements.
- **Scalabilité horizontale** : gérer 20 000 déclarations/heure.
- **Disponibilité 99,9 %**.
- **Formation continue** : intégrer une plateforme « école » pour monter en compétence sur Kafka, Spring Boot, Python.

---

## 2. PÉRIMÈTRE FONCTIONNEL DÉTAILLÉ

### 2.1 Modules métier (contextes bornés DDD)

| Contexte borné | Microservice | Fonctions principales |
| :--- | :--- | :--- |
| Déclarations | `declaration-service` | Soumission DAU, calcul taxes, validation douane, mainlevée |
| Manifestes | `manifest-service` | Réception e‑manifeste, extraction lignes, matching avec DAU |
| Autorisations préalables | `authorization-service` | Demande API, workflow multi‑administrations, expiration |
| Paiements & taxes | `payment-service` | Calcul droits, génération bordereau, paiement mobile (Wave, Trésor Pay), mainlevée financière |
| Contrôle & inspection | `inspection-service` | Orientation canal (vert/orange/rouge), planification inspection, résultat |
| Port Community System | `pcs-adapter` | Réception mouvements conteneurs, notification mainlevée au port |
| Référentiels | `reference-service` | Gestion codes SH, bureaux de douane, tarifs, taux de change |
| Opérateurs | `operator-service` | Enregistrement, authentification, profils, rôles, accréditations |
| Notifications | `notification-service` | Alertes (email, SMS, in‑app) sur événements métier |
| Observabilité & audit | `audit-service` | Centralisation logs, piste d’audit inaltérable, tableau de bord supervision |

### 2.2 Flux événementiels principaux (Kafka)

| Topic | Producteur | Consommateurs | Description |
| :--- | :--- | :--- | :--- |
| `dau.soumise` | `declaration-service` | `payment-service`, `inspection-service`, `audit-service` | Nouvelle déclaration |
| `dau.taxee` | `payment-service` | `declaration-service`, `notification-service` | Taxes calculées |
| `dau.validee` | `declaration-service` | `payment-service`, `pcs-adapter` | Validation douane |
| `paiement.confirme` | `payment-service` | `declaration-service`, `pcs-adapter` | Paiement effectué |
| `mainlevee.emise` | `declaration-service` | `pcs-adapter`, `notification-service` | Mainlevée générée |
| `manifeste.recu` | `manifest-service` | `declaration-service` | Manifeste déposé |
| `risque.fraude.detectee` | `flink-job` | `inspection-service`, `audit-service` | Alerte fraude |
| `notification.demande` | (tout service) | `notification-service` | Demande d’envoi alerte |

### 2.3 Tableaux de bord et reporting

- **DGDDI** : volume DAU/heure, recettes estimées, délai moyen dédouanement, taux de fraude détectée.
- **Transitaires** : suivi déclarations, taxes dues, historique mainlevées.
- **Port Autonome** : mouvements conteneurs, mainlevées émises, temps de séjour.
- **Auditeurs** : recherche historique complète d’une déclaration (piste d’audit).

---

## 3. ARCHITECTURE TECHNIQUE CIBLE

### 3.1 Stack technique (composants & rôles)

| Composant | Rôle | Technologie(s) |
| :--- | :--- | :--- |
| **Front Web** | Interfaces utilisateur | Angular / React, Bootstrap |
| **API Gateway** | Authentification, routage, rate limiting | Kong / Spring Cloud Gateway + OAuth2 |
| **Microservices** | Logique métier (DDD, hexagonale) | Java Spring Boot 3.x, Kotlin |
| **Colonne vertébrale** | Messagerie asynchrone, persistance, rejeu | Apache Kafka (KRaft, 3+ nœuds) |
| **CDC** | Capture changement bases → Kafka | Debezium (Kafka Connect) |
| **Traitement flux léger** | Filtrage, enrichissement | Kafka Streams (Java) |
| **Traitement flux lourd** | Agrégations, CEP, état distribué | Apache Flink (cluster Kubernetes) |
| **Cache & état** | Cache référentiels, sessions, verrous, files d’attente | Redis Cluster (3 masters + replicas) |
| **Stockage objet** | Documents scannés, manifestes, checkpoints Flink | MinIO (mode distribué, S3) |
| **Observabilité** | Logs centralisés, métriques, audit | ELK Stack (Elasticsearch, Logstash, Kibana) + Prometheus/Grafana |
| **Consommateurs alternatifs** | Data, IA, ETL, automatisation | Python (kafka-python, requests) |

### 3.2 Architecture hexagonale type (exemple `declaration-service`)

```
src/main/java/com/guce/declaration/
├── domain/               # Aggregate Declaration, value objects, services domaine
├── application/          # Use cases (ports d’entrée)
├── infrastructure/
│   ├── web/              # REST controller (adaptateur entrant)
│   ├── persistence/      # JPA repository (adaptateur sortant)
│   ├── messaging/        # Kafka producer/consumer (adaptateur sortant)
│   ├── storage/          # MinIO adapter
│   └── cache/            # Redis adapter
└── config/               # Spring configuration
```

### 3.3 Modes de fonctionnement : DEV / PROD / SIMULATION

| Mode | Kafka | Base de données | Redis | MinIO | Objectif |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **DEV** | Mock (Embedded Kafka) | H2 | Simulé (carte mémoire) | Simulé (répertoire local) | Développement rapide, tests unitaires |
| **PROD** | Réel (cluster) | MySQL / PostgreSQL | Réel (cluster) | Réel (cluster) | Production |
| **SIMULATION** | Réel (brokers dédiés) | MySQL (données anonymisées) | Réel (volume réduit) | Réel (bucket de test) | Tests de charge, chaos engineering |

Un **flag de configuration** (`guce.mode=dev|prod|sim`) bascule l’ensemble des adaptateurs via `@Profile` ou `@ConditionalOnProperty`.

### 3.4 Consommateurs Python (multi‑langages)

Structure du projet Python :

```
python-consumers/
├── kafka-consumer/          # consumer direct Kafka → traitement
├── rest-consumer/           # polling REST (fallback)
├── hybrid-consumer/         # Kafka → appel REST vers microservice
├── ai-consumer/             # inference ML sur flux
└── config/                  # connexion, logging
```

Exemples de consommateurs :

- **Consumer direct** : souscrit à `dau.soumise`, calcule un score de risque avec un modèle scikit‑learn.
- **Hybride** : lit `paiement.confirme`, puis appelle une API externe de confirmation bancaire.
- **REST polling** : utilisé quand Kafka est indisponible (fallback).

---

## 4. EXIGENCES NON FONCTIONNELLES (SLAs)

| Qualité | Objectif | Moyens techniques |
| :--- | :--- | :--- |
| **Disponibilité** | 99,9 % | Cluster Kafka multi‑AZ, réplication Redis, MinIO erasure coding |
| **Latence** | < 500 ms pour 95 % des DAU | Cache Redis, traitement asynchrone, partitionnement |
| **Traçabilité** | Inaltérable, horodatée | Topics Kafka rétention 7 jours, Elasticsearch pour audit |
| **Sécurité** | Chiffrement TLS, RBAC | TLS 1.3 pour Kafka, chiffrement au repos MinIO, OAuth2 / Keycloak |
| **Scalabilité** | Horizontale | Partitionnement Kafka, scaling Flink, Redis Cluster |
| **Reprise après sinistre** | RTO < 2h, RPO < 15 min | Kafka MirrorMaker, sauvegardes MinIO vers site secondaire |

---

## 5. BACKLOG DÉTAILLÉ (EPICS → USER STORIES)

### Epic 1 – Fondations & ingestion (Must)

| Feature | User Story | Critères d’acceptation |
| :--- | :--- | :--- |
| 1.1 Cluster Kafka | En tant qu’admin, je veux un cluster Kafka 3 nœuds (KRaft) pour la résilience. | Topics créés, producteurs/consommateurs fonctionnels. |
| 1.2 Debezium CDC | En tant que système, je veux capturer les modifications des tables `declarations` vers Kafka. | Connecteur Debezium opérationnel, lag < 1 s. |
| 1.3 MinIO | En tant que développeur, je veux stocker les pièces jointes des DAU dans MinIO. | Upload/download via URL signée. |
| 1.4 Soumission DAU (REST → Kafka) | En tant que transitaire, je veux soumettre une DAU avec fichiers. | Événement `dau.soumise` produit, ID retourné. |

### Epic 2 – Cache & calculs temps réel (Must)

| Feature | User Story | Critères d’acceptation |
| :--- | :--- | :--- |
| 2.1 Redis Cluster | En tant qu’admin, je veux un cache distribué pour les référentiels. | Redis Cluster 3 masters + 3 replicas. |
| 2.2 Cache des tarifs | En tant que système, je veux que les tarifs soient lus depuis Redis (rafraîchis par CDC). | Modification tarif → cache mis à jour < 1 s. |
| 2.3 Calcul taxes (Flink) | En tant que système, je veux calculer les taxes dès réception de `dau.soumise`. | `dau.taxee` publié, montant correct. |
| 2.4 Paiement mobile | En tant que déclarant, je veux payer par Wave/Trésor Pay. | File d’attente Redis, événement `paiement.confirme`. |

### Epic 3 – Observabilité & fiabilité (Should)

| Feature | User Story | Critères d’acceptation |
| :--- | :--- | :--- |
| 3.1 ELK Stack | En tant qu’exploitant, je veux centraliser les logs de tous les services. | Logs JSON avec `correlationId`, recherche dans Kibana. |
| 3.2 Transactional Outbox | En tant que système, je veux garantir l’atomicité entre écriture base et publication Kafka. | Table `outbox` + Debezium, aucun événement perdu. |
| 3.3 DRP (MirrorMaker) | En tant qu’admin, je veux répliquer les topics vers un site secondaire. | Bascule manuelle, RTO < 2h, RPO < 15 min. |
| 3.4 Tableau de bord audit | En tant qu’auditeur, je veux visualiser l’historique complet d’une déclaration. | Interface Kibana dédiée, recherche par ID. |

### Epic 4 – Intelligence & optimisation (Could)

| Feature | User Story | Critères d’acceptation |
| :--- | :--- | :--- |
| 4.1 Détection fraude (Flink CEP) | En tant que douane, je veux détecter le fractionnement de commandes. | Alerte `risque.fraude.detectee` en temps réel. |
| 4.2 Vue 360° conteneur | En tant que transitaire, je veux suivre mon conteneur. | État enrichi dans Redis, accessible via API. |
| 4.3 Recommandation canal | En tant qu’agent, je veux une suggestion de canal (vert/orange/rouge). | Score de risque affiché dans l’interface. |
| 4.4 Consommateurs Python | En tant que data engineer, je veux traiter les flux avec Python. | Consumer `dau.soumise` calcule un indicateur supplémentaire. |

### Epic 5 – Plateforme école / formation (Should)

| Feature | User Story | Critères d’acceptation |
| :--- | :--- | :--- |
| 5.1 Mode simulation | En tant que formateur, je veux basculer en mode mock (Kafka, Redis simulés). | `guce.mode=dev` sans infrastructure réelle. |
| 5.2 TP Kafka interactifs | En tant qu’apprenant, je veux exécuter des producers/consumers Java et Python. | Scripts `start-dev.bat` / `.ksh` prêts à l’emploi. |
| 5.3 Dashboard pédagogique (AKHQ) | En tant qu’étudiant, je veux visualiser topics, lag, replay. | Accès à AKHQ / Kafka UI intégré au docker‑compose. |

---

## 6. FEUILLE DE ROUTE – 4 PHASES (15 mois)

| Phase | Durée | Objectif | Livrable principal |
| :--- | :--- | :--- | :--- |
| **Phase 1 – Fondations** | Mois 1-3 | Kafka, Debezium, MinIO, première DAU soumise | Cluster Kafka, connecteur CDC, API soumission |
| **Phase 2 – Accélération** | Mois 4-7 | Redis, Flink, calcul taxes, paiement mobile | Cache référentiels, job Flink, paiement Wave |
| **Phase 3 – Observabilité** | Mois 8-11 | ELK, Outbox, DRP, sécurité | Logs centralisés, audit, réplication site secondaire |
| **Phase 4 – Intelligence** | Mois 12-15 | Détection fraude, vue 360°, optimisation | CEP Flink, tableau de bord prédictif |

---

## 7. LIVRABLES ATTENDUS

### 7.1 Code source

- 10 microservices Spring Boot 3.x (architecture hexagonale).
- Consommateurs Python (kafka‑python, requests).
- Docker‑compose complet (Kafka, Zookeeper/KRaft, Redis, MinIO, ELK, Flink, AKHQ).
- Scripts de lancement (`start-dev.bat`, `start-prod.ksh`).

### 7.2 Documentation

- Guide d’installation et de configuration.
- Architecture Decision Records (ADR).
- Glossaire (200+ termes métier & techniques).
- 15 TP progressifs (du niveau débutant à expert Kafka).

### 7.3 Formation

- Ateliers DDD/Event Storming.
- Modules e‑learning sur Kafka, Spring Boot, Python.
- Simulation de panne (chaos engineering).

---

## 8. ANNEXES – EXTRAITS DU GLOSSAIRE

| Terme | Définition |
| :--- | :--- |
| **DAU** | Déclaration en Détail Unique – document douanier central. |
| **API** | Autorisation Préalable à l’Importation (Ministère du Commerce). |
| **Bounded Context** | Limite explicite d’un modèle métier (DDD). |
| **Transactional Outbox** | Pattern garantissant atomicité base/événements via table `outbox`. |
| **Lag Kafka** | Différence entre dernier message produit et dernier message consommé. |
| **Checkpoint Flink** | Capture de l’état distribué pour reprise après panne. |

---

## 9. CONCLUSION

Ce cahier des charges fusionne les besoins **réels et complexes du GUCE CI** avec la **plateforme école Kafka** (Java + Python) pour obtenir :

- Une **architecture de production** robuste, scalable, événementielle.
- Un **environnement de développement et de formation** intégré (mode simulation, TP, monitoring).
- Une **montée en compétence continue** des équipes sur les technologies clés.

La prochaine étape consiste à produire le **code minimal viable** (MVP) pour la Phase 1 : cluster Kafka, Debezium sur PostgreSQL, microservice `declaration-service` avec soumission DAU et stockage MinIO.

---

*Document validé pour lancement de la refonte – version 2.0 – 14 avril 2026*
