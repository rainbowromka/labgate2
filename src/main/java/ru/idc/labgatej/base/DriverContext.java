package ru.idc.labgatej.base;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Контекст драйвера, удобно передавать в нем все необходимые синглтоны,
 * глобальные объекты и статусы, необходимые для работы драйвера.
 */
@AllArgsConstructor
@Data
public class DriverContext
{
    /**
     * Пул соединений с базой данных, он уже должен быть инициирован и настроен
     * для работы.
     */
    private ComboPooledDataSource connectionPool;

    /**
     * Конфигурация драйвера.
     */
    private IConfiguration config;

    /**
     * Признак того, что драйвер работает. Для его остановки достаточно
     * присвоить значение false. Драйвер будет остановлен сразу после выполнения
     * атомарной операции (например запись в базу данных результатов
     * исследований).
     */
    private AtomicBoolean running;

    /**
     * Сервис передачи данных клиенту.
     */
    private ISendClientMessages sendClientMessages;

    public DriverContext() {
    }
}
