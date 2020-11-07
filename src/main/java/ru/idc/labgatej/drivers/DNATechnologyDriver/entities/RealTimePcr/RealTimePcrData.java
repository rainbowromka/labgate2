package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Задание прибора.
 */
@Data
@XmlAccessorType(value = XmlAccessType.FIELD)
public class RealTimePcrData
{
    /**
     * Штатив.
     */
    @XmlElement(name = "plate")
    private RealTimePcrPlate realTimePcrPlates;
}
