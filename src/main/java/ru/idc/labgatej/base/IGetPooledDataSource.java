package ru.idc.labgatej.base;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;

/**
 * Интерфейс, описывающий получение пула доступа к базе данных KRYPTON.
 */
public interface IGetPooledDataSource
{
    /**
     * Возвращает пул доступа к базе данных KRYPTON.
     * @return пул доступа к базе данных KRYPTON.
     * @throws PropertyVetoException
     *         генерируемое исключение.
     */
    ComboPooledDataSource getCpds()
    throws PropertyVetoException;
}
