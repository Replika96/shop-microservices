# 🛍️ Abakan Mangal Shop

Полноценный интернет-магазин на микросервисной архитектуре: бэкенд на Kotlin + Spring Boot, мобильное приложение на Android, веб-админка на React.

---

### Описание

Интернет-магазин с микросервисной архитектурой. Включает бэкенд, Android-приложение для покупателей и веб-панель для администраторов.

**Продакшн:** [api.dragonflex.ru](https://api.dragonflex.ru) / [admin.dragonflex.ru](https://admin.dragonflex.ru)

### Архитектура

```
Android App / Admin Panel
        │
        ▼ HTTPS
     nginx (80/443)
        │
        ▼
   API Gateway (:8080)
   ┌────┴────────────────────────────────┐
   │                                      │
user-service   product-service   order-service   cart-service   wishlist-service
   (:8081)        (:8084)          (:8082)         (:8085)         (:8086)
   │                │                │
   └────────────────┴────────────────┘
              PostgreSQL (:5432)
                    │
             notification-service (:8083)
                    │
                RabbitMQ (:5672)
```

### Стек технологий

| Слой | Технологии |
|---|---|
| Бэкенд | Kotlin, Spring Boot 3, Spring Security, Spring Cloud Gateway |
| База данных | PostgreSQL 16, Flyway (миграции) |
| Очереди | RabbitMQ |
| Хранение фото | Cloudinary |
| Аутентификация | JWT (access + refresh токены) |
| Мобильное приложение | Android, Kotlin, Jetpack Compose, Ktor, Hilt |
| Админка | React, TypeScript, Vite, TanStack Query, Tailwind CSS |
| Инфраструктура | Docker, Docker Compose, nginx, Let's Encrypt |
| CI/CD | GitHub Actions → VPS |

### Сервисы

| Сервис | Порт | Описание |
|---|---|---|
| api-gateway | 8080 | Точка входа, роутинг, JWT-фильтр |
| user-service | 8081 | Регистрация, авторизация, профиль, фото |
| order-service | 8082 | Заказы, статусы, история |
| notification-service | 8083 | Уведомления через RabbitMQ / WebSocket |
| product-service | 8084 | Каталог товаров, категории, загрузка фото |
| cart-service | 8085 | Корзина пользователя |
| wishlist-service | 8086 | Список желаний |

### Флоу заказа

```
1. Пользователь добавляет товары в корзину (cart-service)
2. Оформляет заказ → order-service создаёт запись (PENDING)
3. RabbitMQ → product-service уменьшает остаток
4. RabbitMQ → notification-service уведомляет пользователя
5. Администратор меняет статус через админку
   PENDING → CONFIRMED → SHIPPED → DELIVERED
```

### Быстрый старт (локально)

**Требования:** Docker, Docker Compose

```bash
git clone https://github.com/Replika96/shop.git
cd shop

# Создать .env файл
cp .env.example .env
# Заполнить переменные в .env

docker compose up --build
```

| Сервис | URL |
|---|---|
| API Gateway | http://localhost:8080 |
| RabbitMQ Management | http://localhost:15672 |
| Админка | http://localhost:8090 |

### Переменные окружения (.env)

```env
DB_PASSWORD=           # Пароль PostgreSQL
JWT_SECRET=            # Секрет для JWT (минимум 32 символа)
CLOUDINARY_CLOUD_NAME= # Cloudinary cloud name
CLOUDINARY_API_KEY=    # Cloudinary API key
CLOUDINARY_API_SECRET= # Cloudinary API secret
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
```

### Деплой на VPS

```bash
# 1. Клонировать репозиторий
git clone https://github.com/Replika96/shop.git
cd shop

# 2. Создать .env с реальными значениями

# 3. Запустить сервисы
docker compose up -d

# 4. Получить SSL-сертификаты (один раз)
bash init-letsencrypt.sh your@email.com
```

### GitHub Actions Secrets

| Секрет | Описание |
|---|---|
| `VPS_HOST` | IP-адрес сервера |
| `VPS_USER` | SSH пользователь |
| `VPS_SSH_KEY` | Приватный SSH ключ |
| `CR_PAT` | GitHub Container Registry токен |

### API — основные эндпоинты

```
POST   /api/auth/register       Регистрация
POST   /api/auth/login          Вход, получение JWT
POST   /api/auth/refresh        Обновление токена

GET    /api/products            Список товаров (пагинация, фильтры)
GET    /api/products/{id}       Товар по ID
POST   /api/products            Создать товар (ADMIN)
PATCH  /api/products/{id}       Обновить товар (ADMIN)
DELETE /api/products/{id}       Удалить товар (ADMIN)

GET    /api/orders/my           Мои заказы
POST   /api/orders              Создать заказ
PATCH  /api/orders/{id}/status  Изменить статус (ADMIN)

GET    /api/cart                Корзина
POST   /api/cart                Добавить товар
DELETE /api/cart/{id}           Удалить из корзины

GET    /api/wishlist            Список желаний
POST   /api/wishlist            Добавить товар
DELETE /api/wishlist/{id}       Удалить из списка
```

---

## 🇬🇧 English

### Overview

A full-stack e-commerce application built with microservices architecture. Includes a Kotlin/Spring Boot backend, Android app for customers, and a React admin panel.

**Production:** [api.dragonflex.ru](https://api.dragonflex.ru) / [admin.dragonflex.ru](https://admin.dragonflex.ru)

### Architecture

```
Android App / Admin Panel
        │
        ▼ HTTPS
     nginx (80/443)
        │
        ▼
   API Gateway (:8080)
   ┌────┴────────────────────────────────┐
   │                                      │
user-service   product-service   order-service   cart-service   wishlist-service
   (:8081)        (:8084)          (:8082)         (:8085)         (:8086)
   │                │                │
   └────────────────┴────────────────┘
              PostgreSQL (:5432)
                    │
             notification-service (:8083)
                    │
                RabbitMQ (:5672)
```

### Tech Stack

| Layer | Technologies |
|---|---|
| Backend | Kotlin, Spring Boot 3, Spring Security, Spring Cloud Gateway |
| Database | PostgreSQL 16, Flyway migrations |
| Messaging | RabbitMQ |
| Image Storage | Cloudinary |
| Auth | JWT (access + refresh tokens) |
| Mobile | Android, Kotlin, Jetpack Compose, Ktor, Hilt |
| Admin Panel | React, TypeScript, Vite, TanStack Query, Tailwind CSS |
| Infrastructure | Docker, Docker Compose, nginx, Let's Encrypt |
| CI/CD | GitHub Actions → VPS |

### Services

| Service | Port | Description |
|---|---|---|
| api-gateway | 8080 | Entry point, routing, JWT filter |
| user-service | 8081 | Registration, auth, profile, photo upload |
| order-service | 8082 | Orders, statuses, history |
| notification-service | 8083 | RabbitMQ consumer / WebSocket notifications |
| product-service | 8084 | Product catalog, categories, image upload |
| cart-service | 8085 | Shopping cart |
| wishlist-service | 8086 | Wishlist |

### Order Flow

```
1. User adds items to cart (cart-service)
2. User places order → order-service creates record (PENDING)
3. RabbitMQ → product-service decreases stock
4. RabbitMQ → notification-service notifies user
5. Admin changes status via admin panel
   PENDING → CONFIRMED → SHIPPED → DELIVERED
```

### Quick Start (local)

**Requirements:** Docker, Docker Compose

```bash
git clone https://github.com/Replika96/shop.git
cd shop

# Create .env file
cp .env.example .env
# Fill in environment variables

docker compose up --build
```

| Service | URL |
|---|---|
| API Gateway | http://localhost:8080 |
| RabbitMQ Management | http://localhost:15672 |
| Admin Panel | http://localhost:8090 |

### Environment Variables (.env)

```env
DB_PASSWORD=           # PostgreSQL password
JWT_SECRET=            # JWT secret (min 32 chars)
CLOUDINARY_CLOUD_NAME= # Cloudinary cloud name
CLOUDINARY_API_KEY=    # Cloudinary API key
CLOUDINARY_API_SECRET= # Cloudinary API secret
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
```

### VPS Deployment

```bash
# 1. Clone repository
git clone https://github.com/Replika96/shop.git
cd shop

# 2. Create .env with real values

# 3. Start services
docker compose up -d

# 4. Obtain SSL certificates (once)
bash init-letsencrypt.sh your@email.com
```

### GitHub Actions Secrets

| Secret | Description |
|---|---|
| `VPS_HOST` | Server IP address |
| `VPS_USER` | SSH username |
| `VPS_SSH_KEY` | Private SSH key |
| `CR_PAT` | GitHub Container Registry token |

### API Endpoints

```
POST   /api/auth/register       Register new user
POST   /api/auth/login          Login, get JWT token
POST   /api/auth/refresh        Refresh token

GET    /api/products            Product list (pagination, filters)
GET    /api/products/{id}       Get product by ID
POST   /api/products            Create product (ADMIN)
PATCH  /api/products/{id}       Update product (ADMIN)
DELETE /api/products/{id}       Delete product (ADMIN)

GET    /api/orders/my           My orders
POST   /api/orders              Create order
PATCH  /api/orders/{id}/status  Update status (ADMIN)

GET    /api/cart                Get cart
POST   /api/cart                Add item to cart
DELETE /api/cart/{id}           Remove from cart

GET    /api/wishlist            Get wishlist
POST   /api/wishlist            Add to wishlist
DELETE /api/wishlist/{id}       Remove from wishlist
```

### License

MIT
