package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr;


import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Файл задания в программу Real Time PCR для амплификаторов ДНК "Технологии".
 */
@Data
@XmlRootElement(name = "package")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class RealTimePcrPackage
{
    /**
     * Заголовок файла.
     */
    @XmlElement(name = "RealTime_PCR")
    private RealTimePCR realTimePcr;

    /**
     * Данные задания.
     */
    @XmlElement(name = "create_plate")
    private RealTimePcrCreatePlate realTimePcrCreatePlate;

    /**
     * Данные результатов анализа.
     */
    @XmlElement(name = "data")
    private RealTimePcrData realTimePcrData;
}
