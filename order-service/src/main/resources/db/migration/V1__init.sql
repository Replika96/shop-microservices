CREATE TABLE IF NOT EXISTS orders
(
    id          BIGSERIAL PRIMARY KEY,
    user_email  VARCHAR(255)   NOT NULL,
    total_price NUMERIC(19, 2) NOT NULL,
    status      VARCHAR(50)    NOT NULL DEFAULT 'PENDING',
    address     VARCHAR(500),
    created_at  TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS order_items
(
    id           BIGSERIAL PRIMARY KEY,
    order_id     BIGINT         NOT NULL,
    product_id   BIGINT         NOT NULL,
    product_name VARCHAR(255)   NOT NULL,
    price        NUMERIC(19, 2) NOT NULL,
    quantity     INTEGER        NOT NULL DEFAULT 1,
    image_url    VARCHAR(500),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id)
);
