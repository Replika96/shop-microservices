CREATE TABLE IF NOT EXISTS product_images (
    id         BIGSERIAL PRIMARY KEY,
    product_id BIGINT       NOT NULL,
    url        VARCHAR(500) NOT NULL,
    sort_order INT          NOT NULL DEFAULT 0,
    CONSTRAINT fk_product_images_product
        FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_product_images_product_id ON product_images (product_id);

-- Перенести существующие одиночные фото в новую таблицу
INSERT INTO product_images (product_id, url, sort_order)
SELECT id, image_url, 0
FROM products
WHERE image_url IS NOT NULL AND image_url != '';
