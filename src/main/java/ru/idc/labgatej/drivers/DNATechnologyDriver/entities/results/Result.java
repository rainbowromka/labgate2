package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.results;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * По-тестовый результат выполнения исследования. В одном исследовании может
 * быть как несколько результатов - для каждого из тестов в составе
 * исследования, так и несколько ответов по каждому тесту, входящему в его
 * состав.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Result {
    /**
     * Идентификатор теста в справочнике тестов.
     */
    @XmlAttribute(name = "TestID")
    private String testID;

    /**
     * Результат теста.
     */
    @XmlAttribute(name = "Value")
    private String value;

    /**
     * Единицы измерения результата.
     */
    @XmlAttribute(name = "Units")
    private String units;

    /**
     * Имя теста.
     */
    @XmlAttribute(name = "Name")
    private String name;

    /**
     * Необязательный атрибут.
     */
    @XmlAttribute(name = "Code")
    private String code;

    /**
     * Недокументирован. Пример: DoneTime="2020-01-27T11:03:07.680"
     */
    @XmlAttribute(name = "DoneTime")
    private String doneTime;
}

