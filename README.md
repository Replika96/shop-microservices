# Shop — Microservices Lab Project

Kotlin + Spring Boot microservices: internet shop with RabbitMQ, WebSockets, Swagger, Docker, CI/CD.

## Architecture

```
Client → API Gateway (8080)
              ├── user-service    (8081)  — Auth, JWT
              ├── order-service   (8082)  — Orders, REST, Swagger
              └── notification-service (8083) — RabbitMQ consumer, WebSocket
                        ↑
                   RabbitMQ (5672)
PostgreSQL (5432) ←─ user-service, order-service
```

## Quick Start (local)

```bash
docker compose up --build
```

| Service | URL |
|---|---|
| API Gateway | http://localhost:8080 |
| User Service Swagger | http://localhost:8081/swagger-ui.html |
| Order Service Swagger | http://localhost:8082/swagger-ui.html |
| RabbitMQ Management | http://localhost:15672 (guest/guest) |

## WebSocket (STOMP)

Connect to: `ws://localhost:8083/ws`

Subscribe to order updates:
- `/topic/orders/{orderId}` — updates for specific order
- `/topic/users/{email}/orders` — all orders for user

## API Flow

1. `POST /api/auth/register` — create account
2. `POST /api/auth/login` — get JWT token
3. `POST /api/orders` (header: `X-User-Email: you@mail.com`) — create order
4. WebSocket client receives status update in real time
5. `PATCH /api/orders/{id}/status` — change status → triggers WebSocket push

## GitHub Actions Secrets Required

| Secret | Description |
|---|---|
| `DOCKERHUB_USERNAME` | Docker Hub login |
| `DOCKERHUB_TOKEN` | Docker Hub access token |
| `VPS_HOST` | Server IP |
| `VPS_USER` | SSH user |
| `VPS_SSH_KEY` | Private SSH key |

## Deploy on Linux VPS

```bash
# First time setup
sudo apt update && sudo apt install -y docker.io docker-compose-v2
mkdir ~/shop && cd ~/shop
# Copy docker-compose.yml to server, then:
docker compose up -d
```
