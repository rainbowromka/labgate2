package ru.idc.labgatej.manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Создает пул асинхронных задач. Включает асинхронный запуск.
 */
@Configuration
@EnableAsync
public class DriversAsyncConfig
{
    /**
     * Создает и конфигурирует объект, управляющий пулом потоков.
     *
     * @return объект, управляющий пулом потоков.
     */
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(200);
        executor.setQueueCapacity(10);
        executor.setKeepAliveSeconds(20);
        executor.setThreadNamePrefix("DriverThread-");
        executor.initialize();
        return executor;
    }
}
