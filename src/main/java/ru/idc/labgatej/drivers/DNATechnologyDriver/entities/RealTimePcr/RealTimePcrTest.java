package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Тест.
 */
@Data
@XmlAccessorType(value = XmlAccessType.FIELD)
public class RealTimePcrTest
{
    /**
     * ID теста.
     */
    @XmlAttribute(name = "id")
    private String id;

    /**
     * Результат теста.
     */
    @XmlAttribute(name = "value")
    private String value;

    /**
     * Результаты тестов.
     */
    @XmlElement(name = "result")
    List<RealTimePcrResult> realTimePcrResults;
}
