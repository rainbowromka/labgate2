package ru.idc.labgatej.manager.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Тестовый контроллер сервиса Авторизации. В зависимости от назначенных ролей,
 * для разных пользователей будут срабатывать те или иные REST-сервисы.
 *
 * @author Роман Перминов.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/test")
public class TestController
{
    /**
     * Данный запрос доступен всем пользователям, в том числе не
     * зарегистрированным в системе. Публичный доступ.
     *
     * @return публичный контент.
     */
    @GetMapping("/all")
    public String allAccess()
    {
        return "Public Content.";
    }

    /**
     * Данный запрос доступен пользователям с правами USER, MODERATOR, ADMIN.
     * @return контент для всех пользователей.
     */
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String userAccess()
    {
        return "User Content.";
    }

    /**
     * Данный запрос доступен пользователям с правами MODERATOR.
     *
     * @param principal
     *        данные пользователя.
     * @return контент для пользователя с правами MODERATOR.
     */
    @GetMapping("/mod")
    @PreAuthorize("hasRole('MODERATOR')")
    public String moderatorAccess(Principal principal)
    {
        return "Moderator Board.";
    }

    /**
     * Данный запрос доступен пользователям с правами ADMIN.
     *
     * @return контент для пользователя с правами ADMIN.
     */
    @GetMapping("/admin")
    @PreAuthorize("haseRole('ADMIN')")
    public String adminAccess()
    {
        return "Admin Board.";
    }
}
