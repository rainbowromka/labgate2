package ru.idc.labgatej.model.KdlMax;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Объект пациента протокола ASTM (КДЛ-Макс)
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Patient implements ASTMRecord
{
    /**
     * Sequence #. Порядковый номер записи (согласно протоколу ASTM).
     */
    Long sequence;

    /**
     * Заказы исследований.
     */
    List<AstmOrder> astmOrders;

    /**
     * Получить тип записи. Всегда возвращает "P" для данного типа.
     *
     * @return тип записи. Всегда возвращает "P" для данного типа.
     */
    @Override
    public String getType() {
        return "P";
    }

    /**
     * Получает список заказов. Ленивая инициализация.
     *
     * @return список заказов.
     */
    public List<AstmOrder> getAstmOrders()
    {
        if (astmOrders == null) {
            astmOrders = new ArrayList<>();
        }

        return astmOrders;
    }

    /**
     * Получить строку пациента в формате протокола ASTM.
     *
     * @return строка пациента в формате протокола ASTM.
     */
    @Override
    public String toASTM() {

        StringBuilder msg =new StringBuilder(getType()+"|")
            .append(Objects.toString(sequence, ""))
            .append("||||||||||||||||||||||||||||||||<CR>");

        for (AstmOrder astmOrder : getAstmOrders())
        {
            msg.append(astmOrder.toASTM());
        }

        return msg.toString();
    }

}
