package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Заголовок файла.
 */
@Data
@XmlAccessorType(value = XmlAccessType.FIELD)
public class RealTimePCR
{
    /**
     * Путь к протоколу.
     */
    @XmlAttribute(name = "ProtocolPath")
    private String protocolPath;
}
