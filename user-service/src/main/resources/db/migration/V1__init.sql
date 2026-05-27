CREATE TABLE IF NOT EXISTS users
(
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    surname    VARCHAR(255),
    patronymic VARCHAR(255),
    phone      VARCHAR(255),
    city       VARCHAR(255),
    region     VARCHAR(255),
    street     VARCHAR(255),
    zip_code   VARCHAR(255),
    role       VARCHAR(50)  NOT NULL DEFAULT 'USER',
    CONSTRAINT uk_users_email UNIQUE (email)
);
