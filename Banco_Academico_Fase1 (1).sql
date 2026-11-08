-- =========================================================
-- BANCO ACADEMICO UDB
-- FASE 1: BASE DE DATOS Y TABLA DE CLIENTES
-- Motor: MySQL / InnoDB
-- Codificacion: utf8mb4
--
-- Este archivo crea la base de datos y la tabla clientes.
-- Ejecutar este script una sola vez en MySQL Workbench.
-- No elimina datos existentes.
-- =========================================================

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

-- =========================================================
-- VERIFICACION DE LA ESTRUCTURA
-- =========================================================
DESCRIBE clientes;

-- =========================================================
-- CONSULTA DE CLIENTES
-- =========================================================
SELECT * FROM clientes;

-- =========================================================
-- DATOS DE EJEMPLO (OPCIONAL)
-- Ejecutar este bloque solo si desean cargar los 3 ejemplos.
-- Si ya existen, no volver a ejecutarlo porque las columnas
-- unicas impedirán duplicarlos.
-- =========================================================
-- INSERT INTO clientes
-- (codigo_cliente, dui, nombre_completo, telefono,
--  correo_electronico, direccion, estado)
-- VALUES
-- ('CLI-0001', '12345678-9', 'Carlos Alberto Lopez',
--  '76543210', 'carlos@gmail.com',
--  'Sonsonate, El Salvador', 'Activo'),
-- ('CLI-0002', '98765432-1', 'Maria Fernanda Garcia',
--  '71234567', 'maria@gmail.com',
--  'Izalco, Sonsonate', 'Activo'),
-- ('CLI-0003', '11223344-5', 'Jose Manuel Perez',
--  '78901234', 'jose@gmail.com',
--  'Santa Ana, El Salvador', 'Inactivo');

-- =========================================================
-- FIN DEL SCRIPT
-- =========================================================
