###DOCKER COMPOSE ###

services:

  postgres:
    image: postgres:16
    container_name: flashbuy-postgres

    environment:
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin123
      POSTGRES_DB: flashbuy

    ports:
      - "5432:5432"

    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:

###SCRIPTS TABLAS####

CREATE TABLE product (
    product_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(10,2) NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE inventory (
    inventory_id BIGSERIAL PRIMARY KEY,
    product_id BIGINT UNIQUE NOT NULL,
    available_stock INT NOT NULL,
    reserved_stock INT DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_inventory_product
    FOREIGN KEY (product_id)
    REFERENCES product(product_id)
);

CREATE TABLE orders (
    order_id BIGSERIAL PRIMARY KEY,
    customer_identifier VARCHAR(255),
    order_status VARCHAR(50),
    total_amount NUMERIC(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_item (
    order_item_id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL,

    CONSTRAINT fk_order_item_order
    FOREIGN KEY (order_id)
    REFERENCES orders(order_id),

    CONSTRAINT fk_order_item_product
    FOREIGN KEY (product_id)
    REFERENCES product(product_id)
);

CREATE TABLE payment (
    payment_id BIGSERIAL PRIMARY KEY,
    order_id BIGINT UNIQUE NOT NULL,
    amount NUMERIC(10,2),
    payment_status VARCHAR(50),
    transaction_reference VARCHAR(255),
    paid_at TIMESTAMP,

    CONSTRAINT fk_payment_order
    FOREIGN KEY (order_id)
    REFERENCES orders(order_id)
);

#### SCRIPT INSERTAR DATOS DE PRUEBA ###

INSERT INTO product (
    name,
    description,
    price
)
VALUES
(
    'iPhone 16 Pro',
    'Flash sale product',
    1299.99
);

INSERT INTO inventory (
    product_id,
    available_stock
)
VALUES
(
    1,
    100
);