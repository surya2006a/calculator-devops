# Calculator DevOps

A Calculator REST API (Spring Boot + Maven) with a full CI/CD pipeline:
GitHub -> Jenkins -> Docker Hub -> Docker Compose (Nginx + 2 API replicas).

## API

| Operation | Endpoint |
|-----------|----------|
| Add       | `GET /api/v1/add?a=2&b=3` |
| Subtract  | `GET /api/v1/subtract?a=5&b=2` |
| Multiply  | `GET /api/v1/multiply?a=4&b=3` |
| Divide    | `GET /api/v1/divide?a=10&b=4` (b=0 returns HTTP 400) |
| Health    | `GET /actuator/health` |

Example response:

```json
{ "operation": "add", "a": 2, "b": 3, "result": 5 }
```

Error response (HTTP 400):

```json
{ "status": 400, "error": "Bad Request", "message": "Division by zero is not allowed", "timestamp": "..." }
```

## Build, test, run

```bash
mvn clean package        # build the executable JAR
mvn test                 # run unit + MockMvc integration tests
java -jar target/calculator-api.jar
curl "http://localhost:8080/api/v1/add?a=2&b=3"
```

## Docker

```bash
docker build -t calculator-api .
docker run -p 8080:8080 calculator-api
```

## Docker Compose (Nginx + 2 replicas)

```bash
docker compose up -d --build
curl "http://localhost:8081/api/v1/multiply?a=6&b=7"
curl http://localhost:8081/actuator/health
docker compose down
```

## CI/CD

The `Jenkinsfile` runs: Checkout -> Build -> Test -> Docker Build -> Push -> Deploy,
triggered by a GitHub webhook, and sends an email on success/failure.
Docker Hub credentials are stored in the Jenkins Credentials store (ID `dockerhub-creds`).
