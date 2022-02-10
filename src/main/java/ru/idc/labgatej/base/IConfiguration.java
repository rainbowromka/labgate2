package ru.idc.labgatej.base;

/**
 * Интерфейс описывающий получение доступа к параметрам объекта экземпляра
 * драйвера.
 */
public interface IConfiguration
{
    /**
     * Получает значение параметра объекта экземпляра драйвера по имени.
     *
     * @param param
     *        имя параметра.
     * @return значение параметра.
     */
    String getParamValue(String param);

    /**
     * Получает код драйвера.
     *
     * @return код драйвера.
     */
    String getDriverName();

    /**
     * Получает значение ID драйвера.
     *
     * @return ID драйвера.
     */
    Long getDriverId();

    /**
     * Устанавливает статус драйвера.
     * @param status
     *        статус драйвера.
     */
    void setStatus(
        DriverStatus status);

    /**
     * Получает имя экземпляра драйвера.
     *
     * @return имя экземпляра драйвера.
     */
    String getDriverInstanceName();
}
