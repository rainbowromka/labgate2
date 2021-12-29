package ru.idc.labgatej.manager.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * JWT ответ на запрос. Содержит информацию о пользователе - токен, тип, id,
 * имя пользователя, e-mail, роли. *
 */
@Getter
@Setter
public class JwtResponse
{
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;

    /**
     * Создает объект пользователя - ответ на запрос.
     *
     * @param accesToken
     *        токен.
     * @param id
     *        id - пользователя.
     * @param username
     *        имя пользователя.
     * @param email
     *        электронная почта.
     * @param roles
     *        роли.
     */
    public JwtResponse(
        String accesToken,
        Long id,
        String username,
        String email,
        List<String> roles)
    {
        this.token = accesToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
