package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.results;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Исследование.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Service
{
    /**
     * Идентификатор исследования.
     */
    @XmlAttribute(name = "ServiceID")
    private String serviceID;

    /**
     * Имя исследования в ЛИС. Берется из заявки.
     */
    @XmlAttribute(name = "Name")
    private String name;

    /**
     * Код исследования в ЛИС. Берется из заявки.
     */
    @XmlAttribute(name = "Code")
    private String code;

    /**
     * По-тестовый результат выполнения исследования. В одном исследовании может
     * быть как несколько результатов - для каждого из тестов в составе
     * исследования, так и несколько ответов по каждому тесту, входящему в его
     * состав.
     */
    @XmlElement(name = "Result")
    private List<Result> results;
}
