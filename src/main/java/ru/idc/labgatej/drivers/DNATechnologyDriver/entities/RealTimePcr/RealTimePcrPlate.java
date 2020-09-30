package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Штатив.
 */
@Data
@XmlAccessorType(value = XmlAccessType.FIELD)
public class RealTimePcrPlate
{
    /**
     * ID штатива.
     */
    @XmlAttribute(name = "id")
    private String id;
    /**
     * Ячейки с пробирками в штативе.
     */
    @XmlElement(name = "cell")
    private List<RealTimePcrCell> realTimePcrCells;
}
