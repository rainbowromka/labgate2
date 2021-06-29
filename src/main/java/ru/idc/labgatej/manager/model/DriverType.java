package ru.idc.labgatej.manager.model;

/**
 * Тип драйвера.
 */
public enum DriverType
{
    /**
     * Соединение по COM-порту.
     */
    COM,

    /**
     * Соединение по TCP-IP протоколу.
     */
    SOCKET,

    /**
     * Подклюсение по COM-порту через сетевой интерфейс.
     */
    TTY
}
