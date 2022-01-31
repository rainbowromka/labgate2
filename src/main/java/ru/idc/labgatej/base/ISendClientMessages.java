package ru.idc.labgatej.base;

/**
 * Интерфейс, определяющий отправку сообщений клиенту.
 */
public interface ISendClientMessages
{
    /**
     * Отправляем клиенту информацию о том, что драйвер запущен. Признаком того,
     * что драйвер запущен является, передачи основных объектов и вызов метода
     * loop.
     *
     * @param config
     *        конфигурация экземпляра драйвера.
     */
    void sendDriverIsRunning(IConfiguration config);

    /**
     * Отправляем клиенту информацию о том, что драйвер остановлен. Признаком
     * того, что драйвер остановлен является выход из основного цикла метода
     * loop.
     *
     * @param config
     *        конфигурация экземпляра драйвера.
     */
    void sendDriverIsStopped(IConfiguration config);
}
