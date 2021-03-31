package ru.idc.labgatej.model.KdlMax;

/**
 * Интерфейс записи протокола ASTM для приборов KDL max.
 */
public interface ASTMRecord
{
    /**
     * Получить тип записи.
     *
     * @return тип записи.
     */
    String getType();

    /**
     * Получить строку записи в формате протокола ASTM.
     *
     * @return строка записи в формате протокола ASTM.
     */
    String toASTM();
}
