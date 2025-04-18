DELETE FROM card_block_reasons;
DELETE FROM transactions;
DELETE FROM limits;
DELETE FROM cards;
DELETE FROM users;

-- Добавляем тестовые данные
INSERT INTO users (id, email, password, role, username)
VALUES (1, 'user1@example.com', 'password1', 'USER', 'user1');

INSERT INTO users (id, email, password, role, username)
VALUES (2, 'user2@example.com', 'password2', 'USER', 'user2');

INSERT INTO cards (id, card_number, user_id, expiry_date, status, balance)
VALUES (1, '1234-5678-9012-3456', 1, '2025-12-31', 'ACTIVE', 1000.00);

INSERT INTO cards (id, card_number, user_id, expiry_date, status, balance)
VALUES (2, '9876-5432-1098-7654', 2, '2025-12-31', 'ACTIVE', 500.00);

INSERT INTO limits (id, card_id, daily_limit, monthly_limit)
VALUES (1, 1, 2000.00, 10000.00);

INSERT INTO limits (id, card_id, daily_limit, monthly_limit)
VALUES (2, 2, 1500.00, 8000.00);
