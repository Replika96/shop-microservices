-- Разрешаем NULL в user_email для поддержки анонимных гостей
ALTER TABLE cart_items ALTER COLUMN user_email DROP NOT NULL;

-- Добавляем guest_id если ещё не было (при переходе со старой схемы)
ALTER TABLE cart_items ADD COLUMN IF NOT EXISTS guest_id VARCHAR(255);
