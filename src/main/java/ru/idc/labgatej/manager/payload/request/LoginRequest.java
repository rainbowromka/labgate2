package ru.idc.labgatej.manager.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * Объект запроса на авторизацию.
 *
 * @author Роман Перминов.
 */
@Getter
@Setter
public class LoginRequest
{
    /**
     * Имя пользователя.
     */
    @NotBlank
    private String username;

    /**
     * Пароль.
     */
    @NotBlank
    private String password;
}
