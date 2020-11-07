package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Ячейка с пробиркой в штативе.
 */
@Data
@XmlAccessorType(value = XmlAccessType.FIELD)
public class RealTimePcrCell
{
    /**
     * Расположение ячейки в штативе по горизонтали.
     */
    @XmlAttribute(name = "x")
    private String x;

    /**
     * Расположение ячейки в штативе по вертикали.
     */
    @XmlAttribute(name = "y")
    private String y;

    /**
     * Имя ячейки. Barcode.
     */
    @XmlAttribute(name = "name")
    private String name;

    /**
     * Состояние.
     */
    @XmlAttribute(name = "state")
    private String state;

    /**
     * Тесты.
     */
    @XmlElement(name = "test")
    private List<RealTimePcrTest> realTimePcrTests;
}
