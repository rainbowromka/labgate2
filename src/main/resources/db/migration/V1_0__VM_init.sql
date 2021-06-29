CREATE SCHEMA IF NOT EXISTS driver;
/**
 * Необходимо что бы заработал Hibernate.
 */
CREATE SEQUENCE driver.hibernate_sequence;

CREATE SEQUENCE driver.driver_entity_sequence START WITH 1;

CREATE TABLE driver.entity (
    /**
     * ID события.
     */
    id BIGINT PRIMARY KEY,

    /**
     * Имя драйвера
     */
    name VARCHAR,

    /**
     * Имя драйвера
     */
    code VARCHAR,

    /**
     * Тип драйвера.
     */
    type VARCHAR,

    /**
     * Статус драйвера.
     */
    status VARCHAR
);

CREATE SEQUENCE driver.driver_parameter_sequence START WITH 1;

CREATE TABLE driver.parameters (
    /**
     * Идентификатор параметра.
     */
    id BIGINT PRIMARY KEY,

    /**
     * Имя параметра.
     */
    name VARCHAR NOT NULL,

    /**
     * Значение параметра.
     */
    value VARCHAR,

    /**
     * Роль.
     */
    driver_entity_id BIGINT
);
--
--
