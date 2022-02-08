package ru.idc.labgatej.manager.security.jwt;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import io.jsonwebtoken.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;
import ru.idc.labgatej.manager.security.services.UserDetailsImpl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


/**
 * Обеспечивает методами для генерирования, парсинга и валидации JWT.
 */
@Component
public class JwtUtils
{
    private static final Logger logger =
        LoggerFactory.getLogger(JwtUtils.class);

    @Value("${labgetej.app.jwtSecret}")
    private String jwtSecret;

    @Value("${labgatej.app.jwtExpirationMS}")
    private int jwtExpirationMs;

    @Value("${labgatej.app.jwtCookieName}")
    private String jwtCookie;

    /**
     * Получить JWT-токен из cookies.
     *
     * @param request
     *        запрос, содержащий cookies.
     * @return токен, полученный из cookies, либо null.
     */
    public String getJwtFromCookies(
        HttpServletRequest request)
    {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    /**
     * Генерирует JWT токен для cookies.
     *
     * @param userPrincipal
     *        детальная информация о пользователе.
     * @return токен сгенерированный для cookies.
     */
    public ResponseCookie generateJwtCookie(
        UserDetailsImpl userPrincipal)
    {
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt)
            .path("/")//.domain("/")
            .maxAge(24 * 60 * 60).httpOnly(false).secure(false)
            .build();
        return cookie;
    }

    /**
     * Убирает из cookies JWT-токен.
     *
     * @return cookies без JWT-токена.
     */
    public ResponseCookie getCleanJwtCookie()
    {
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, null)
            .path("/api").build();
        return cookie;
    }

    /**
     * Генерирует токен.
     * @param authentication
     *        объект аутентификации.
     * @return токен.
     */
    public String generateJwtToken(
        Authentication authentication)
    {
        UserDetailsImpl userPrincipal =
            (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
            .setSubject((userPrincipal.getUsername()))
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }

    /**
     * Получить имя пользователя по токену.
     * @param token
     *        токен.
     * @return имя пользователя.
     */
    public String getUserNameFromJwtToken(
        String token)
    {
        return Jwts.parser().setSigningKey(jwtSecret)
            .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Проверить Jwt-токен.
     *
     * @param authToken
     *        токен.
     * @return true - токен верен.
     */
    public boolean validateJwtToken(
        String authToken)
    {
        try
        {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        }
        catch (SignatureException e)
        {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        }
        catch (MalformedJwtException e)
        {
            logger.error("Invalid JWT token: {}", e.getMessage());
        }
        catch (ExpiredJwtException e)
        {
            logger.error("JWT token is expired: {}", e.getMessage());
        }
        catch (UnsupportedJwtException e)
        {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    /**
     * Генерирует токен по имени пользователя.
     * @param username
     * @return
     */
    public String generateTokenFromUsername(
        String username)
    {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }

    /**
     * Парсит запрос.
     *
     * @param request
     *        запрос.
     * @return заголовок Authorization.
     */
    public String parseJwt(
            HttpServletRequest request)
    {
        String headerAuth = request.getHeader("Authorization");
        if (!StringUtils.hasText(headerAuth) && request.getCookies() != null)
        {
            Optional<Cookie> cookie =
                    Arrays.stream(request.getCookies())
                            .filter(c -> c.getName().equals("Token")).findFirst();
            if (cookie.isPresent()) {
                headerAuth = cookie.get().getValue();
                return headerAuth;
            }
        }

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

        return null;
    }

}
