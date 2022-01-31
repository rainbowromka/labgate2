package ru.idc.labgatej.manager.config;

import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Конфигуратор, включает поддержку WebSocket. Обеспечивает поддержку базовых
 * соглашений при конфигурировании базовых настроек.
 */
@Component
@EnableWebSocketMessageBroker
public class WebSocketConfiguration
implements WebSocketMessageBrokerConfigurer
{
    /**
     * Префикс, который добавляется к маршруту каждого сообщения.
     */
    public static final String MESSAGE_PREFIX = "/driver";

    /**
     * Метод используется для настройки точки прерывания на серверной
     * стороне для связи с клиентом по ссылке "/runstop".
     *
     * @param registry
     *        контракт на регистрацию конечных точек WebSocket по под-протоколу
     *        STOMP.
     */
    @Override
    public void registerStompEndpoints(
        StompEndpointRegistry registry)
    {
        registry.addEndpoint("/driverentrypoint")
//            .setAllowedOriginPatterns("*")
            .withSockJS();
    }

    /**
     * Используется для настройки брокера, используемого для ретрансляции
     * сообщений между сервером и клиентом.
     *
     * @param registry
     *        реестр для настройки параметров брокера сообщений.
     */
    @Override
    public void configureMessageBroker(
        MessageBrokerRegistry registry)
    {
        registry.enableSimpleBroker(MESSAGE_PREFIX);
        registry.setApplicationDestinationPrefixes("/app");
    }
}
