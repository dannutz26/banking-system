CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    source_iban VARCHAR(34),
    target_iban VARCHAR(34),
    amount NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    description TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    account_id BIGINT REFERENCES accounts(id) ON DELETE CASCADE
);

CREATE INDEX idx_transaction_account_id ON transactions(account_id);
CREATE INDEX idx_transaction_timestamp ON transactions(timestamp DESC);