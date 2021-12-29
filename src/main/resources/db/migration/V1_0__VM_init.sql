CREATE SCHEMA IF NOT EXISTS driver;
/**
 * Необходимо что бы заработал Hibernate.
 */
CREATE SEQUENCE driver.hibernate_sequence;

/**
 * Сущность драйвера.
 */
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

/**
 * Сущность параметров драйвера.
 */
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

/**
 * Сущность роли.
 */
CREATE SEQUENCE driver.driver_role_sequence START WITH 1;
CREATE TABLE driver.roles (
  /**
   * Идентификатор роли.
   */
  id BIGINT PRIMARY KEY DEFAULT nextval('driver.driver_role_sequence'),

  /**
   * Имя роли.
   */
  name VARCHAR NOT NULL
);

/**
 * Сущность пользователя.
 */
CREATE SEQUENCE driver.driver_user_sequence START WITH 1;
CREATE TABLE driver.users (
  /**
   * Идентификатор пользователя.
   */
  id BIGINT PRIMARY KEY,

  /**
   * e-mail пользователя.
   */
  email VARCHAR(50) UNIQUE DEFAULT NULL,

  /**
   * Пароль пользователя.
   */
  password VARCHAR(120) DEFAULT NULL,

  /**
   * Имя пользователя
   */
  username VARCHAR(20) UNIQUE DEFAULT NULL
);

/**
 * Связь многие ко многим для сущностей роль - пользователь.
 */
-- CREATE SEQUENCE driver.driver_user_sequence START WITH 1;
CREATE TABLE driver.user_roles (
  /**
   * Идентификатор пользователя.
   */
  user_id BIGINT NOT NULL,

  /**
   * Идентификатор роли.
   */
  role_id BIGINT NOT NULL,
  PRIMARY KEY(user_id, role_id)
);
-- Необходимо добавить рли в базу данных.
INSERT INTO driver.roles(name) VALUES('ROLE_USER');
INSERT INTO driver.roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO driver.roles(name) VALUES('ROLE_ADMIN');