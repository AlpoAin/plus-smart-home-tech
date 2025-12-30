CREATE SCHEMA IF NOT EXISTS payment;

CREATE TABLE IF NOT EXISTS payment.payments (
  payment_id UUID PRIMARY KEY,
  order_id UUID NOT NULL,
  total_payment DOUBLE PRECISION,
  delivery_total DOUBLE PRECISION,
  fee_total DOUBLE PRECISION,
  state VARCHAR NOT NULL
);