package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Данные задания.
 */
@Data
@XmlAccessorType(value = XmlAccessType.FIELD)
public class RealTimePcrCreatePlate
{
    /**
     * Наименование плашки. Barcode.
     */
    @XmlAttribute(name = "plate")
    private String plate;

    /**
     * Количество ячеек по горизонтали.
     */
    @XmlAttribute(name = "size_x")
    private String size_x;

    /**
     * Количество ячеек по вертикали.
     */
    @XmlAttribute(name = "size_y")
    private String size_y;

    /**
     * Ячейки с пробирками в штативе.
     */
    @XmlElement(name = "cell")
    List<RealTimePcrCell> realTimePcrCells;
}
