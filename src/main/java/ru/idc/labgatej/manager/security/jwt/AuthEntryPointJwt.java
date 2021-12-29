package ru.idc.labgatej.manager.security.jwt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


/**
 * Класс, отлавливающий ошибку аутентификации.
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint
{
    private static final Logger logger =
        LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /**
     * Этот метод будет срабатывать каждый раз, когда неаутентифицированный
     * пользователь запрашивает защищенный HTTP-ресурс и генерируется исключение
     * AuthenticationException.
     *
     * @param request
     *        запрос.
     * @param response
     *        ответ.
     * @param authException
     *        исключение сгенерированное при неаутентифицированном запросе.
     * @throws IOException
     *         генерируемое исключение.
     * @throws ServletException
     *         генерируемое исключение.
     */
    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException)
    throws IOException, ServletException
    {
        logger.error("Unauthorized error: {}", authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}
