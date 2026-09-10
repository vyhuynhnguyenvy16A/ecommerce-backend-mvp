DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'cart_items'
          AND column_name = 'variant_id'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'cart_items'
          AND column_name = 'product_variant_id'
    ) THEN
        ALTER TABLE cart_items
            RENAME COLUMN variant_id TO product_variant_id;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM pg_class
        WHERE relname = 'idx_cart_items_variant_id'
          AND relkind = 'i'
    ) AND NOT EXISTS (
        SELECT 1
        FROM pg_class
        WHERE relname = 'idx_cart_items_product_variant_id'
          AND relkind = 'i'
    ) THEN
        ALTER INDEX idx_cart_items_variant_id
            RENAME TO idx_cart_items_product_variant_id;
    END IF;
END $$;