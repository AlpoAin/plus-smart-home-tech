CREATE SCHEMA IF NOT EXISTS shopping_cart;

CREATE TABLE IF NOT EXISTS shopping_cart.carts (
  cart_id UUID PRIMARY KEY,
  username VARCHAR NOT NULL,
  state VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS shopping_cart.cart_products (
  cart_id UUID NOT NULL REFERENCES shopping_cart.carts(cart_id),
  product_id UUID NOT NULL,
  quantity BIGINT NOT NULL,
  PRIMARY KEY (cart_id, product_id)
);
