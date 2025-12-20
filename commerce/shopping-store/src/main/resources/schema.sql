CREATE SCHEMA IF NOT EXISTS shopping_store;

CREATE TABLE IF NOT EXISTS shopping_store.products (
  product_id UUID PRIMARY KEY,
  product_name VARCHAR NOT NULL,
  description TEXT NOT NULL,
  image_src VARCHAR,
  quantity_state VARCHAR NOT NULL,
  product_state VARCHAR NOT NULL,
  product_category VARCHAR NOT NULL,
  price NUMERIC(12,2) NOT NULL
);

INSERT INTO shopping_store.products
(product_id, product_name, description, image_src, quantity_state, product_state, product_category, price)
VALUES
('00000000-0000-0000-0000-000000000001', 'Alpha', 'Test product Alpha', null, 'ENOUGH', 'ACTIVE', 'CONTROL', 1000.00),
('00000000-0000-0000-0000-000000000002', 'Beta',  'Test product Beta',  null, 'ENOUGH', 'ACTIVE', 'CONTROL', 900.00),
('00000000-0000-0000-0000-000000000003', 'Gamma', 'Test product Gamma', null, 'ENOUGH', 'ACTIVE', 'CONTROL', 1100.00);