# declaration-service (GUCE CI 2.0 — Phase 1 MVP)

Microservice Spring Boot **soumission** de DAU : architecture hexagonale, persistance JPA, publication d’événements sur le topic **`dau.soumise`** (Kafka réel ou **simulation**).

## Prérequis

- **Java 17+**
- **Maven 3.9+**
- **Kafka** (uniquement pour les profils `prod` ou `sim`)

## Modes (`guce.mode`)

| Valeur | Comportement |
|--------|----------------|
| `dev` | H2 en mémoire, **aucun broker Kafka** (auto-config Kafka désactivée), événements en **simulation** (logs). |
| `prod` | PostgreSQL recommandé, **Kafka** réel (`spring.kafka.bootstrap-servers`). |
| `sim` | Kafka réel (brokers dédiés), configuration typique pour tests de charge / formation. |

Activer un profil Spring : `SPRING_PROFILES_ACTIVE=dev` (ou `prod`, `sim`).

## Lancer en local (dev)

```bat
start-dev.bat
```

ou :

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## API

- `POST /api/v1/declarations` — soumet une DAU (JSON), retourne `201` + identifiant.
- `GET /api/v1/declarations/{id}` — consultation.
- Swagger UI : **http://localhost:8080/swagger-ui/index.html**
- Actuator : **http://localhost:8080/actuator/health**

### Exemple `curl`

```bash
curl -s -X POST http://localhost:8080/api/v1/declarations ^
  -H "Content-Type: application/json" ^
  -d "{\"correlationId\":\"corr-1\",\"declarantId\":\"TR-1\",\"customsOfficeCode\":\"CIAB1\",\"referenceNumber\":\"REF-1\",\"payloadJson\":\"{\\\"lines\":[]}\"}"
```

## Configuration utile

| Variable / propriété | Rôle |
|----------------------|------|
| `guce.mode` | `dev` / `prod` / `sim` |
| `guce.kafka.topic-dau-soumise` | Nom du topic (défaut `dau.soumise`) |
| `KAFKA_BOOTSTRAP_SERVERS` | Brokers Kafka (profils `prod` / `sim`) |
| `SPRING_DATASOURCE_*` | Base PostgreSQL en prod |

## Structure du code

```
com.guce.declaration
├── domain/                 # Agrégat, statuts
├── application/            # Cas d’usage, ports
├── infrastructure/
│   ├── web/                # REST, DTO
│   ├── persistence/        # JPA
│   └── messaging/          # Kafka + simulation
└── config/                 # Propriétés, Kafka producer
```

## Tests

```bash
mvn test
```
