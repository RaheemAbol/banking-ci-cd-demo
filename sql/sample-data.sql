-- Optional sample catalog for running the app locally. Run once after schema.sql.
INSERT INTO vendors (id, name, email) VALUES
    (1, 'Northline Office', 'northline@example.test'),
    (2, 'Brightwire Tech', 'brightwire@example.test'),
    (3, 'Trailside Supply', 'trailside@example.test');

INSERT INTO products (id, name, category, price, vendor_id) VALUES
    (101, 'Desk Lamp', 'OFFICE', 39.99, 1),
    (102, 'Notebook Set', 'OFFICE', 12.50, 1),
    (103, 'USB-C Hub', 'ELECTRONICS', 49.99, 2),
    (104, 'Wireless Mouse', 'ELECTRONICS', 24.99, 2);
