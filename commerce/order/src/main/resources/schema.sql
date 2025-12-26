CREATE SCHEMA IF NOT EXISTS orders;

CREATE TABLE IF NOT EXISTS orders.orders (
  order_id UUID PRIMARY KEY,
  shopping_cart_id UUID,
  payment_id UUID,
  delivery_id UUID,
  state VARCHAR NOT NULL,
  delivery_weight DOUBLE PRECISION,
  delivery_volume DOUBLE PRECISION,
  fragile BOOLEAN,
  total_price DOUBLE PRECISION,
  delivery_price DOUBLE PRECISION,
  product_price DOUBLE PRECISION,
  username VARCHAR
);

CREATE TABLE IF NOT EXISTS orders.order_products (
  order_id UUID NOT NULL REFERENCES orders.orders(order_id),
  product_id UUID NOT NULL,
  quantity BIGINT NOT NULL,
  PRIMARY KEY (order_id, product_id)
);