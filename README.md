# GUCE CI 2.0 — multi-services REST

Projet Maven multi-modules (`guce-ci`). **Aucune application web frontale** pour l’instant : uniquement des **API REST** + Swagger UI par service.

## Build global

```bash
mvn -q -DskipTests compile
```

## Portail web (Thymeleaf + Bootstrap)

- Module **`guce-portal`** — port **8090** : tableau de bord avec onglets, formulaires reliés aux API, boutons **« Remplir exemple »** (données démo).
- Lancer : `cd guce-portal` puis `mvn spring-boot:run` ou `start-portal.bat`.
- Les microservices **8080–8089** doivent être démarrés pour que les envois fonctionnent (mode **dev** sans Kafka côté services : simulation par logs).

## Services (ports en `dev`)

| Service | Port | Swagger UI | Rôle principal |
|---------|------|------------|------------------|
| `declaration-service` | 8080 | `/swagger-ui.html` | DAU, topic `dau.soumise` |
| `manifest-service` | 8081 | idem | E-manifestes, `manifeste.recu` |
| `authorization-service` | 8082 | idem | Autorisations préalables (API) |
| `payment-service` | 8083 | idem | Taxes / paiements, `dau.taxee`, `paiement.confirme` |
| `inspection-service` | 8084 | idem | Canaux vert/orange/rouge |
| `pcs-adapter` | 8085 | idem | Mouvements conteneurs, accusé mainlevée |
| `reference-service` | 8086 | idem | Codes SH, bureaux, tarifs (données démo) |
| `operator-service` | 8087 | idem | Opérateurs / transitaires |
| `notification-service` | 8088 | idem | Demandes de notification, `notification.demande` |
| `audit-service` | 8089 | idem | Piste d’audit |
| **`guce-portal`** | **8090** | *(UI Thymeleaf)* | **Portail démo** — pas d’API REST métier |

## Lancer un service

```bash
cd declaration-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

(Kafka **non requis** en `dev` pour les services qui publient : simulation par logs.)

## Documentation détaillée

Voir `declaration-service/README.md` pour le mode `guce.mode` et les profils `prod` / `sim`.
