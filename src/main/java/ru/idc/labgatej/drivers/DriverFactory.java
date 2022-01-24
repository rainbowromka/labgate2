package ru.idc.labgatej.drivers;

import ru.idc.labgatej.base.IDriver;
import ru.idc.labgatej.drivers.DNATechnologyDriver.DnaTechnologyDriver;
import ru.idc.labgatej.drivers.KDLPrime.KdlPrime;
import ru.idc.labgatej.drivers.KdlMax.KdlMaxDriverAstmDual;
import ru.idc.labgatej.drivers.lazurite.Lazurite;

/**
 * Фабрика по коду драйвера создает новый экземпляр драйвера в памяти, для
 * последующего конфигурирования и запуска.
 *
 * @author Roman Perminov
 */
public class DriverFactory
{
    /**
     * Фабричный метод, предоставляет объект драйвера по его коду.
     *
     * @param code
     *        код драйвера.
     * @return объект драйвера готовый для конфигурирования и запуска.
     */
    public static IDriver getDriverByName(
        String code)
    {
        switch (code.toUpperCase().trim()) {
            case "CITM": return new CitmDriver();
            case "MEDONIC":	return new Medonic();
            case "MULTISKANFC":	return new MultiskanFC();
            case "LAZURITE":	return new Lazurite();
            case "URISKAN": return new UriskanProDriver();
            case "REALBEST": return new RealBest();
            case "DNATEACH": return new DnaTechnologyDriver();
            case "CSVIMPORTER": return new CsvImporter();
            case "KDLMAX": return new KdlMaxDriverAstmDual();
            case "KDLPRIME": return new KdlPrime();
            default: return null;
        }
    }
}
