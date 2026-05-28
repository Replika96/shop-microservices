-- Добавляем total_price (его не было в старой плоской схеме)
ALTER TABLE orders ADD COLUMN IF NOT EXISTS total_price NUMERIC(19, 2) NOT NULL DEFAULT 0;

-- Удаляем старые плоские колонки если они ещё есть
ALTER TABLE orders DROP COLUMN IF EXISTS product_id;
ALTER TABLE orders DROP COLUMN IF EXISTS product_name;
ALTER TABLE orders DROP COLUMN IF EXISTS price;
ALTER TABLE orders DROP COLUMN IF EXISTS quantity;
ALTER TABLE orders DROP COLUMN IF EXISTS image_url;

-- Создаём таблицу позиций заказа
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
