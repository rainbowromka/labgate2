package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Образец
 */
@Data
@XmlAccessorType(value = XmlAccessType.FIELD)
public class RealTimePcrSample
{
    /**
     * Имя образца (баркод).
     */
    @XmlAttribute(name = "name")
    private String name;

    /**
     * Отображаемое имя образца.
     */
    @XmlAttribute(name = "display_name")
    private String displayName;

    /**
     * Пациент.
     */
    @XmlAttribute(name = "patient")
    private String patient;
}
