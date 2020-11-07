package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.results;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Описание образца.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Sample
{

    /**
     * Идентификатор образца в ЛИС, полученный при импорте заявки.
     */
    @XmlAttribute(name = "ID")
    private Long id;

    @XmlAttribute(name = "SourceID")
    private Long sourceID;

    /**
     * Маркировка пробирки с образцом.
     */
    @XmlAttribute(name = "Code")
    private String code;

    /**
     * Идентификатор биоматериала.
     */
    @XmlAttribute(name = "MatID")
    private String matID;

    /**
     * Дата создания заявки.
     */
    @XmlAttribute(name = "SamplingDate")
    private String samplingDate;

    /**
     * Исследование.
     */
    @XmlElement(name = "Service")
    private Service service;
}
