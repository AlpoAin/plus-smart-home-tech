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
