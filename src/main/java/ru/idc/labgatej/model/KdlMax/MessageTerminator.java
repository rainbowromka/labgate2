package ru.idc.labgatej.model.KdlMax;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * Объект завершающей записи протокола ASTM (КДЛ-Макс)
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageTerminator implements ASTMRecord
{
    Long sequence;
    String terminationCode;

    /**
     * Получить тип записи. Всегда возвращает "L" для данного типа.
     *
     * @return тип записи. Всегда возвращает "L" для данного типа.
     */
    @Override
    public String getType() {
        return "L";
    }

    /**
     * Получить строку завершающей записи в формате протокола ASTM.
     *
     * @return строка завершающей записи в формате протокола ASTM.
     */
    @Override
    public String toASTM() {
        return new StringBuilder(getType()).append("|")
            .append(Objects.toString(getSequence(), "")).append("|")
            .append(Objects.toString(getTerminationCode(), "")).append("<CR>")
            .toString();
    }
}
