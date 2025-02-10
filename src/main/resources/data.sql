-- Create tables if not exist
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS organizations (
    id TEXT PRIMARY KEY,
    country TEXT NOT NULL,
    created_date DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS ads (
    id TEXT PRIMARY KEY,
    amount INTEGER NOT NULL,
    price INTEGER NOT NULL,
    org_id TEXT NOT NULL,
    FOREIGN KEY (org_id) REFERENCES organizations(id)
);

CREATE TABLE IF NOT EXISTS fnancing_providers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    slug TEXT NOT NULL UNIQUE,
    payment_method TEXT NOT NULL,
    financing_percentage INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS offers (
    id TEXT PRIMARY KEY,
    payment_method TEXT NOT NULL,
    financing_privder INTEGER,
    amount INTEGER NOT NULL,
    accepted INTEGER DEFAULT 0,
    price INTEGER NOT NULL,
    FOREIGN KEY (financing_privder) REFERENCES fnancing_providers(id)
);

-- Insert initial data if not exists
INSERT OR IGNORE INTO organizations (id, country, created_date) VALUES
    ('org1', 'SPAIN', datetime('now', '-2 years')),
    ('org2', 'FRANCE', datetime('now', '-1 year')),
    ('org3', 'USA', datetime('now', '-3 years'));

INSERT OR IGNORE INTO fnancing_providers (slug, payment_method, financing_percentage) VALUES
    ('financing_bank', '100_in_unload', 5),
    ('financing_fintech', 'payment_in_shipping', 7); 