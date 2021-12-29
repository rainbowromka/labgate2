package ru.idc.labgatej.manager.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import ru.idc.labgatej.manager.security.services.UserDetailsServiceImpl;


/**
 * Выполняет однократное выполнение каждого запроса к нашему API. Он
 * предоставляет метод doFilterInternal(), который мы реализуем для
 * синтаксического анализа и проверки JWT, загрузки сведений о пользователе (с
 * помощью UserDetailsService), проверки авторизации
 * (с помощью UsernamePasswordAuthenticationToken).
 */
public class AuthTokenFilter
extends OncePerRequestFilter
{
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger
        = LoggerFactory.getLogger(AuthTokenFilter.class);

    /**
     * Гарантировано вызывается только один раз для каждого запроса в одном
     * потоке запроса.
     *
     * @param request
     *        запрос.
     * @param response
     *        ответ.
     * @param filterChain
     *        объект, дающий представление о цепочки вызовов, отфильтрованного
     *        запроса.
     * @throws ServletException
     *         генерируемое исключение.
     * @throws IOException
     *         генерируемое исключение.
     */
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
    throws ServletException, IOException
    {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
            }
        }
        catch (Exception e)
        {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Парсит запрос.
     *
     * @param request
     *        запрос.
     * @return заголовок Authorization.
     */
    private String parseJwt(
        HttpServletRequest request)
    {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer"))
        {
            if (headerAuth.length() > 7)
            {
                // Вообще можно по умолчанию забирать от начала до конца строки,
                // если не будет работать, то тогда раскомментировать ниже.
                //return headerAuth.substring(7, headerAuth.length());
                return headerAuth.substring(7);
            } else {
                return "";
            }
        }

//        request.getCookies()[1].getName();

        return null;
    }
}
