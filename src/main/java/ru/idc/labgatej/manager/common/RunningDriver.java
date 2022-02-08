package ru.idc.labgatej.manager.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.idc.labgatej.Manager;
import ru.idc.labgatej.base.DriverStatus;
import ru.idc.labgatej.manager.model.DriverEntity;

/**
 * Объект запущенного экземпляра драйвера.
 *
 * @author Roman Perminov.
 */
@AllArgsConstructor
@Data
public class RunningDriver
{
    /**
     * Экземпляр менеджера драйвера.
     */
    private Manager driverManager;

    /**
     * Конфигурация экземпляра драйвера.
     */
    private DriverEntity driverEntity;

    /**
     * Получает статус работы драйвера.
     *
     * @return статус работы драйвера.
     */
    public DriverStatus getStatus()
    {
        return driverEntity.getStatus();
    }

    /**
     * Устанавливает статус работы драйвера.
     *
     * @param status
     *        статус работы драйвера.
     */
    public void setStatus(DriverStatus status)
    {
        driverEntity.setStatus(status);
    }
}
