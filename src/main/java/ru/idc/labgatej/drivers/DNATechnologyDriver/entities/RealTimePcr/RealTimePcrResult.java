package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Результ теста.
 */
@Data
@XmlAccessorType(value = XmlAccessType.FIELD)
public class RealTimePcrResult
{
    /**
     * Имя результата.
     */
    @XmlAttribute(name = "name")
    private String name;

    /**
     * Значение результата.
     */
    @XmlAttribute(name = "value")
    private String value;
}
