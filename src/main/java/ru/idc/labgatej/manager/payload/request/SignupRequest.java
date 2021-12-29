package ru.idc.labgatej.manager.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.Set;

/**
 * Объект, содержащий запрос для авторизации пользователя.
 */
@Getter
@Setter
public class SignupRequest
{
    /**
     * Имя пользователя.
     */
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    /**
     * Электронная почта.
     */
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    /**
     * Набор ролей.
     */
    private Set<String> role;

    /**
     * Пароль.
     */
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
}
