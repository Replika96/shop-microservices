-- Разрешаем NULL в user_email для поддержки анонимных гостей
ALTER TABLE wishlist_items ALTER COLUMN user_email DROP NOT NULL;

-- Добавляем guest_id если ещё не было (при переходе со старой схемы)
ALTER TABLE wishlist_items ADD COLUMN IF NOT EXISTS guest_id VARCHAR(255);

-- Удаляем старый уникальный индекс (был на userEmail + productId, несовместим с nullable)
DO
$$
    DECLARE
        v_constraint TEXT;
    BEGIN
        SELECT constraint_name
        INTO v_constraint
        FROM information_schema.table_constraints
        WHERE table_name = 'wishlist_items'
          AND constraint_type = 'UNIQUE'
        LIMIT 1;

        IF v_constraint IS NOT NULL THEN
            EXECUTE format('ALTER TABLE wishlist_items DROP CONSTRAINT %I', v_constraint);
        END IF;
    END;
$$;
