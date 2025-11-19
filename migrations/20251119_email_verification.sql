-- Добавление поля isActive в таблицу users (если его нет)
ALTER TABLE "user"
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT FALSE;

-- Создание таблицы для кодов верификации email
CREATE TABLE IF NOT EXISTS email_verification_codes
(
    id
    SERIAL
    PRIMARY
    KEY,
    user_id
    INTEGER
    NOT
    NULL
    REFERENCES
    "user"
(
    id
) ON DELETE CASCADE,
    code VARCHAR
(
    6
) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- Индекс для быстрого поиска кодов по user_id
CREATE INDEX IF NOT EXISTS idx_email_verification_codes_user_id ON email_verification_codes(user_id);

-- Индекс для автоматической очистки истекших кодов
CREATE INDEX IF NOT EXISTS idx_email_verification_codes_expires_at ON email_verification_codes(expires_at);

