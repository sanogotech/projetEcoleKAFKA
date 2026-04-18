# GUCE CI 2.0 — plateforme multi-services (Java / Spring Boot / Kafka)

Projet Maven multi-modules **`guce-ci`** : microservices REST pour le guichet unique du commerce extérieur (démo formation), avec publication d’événements **Apache Kafka** en **production** ou **simulation**, et **simulation par logs** en **développement** (aucun broker requis).

---

## Sommaire

1. [Prérequis](#prérequis)  
2. [Structure du dépôt](#structure-du-dépôt)  
3. [Build](#build)  
4. [Modes d’exécution : `dev`, `prod`, `sim`](#modes-dexécution--dev-prod-sim)  
5. [Kafka : topics et variables](#kafka--topics-et-variables)  
6. [Lancer en mode développement (sans Kafka)](#lancer-en-mode-développement-sans-kafka)  
7. [Lancer en mode production ou simulation (avec Kafka)](#lancer-en-mode-production-ou-simulation-avec-kafka)  
8. [Portail web `guce-portal`](#portail-web-guce-portal)  
9. [Services : ports, Swagger, base de données](#services--ports-swagger-base-de-données)  
10. [Dépannage](#dépannage)  

---

## Prérequis

| Composant | Mode **dev** | Mode **prod** / **sim** (Kafka réel) |
|-----------|----------------|--------------------------------------|
| **JDK** | 17+ | 17+ |
| **Maven** | 3.9+ recommandé | idem |
| **Apache Kafka** | Non | Oui (cluster ou broker local) |
| **PostgreSQL** | Optionnel (H2 en mémoire par défaut) | Recommandé pour la persistance |
| **Navigateur** | Pour Swagger UI et le portail | idem |

---

## Structure du dépôt

```
guce-ci/                          (POM parent)
├── declaration-service/          DAU, topic dau.soumise
├── manifest-service/           manifeste.recu
├── authorization-service/      autorisations API (pas de Kafka)
├── payment-service/            dau.taxee, paiement.confirme
├── inspection-service/         inspection (pas de Kafka)
├── pcs-adapter/                PCS (pas de Kafka)
├── reference-service/          référentiels (pas de Kafka)
├── operator-service/           opérateurs (pas de Kafka)
├── notification-service/       notification.demande
├── audit-service/              audit REST (pas de Kafka)
└── guce-portal/                UI Thymeleaf / Bootstrap (proxy vers les API)
```

---

## Build

À la racine du dépôt :

```bash
mvn -q -DskipTests compile
```

Packager un module (ex. `declaration-service`) :

```bash
cd declaration-service
mvn -q -DskipTests package
```

Exécuter les tests d’un module :

```bash
cd declaration-service
mvn test
```

---

## Modes d’exécution : `dev`, `prod`, `sim`

Le comportement **Kafka** et la **base de données** sont pilotés par :

1. Le **profil Spring** : `dev`, `prod` ou `sim` (`spring.profiles.active` ou `SPRING_PROFILES_ACTIVE`).  
2. La propriété **`guce.mode`** (souvent alignée sur le profil dans les fichiers `application-*.properties`).

| Profil / `guce.mode` | Kafka | Comportement typique |
|----------------------|--------|----------------------|
| **`dev`** | **Non** (auto-configuration Kafka **désactivée** sur les services concernés) | H2 en mémoire, événements **simulés** (logs `[SIMULATION]`). Aucun broker à lancer. |
| **`prod`** | **Oui** | PostgreSQL (recommandé), producteurs Kafka **réels** vers le cluster (`KAFKA_BOOTSTRAP_SERVERS`). |
| **`sim`** | **Oui** | Même logique que prod côté Kafka ; souvent brokers **dédiés** tests / formation (`application-sim.properties`). |

Les services **sans** dépendance Kafka (`authorization-service`, `inspection-service`, `pcs-adapter`, `reference-service`, `operator-service`, `audit-service`) se contentent du profil pour la base ; ils n’ouvrent pas de connexion broker.

---

## Kafka : topics et variables

### Topics métier (producteurs intégrés dans ce dépôt)

| Topic | Service(s) |
|-------|------------|
| `dau.soumise` | `declaration-service` |
| `manifeste.recu` | `manifest-service` |
| `dau.taxee` | `payment-service` |
| `paiement.confirme` | `payment-service` |
| `notification.demande` | `notification-service` |

Les noms peuvent être surchargés via `guce.kafka.*` dans chaque `application.properties`.

### Variables d’environnement utiles (prod / sim)

| Variable | Rôle |
|----------|------|
| `KAFKA_BOOTSTRAP_SERVERS` | Liste des brokers Kafka, ex. `localhost:9092` ou `kafka1:9092,kafka2:9092`. |
| `SPRING_PROFILES_ACTIVE` | `prod` ou `sim` (ou `dev`). |
| `SPRING_DATASOURCE_URL` | JDBC PostgreSQL en prod (par service). |
| `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | Identifiants base. |

**À faire côté infrastructure :** créer les topics sur le cluster (ou laisser la création automatique si le broker l’autorise). En local, un seul broker Kafka suffit pour les tests.

---

## Lancer en mode développement (sans Kafka)

Objectif : **développement rapide**, pas de cluster Kafka, événements **loggés** à la place de la publication.

1. Activer le profil **`dev`** (souvent le défaut dans `application.properties`).  
2. Démarrer **chaque** microservice nécessaire sur son port (voir [table ci-dessous](#services--ports-swagger-base-de-données)).

**Exemple — `declaration-service` (Windows PowerShell) :**

```powershell
cd declaration-service
$env:SPRING_PROFILES_ACTIVE="dev"
mvn spring-boot:run
```

**Exemple — même chose en une ligne Maven :**

```bash
cd declaration-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Ou utiliser `start-dev.bat` à la racine de `declaration-service` si présent.

Répéter pour les autres modules selon les besoins (tous les ports **8080–8089** peuvent tourner en parallèle : bases H2 **isolées** par service).

**Vérification :** les logs ne doivent **pas** tenter de se connecter à Kafka en `dev` pour les services qui excluent `KafkaAutoConfiguration`. Les actions métier affichent des lignes du type `[SIMULATION] topic=...`.

---

## Lancer en mode production ou simulation (avec Kafka)

**Prérequis :** broker(s) Kafka joignables ; topics créés ou auto-création activée ; PostgreSQL si vous suivez les `application-prod.properties` (recommandé hors démo).

### 1. Démarrer Kafka

Adaptez à votre installation (Docker, bare metal, cloud). Exemple indicatif de variable :

```text
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

### 2. Lancer un service en `prod`

**Linux / macOS :**

```bash
export SPRING_PROFILES_ACTIVE=prod
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/guce_declaration
export SPRING_DATASOURCE_USERNAME=guce
export SPRING_DATASOURCE_PASSWORD=***
cd declaration-service
mvn spring-boot:run
```

**Windows (PowerShell) :**

```powershell
$env:SPRING_PROFILES_ACTIVE="prod"
$env:KAFKA_BOOTSTRAP_SERVERS="localhost:9092"
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/guce_declaration"
$env:SPRING_DATASOURCE_USERNAME="guce"
$env:SPRING_DATASOURCE_PASSWORD="secret"
cd declaration-service
mvn spring-boot:run
```

`guce.mode` est positionné à **`prod`** dans `application-prod.properties` : les **producteurs Kafka** réels sont activés pour les services concernés.

### 3. Profil **`sim`**

Même principe que `prod` pour Kafka, avec souvent des brokers ou URLs dédiés :

```bash
export SPRING_PROFILES_ACTIVE=sim
export KAFKA_BOOTSTRAP_SERVERS=kafka-sim:9092   # exemple
cd declaration-service
mvn spring-boot:run
```

Les fichiers `application-sim.properties` par module complètent / surchargent la configuration.

### 4. JAR exécutable

Après `mvn package` :

```bash
java -jar declaration-service/target/declaration-service-0.1.0-SNAPSHOT.jar --spring.profiles.active=prod
```

(en passant toujours `KAFKA_BOOTSTRAP_SERVERS` et la datasource si besoin.)

---

## Portail web `guce-portal`

- **URL :** [http://localhost:8090](http://localhost:8090)  
- **Rôle :** interface Thymeleaf + Bootstrap ; les formulaires appellent les API **en HTTP** vers les microservices (pas de Kafka dans le navigateur).  
- **Lancement :**

```bash
cd guce-portal
mvn spring-boot:run
```

ou `guce-portal/start-portal.bat` sous Windows.

Les URLs des backends sont dans `guce-portal/src/main/resources/application.properties` (`guce.services.*`, par défaut `http://localhost:8080` … `8089`). En **prod**, si les services sont derrière une gateway ou d’autres hôtes, **surchargez** ces propriétés ou utilisez des variables d’environnement (à ajouter si vous externalisez la config).

**Important :** démarrez les microservices **avant** d’utiliser les formulaires du portail. En **dev**, le portail fonctionne avec les services en **mode dev sans Kafka** ; en **prod**, les services publient vers Kafka **côté serveur** lors des appels REST issus du portail.

---

## Services : ports, Swagger, base de données

| Module | Port HTTP | Swagger UI | Kafka (si activé) | Base par défaut en dev |
|--------|-----------|------------|--------------------|-------------------------|
| `declaration-service` | 8080 | `/swagger-ui.html` | `dau.soumise` | H2 |
| `manifest-service` | 8081 | idem | `manifeste.recu` | H2 |
| `authorization-service` | 8082 | idem | — | H2 |
| `payment-service` | 8083 | idem | `dau.taxee`, `paiement.confirme` | H2 |
| `inspection-service` | 8084 | idem | — | H2 |
| `pcs-adapter` | 8085 | idem | — | H2 |
| `reference-service` | 8086 | idem | — | *(données en mémoire)* |
| `operator-service` | 8087 | idem | — | H2 |
| `notification-service` | 8088 | idem | `notification.demande` | H2 |
| `audit-service` | 8089 | idem | — | H2 |
| **`guce-portal`** | **8090** | — | — | — |

**Santé :** `GET http://localhost:<port>/actuator/health` (selon exposition Actuator).

Documentation détaillée du premier microservice : **`declaration-service/README.md`**.

---

## Dépannage

| Symptôme | Piste |
|----------|--------|
| Erreur de connexion Kafka en **prod** | Vérifier `KAFKA_BOOTSTRAP_SERVERS`, pare-feu, et que le profil actif est bien `prod` ou `sim`. |
| Encore des logs `[SIMULATION]` en prod | Vérifier que `guce.mode` n’est pas `dev` (fichiers `application-*.properties` et surcharge env). |
| Port déjà utilisé | Changer `server.port` ou arrêter l’autre processus. |
| Portail : « Service injoignable » | Démarrer le microservice cible sur le port attendu ; vérifier `guce.services` dans `guce-portal`. |
| PostgreSQL en prod | Créer les bases par service et aligner `SPRING_DATASOURCE_*` avec `application-prod.properties` de chaque module. |

---

## Licence / usage

Projet de **démonstration pédagogique** ; adapter sécurité, secrets et durcissement avant tout usage réel en production.
