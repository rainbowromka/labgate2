package ru.idc.labgatej.manager.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Ответ сервера, содержащий информацию о пользователе.
 *
 * @author Roman Perminov
 */
@Getter
@Setter
public class UserInfoResponse
{
    private Long id;
    private String username;
    private String email;
    private List<String> roles;

    public UserInfoResponse(
        Long id,
        String username,
        String email,
        List<String> roles)
    {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
