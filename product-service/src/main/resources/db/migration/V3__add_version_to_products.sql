-- Колонка для оптимистичной блокировки (@Version в Hibernate)
ALTER TABLE products ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
