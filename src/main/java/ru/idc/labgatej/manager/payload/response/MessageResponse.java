package ru.idc.labgatej.manager.payload.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Ответ с сообщением.
 */
@Getter
@Setter
public class MessageResponse
{
    /**
     * Сообщение.
     */
    private String message;

    /**
     * Возвращает сообщение.
     *
     * @param message
     *        сообщение.
     */
    public MessageResponse(
        String message)
    {
        this.message = message;
    }
}
