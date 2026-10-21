-- 1. Drop the old Hibernate-generated check constraint (brand between 0 and 3)
ALTER TABLE vault_card
DROP CONSTRAINT IF EXISTS vault_card_brand_check;

-- 2. Convert integer ordinal to string
ALTER TABLE vault_card
ALTER COLUMN brand TYPE VARCHAR(20)
    USING CASE brand
        WHEN 0 THEN 'VISA'
        WHEN 1 THEN 'MASTERCARD'
        WHEN 2 THEN 'RUPAY'
        WHEN 3 THEN 'AMEX'
END;

-- 3. Add the new check constraint
ALTER TABLE vault_card
    ADD CONSTRAINT chk_vault_card_brand
        CHECK (brand IN ('VISA', 'MASTERCARD', 'RUPAY', 'AMEX'));