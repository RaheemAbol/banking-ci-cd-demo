-- Run once in a new, empty demo database. CI gets a new database each run.

CREATE TABLE vendors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL
);

CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(40) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    vendor_id BIGINT NOT NULL,
    CONSTRAINT fk_product_vendor
        FOREIGN KEY (vendor_id) REFERENCES vendors(id)
);

-- Adds only the security table. Existing products and vendors remain unchanged.
CREATE TABLE IF NOT EXISTS app_users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    display_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- After registering admin@example.test through the application, an instructor
-- with database access can promote that specific demonstration account:
-- UPDATE app_users SET role = 'ADMIN' WHERE email = 'admin@example.test';
-- Log out and log in again after promotion. Never accept a role at registration.

-- Supplied extension: a user's cart consists of that user's cart_items rows.
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT uq_cart_product UNIQUE (user_id, product_id),
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES app_users(id),
    CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES products(id)
);
