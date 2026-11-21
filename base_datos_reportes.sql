CREATE DATABASE IF NOT EXISTS banco_academico CHARACTER SET utf8mb4;
USE banco_academico;

CREATE TABLE IF NOT EXISTS clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL,
    apellido VARCHAR(60) NOT NULL,
    dui VARCHAR(10) UNIQUE,
    telefono VARCHAR(15),
    correo VARCHAR(80)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS cuentas (
    id_cuenta INT AUTO_INCREMENT PRIMARY KEY,
    numero_cuenta VARCHAR(20) UNIQUE NOT NULL,
    id_cliente INT NOT NULL,
    saldo DECIMAL(12,2) NOT NULL DEFAULT 0,
    estado ENUM('ACTIVA','BLOQUEADA') NOT NULL DEFAULT 'ACTIVA',
    CONSTRAINT fk_cuentas_clientes
        FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS transacciones (
    id_transaccion INT AUTO_INCREMENT PRIMARY KEY,
    id_cuenta INT NOT NULL,
    tipo ENUM(
        'DEPOSITO',
        'RETIRO',
        'TRANSFERENCIA_ENVIADA',
        'TRANSFERENCIA_RECIBIDA'
    ) NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    cuenta_relacionada INT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transacciones_cuenta
        FOREIGN KEY (id_cuenta) REFERENCES cuentas(id_cuenta),
    CONSTRAINT fk_transacciones_cuenta_relacionada
        FOREIGN KEY (cuenta_relacionada) REFERENCES cuentas(id_cuenta)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO clientes (nombre, apellido, dui, telefono, correo)
VALUES
    ('Ana', 'Martínez', '01234567-1', '7000-1001', 'ana.martinez@example.com'),
    ('Carlos', 'Hernández', '02345678-2', '7000-1002', 'carlos.hernandez@example.com'),
    ('Sofía', 'López', '03456789-3', '7000-1003', 'sofia.lopez@example.com'),
    ('Diego', 'Ramírez', '04567890-4', '7000-1004', 'diego.ramirez@example.com')
ON DUPLICATE KEY UPDATE id_cliente = id_cliente;

INSERT INTO cuentas (numero_cuenta, id_cliente, saldo, estado)
VALUES (
    'CTA-001',
    (SELECT id_cliente FROM clientes WHERE dui = '01234567-1'),
    1800.00,
    'ACTIVA'
)
ON DUPLICATE KEY UPDATE id_cuenta = id_cuenta;

INSERT INTO cuentas (numero_cuenta, id_cliente, saldo, estado)
VALUES (
    'CTA-002',
    (SELECT id_cliente FROM clientes WHERE dui = '02345678-2'),
    2450.50,
    'ACTIVA'
)
ON DUPLICATE KEY UPDATE id_cuenta = id_cuenta;

INSERT INTO cuentas (numero_cuenta, id_cliente, saldo, estado)
VALUES (
    'CTA-003',
    (SELECT id_cliente FROM clientes WHERE dui = '03456789-3'),
    975.25,
    'ACTIVA'
)
ON DUPLICATE KEY UPDATE id_cuenta = id_cuenta;

INSERT INTO cuentas (numero_cuenta, id_cliente, saldo, estado)
VALUES (
    'CTA-004',
    (SELECT id_cliente FROM clientes WHERE dui = '04567890-4'),
    120.00,
    'BLOQUEADA'
)
ON DUPLICATE KEY UPDATE id_cuenta = id_cuenta;

INSERT INTO cuentas (numero_cuenta, id_cliente, saldo, estado)
VALUES (
    'CTA-005',
    (SELECT id_cliente FROM clientes WHERE dui = '01234567-1'),
    50.00,
    'BLOQUEADA'
)
ON DUPLICATE KEY UPDATE id_cuenta = id_cuenta;

INSERT INTO transacciones (id_cuenta, tipo, monto, cuenta_relacionada, fecha)
SELECT c.id_cuenta, 'DEPOSITO', 1000.00, NULL, '2026-11-01 09:00:00'
FROM cuentas c
WHERE c.numero_cuenta = 'CTA-001'
  AND NOT EXISTS (
      SELECT 1
      FROM transacciones t
      WHERE t.id_cuenta = c.id_cuenta
        AND t.tipo = 'DEPOSITO'
        AND t.monto = 1000.00
        AND t.cuenta_relacionada IS NULL
        AND t.fecha = '2026-11-01 09:00:00'
  );

INSERT INTO transacciones (id_cuenta, tipo, monto, cuenta_relacionada, fecha)
SELECT c.id_cuenta, 'DEPOSITO', 500.00, NULL, '2026-11-02 10:00:00'
FROM cuentas c
WHERE c.numero_cuenta = 'CTA-002'
  AND NOT EXISTS (
      SELECT 1
      FROM transacciones t
      WHERE t.id_cuenta = c.id_cuenta
        AND t.tipo = 'DEPOSITO'
        AND t.monto = 500.00
        AND t.cuenta_relacionada IS NULL
        AND t.fecha = '2026-11-02 10:00:00'
  );

INSERT INTO transacciones (id_cuenta, tipo, monto, cuenta_relacionada, fecha)
SELECT c.id_cuenta, 'DEPOSITO', 800.00, NULL, '2026-11-03 11:00:00'
FROM cuentas c
WHERE c.numero_cuenta = 'CTA-003'
  AND NOT EXISTS (
      SELECT 1
      FROM transacciones t
      WHERE t.id_cuenta = c.id_cuenta
        AND t.tipo = 'DEPOSITO'
        AND t.monto = 800.00
        AND t.cuenta_relacionada IS NULL
        AND t.fecha = '2026-11-03 11:00:00'
  );

INSERT INTO transacciones (id_cuenta, tipo, monto, cuenta_relacionada, fecha)
SELECT c.id_cuenta, 'RETIRO', 200.00, NULL, '2026-11-04 12:00:00'
FROM cuentas c
WHERE c.numero_cuenta = 'CTA-001'
  AND NOT EXISTS (
      SELECT 1
      FROM transacciones t
      WHERE t.id_cuenta = c.id_cuenta
        AND t.tipo = 'RETIRO'
        AND t.monto = 200.00
        AND t.cuenta_relacionada IS NULL
        AND t.fecha = '2026-11-04 12:00:00'
  );

INSERT INTO transacciones (id_cuenta, tipo, monto, cuenta_relacionada, fecha)
SELECT c.id_cuenta, 'RETIRO', 100.00, NULL, '2026-11-05 13:00:00'
FROM cuentas c
WHERE c.numero_cuenta = 'CTA-002'
  AND NOT EXISTS (
      SELECT 1
      FROM transacciones t
      WHERE t.id_cuenta = c.id_cuenta
        AND t.tipo = 'RETIRO'
        AND t.monto = 100.00
        AND t.cuenta_relacionada IS NULL
        AND t.fecha = '2026-11-05 13:00:00'
  );

INSERT INTO transacciones (id_cuenta, tipo, monto, cuenta_relacionada, fecha)
SELECT origen.id_cuenta, 'TRANSFERENCIA_ENVIADA', 300.00, destino.id_cuenta,
       '2026-11-06 14:00:00'
FROM cuentas origen
INNER JOIN cuentas destino ON destino.numero_cuenta = 'CTA-002'
WHERE origen.numero_cuenta = 'CTA-001'
  AND NOT EXISTS (
      SELECT 1
      FROM transacciones t
      WHERE t.id_cuenta = origen.id_cuenta
        AND t.tipo = 'TRANSFERENCIA_ENVIADA'
        AND t.monto = 300.00
        AND t.cuenta_relacionada = destino.id_cuenta
        AND t.fecha = '2026-11-06 14:00:00'
  );

INSERT INTO transacciones (id_cuenta, tipo, monto, cuenta_relacionada, fecha)
SELECT destino.id_cuenta, 'TRANSFERENCIA_RECIBIDA', 300.00, origen.id_cuenta,
       '2026-11-06 14:00:00'
FROM cuentas origen
INNER JOIN cuentas destino ON destino.numero_cuenta = 'CTA-002'
WHERE origen.numero_cuenta = 'CTA-001'
  AND NOT EXISTS (
      SELECT 1
      FROM transacciones t
      WHERE t.id_cuenta = destino.id_cuenta
        AND t.tipo = 'TRANSFERENCIA_RECIBIDA'
        AND t.monto = 300.00
        AND t.cuenta_relacionada = origen.id_cuenta
        AND t.fecha = '2026-11-06 14:00:00'
  );
