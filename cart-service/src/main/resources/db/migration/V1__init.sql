CREATE TABLE IF NOT EXISTS cart_items
(
    id           BIGSERIAL PRIMARY KEY,
    user_email   VARCHAR(255),
    guest_id     VARCHAR(255),
    product_id   BIGINT         NOT NULL,
    product_name VARCHAR(255)   NOT NULL,
    price        NUMERIC(19, 2) NOT NULL,
    quantity     INTEGER        NOT NULL DEFAULT 1,
    image_url    VARCHAR(500)
);
