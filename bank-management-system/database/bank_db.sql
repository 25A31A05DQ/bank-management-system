-- =====================================================================
-- Mini Bank Management System - database script (MySQL 8.0.16+)
-- Run with:  mysql -u root -p < database/bank_db.sql
-- =====================================================================

CREATE DATABASE IF NOT EXISTS bank_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE bank_db;

-- ---------------------------------------------------------------------
-- 1. admin
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS admin (
    id            INT           NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)   NOT NULL,
    password_hash VARCHAR(255)  NOT NULL,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_admin_username UNIQUE (username)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 2. customers
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS customers (
    id             INT            NOT NULL AUTO_INCREMENT,
    account_number VARCHAR(20)    NOT NULL,
    name           VARCHAR(100)   NOT NULL,
    phone          VARCHAR(20)    NOT NULL,
    email          VARCHAR(100)   NOT NULL,
    address        VARCHAR(255)   NOT NULL,
    balance        DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_customers_account UNIQUE (account_number),
    CONSTRAINT uq_customers_email   UNIQUE (email),
    CONSTRAINT chk_customers_balance CHECK (balance >= 0)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 3. transactions
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS transactions (
    id               BIGINT         NOT NULL AUTO_INCREMENT,
    customer_id      INT            NOT NULL,
    type             ENUM('DEPOSIT', 'WITHDRAWAL') NOT NULL,
    amount           DECIMAL(15, 2) NOT NULL,
    transaction_date TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_transactions_customer FOREIGN KEY (customer_id)
        REFERENCES customers (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_transactions_amount CHECK (amount > 0),
    INDEX idx_transactions_date (transaction_date),
    INDEX idx_transactions_customer (customer_id)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Default admin account   username: admin   password: admin123
-- The password is stored as a salted PBKDF2-HMAC-SHA256 hash.
-- ---------------------------------------------------------------------
INSERT INTO admin (username, password_hash)
VALUES ('admin', 'pbkdf2$65536$ax8Mmk1+I1ihxOnwez1aYg==$PlxbuLGMKTDkkxGnrNzyJpatyHvP27wjxLNnfGb9Hvk=')
ON DUPLICATE KEY UPDATE username = username;
