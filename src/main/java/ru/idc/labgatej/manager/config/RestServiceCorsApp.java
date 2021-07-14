package ru.idc.labgatej.manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @author Roman Perminov
 * Настраиваем CORS. Если его не добавить. То многие запросы могут не пройти.
 * Т.к. spring проверяет от кого пришел запрос.
 */
@Configuration
public class RestServiceCorsApp
{
    /**
     * Возвращает концигуратор CORS.
     * @return конфигуратор CORS.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            /**
             * Регистрируем параметры CORS фильтрации.
             * @param registry
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE",
                    "PATCH");
            }
        };
    }
}
