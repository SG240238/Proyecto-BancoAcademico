CREATE DATABASE IF NOT EXISTS banco_academico_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE banco_academico_db;

CREATE TABLE IF NOT EXISTS clientes (
    id_cliente INT NOT NULL AUTO_INCREMENT,
    codigo_cliente VARCHAR(20) NOT NULL,
    dui VARCHAR(10) NOT NULL,
    nombre_completo VARCHAR(120) NOT NULL,
    telefono VARCHAR(8) NOT NULL,
    correo_electronico VARCHAR(100) NULL,
    direccion VARCHAR(255) NOT NULL,
    estado ENUM('Activo', 'Inactivo', 'Bloqueado')
        NOT NULL DEFAULT 'Activo',
    fecha_registro TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_clientes
        PRIMARY KEY (id_cliente),

    CONSTRAINT uq_codigo_cliente
        UNIQUE (codigo_cliente),

    CONSTRAINT uq_dui
        UNIQUE (dui),

    CONSTRAINT uq_correo
        UNIQUE (correo_electronico)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS cuentas (
    id_cuenta INT NOT NULL AUTO_INCREMENT,
    numero_cuenta VARCHAR(20) NOT NULL,
    id_cliente INT NOT NULL,
    saldo DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    estado ENUM('ACTIVA', 'BLOQUEADA', 'INACTIVA')
        NOT NULL DEFAULT 'ACTIVA',

    CONSTRAINT pk_cuentas
        PRIMARY KEY (id_cuenta),

    CONSTRAINT uq_numero_cuenta
        UNIQUE (numero_cuenta),

    CONSTRAINT fk_cuentas_clientes
        FOREIGN KEY (id_cliente)
        REFERENCES clientes (id_cliente)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS transacciones (
    id_transaccion INT NOT NULL AUTO_INCREMENT,
    id_cuenta INT NOT NULL,
    tipo ENUM(
        'DEPOSITO',
        'RETIRO',
        'TRANSFERENCIA_ENVIADA',
        'TRANSFERENCIA_RECIBIDA'
    ) NOT NULL,
    monto DECIMAL(12, 2) NOT NULL,
    cuenta_relacionada INT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_transacciones
        PRIMARY KEY (id_transaccion),

    CONSTRAINT fk_transacciones_cuentas
        FOREIGN KEY (id_cuenta)
        REFERENCES cuentas (id_cuenta)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_transacciones_relacionada
        FOREIGN KEY (cuenta_relacionada)
        REFERENCES cuentas (id_cuenta)
        ON UPDATE CASCADE
        ON DELETE SET NULL
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

DESCRIBE clientes;
DESCRIBE cuentas;
DESCRIBE transacciones;

SELECT * FROM clientes;
SELECT * FROM cuentas;
SELECT * FROM transacciones;
