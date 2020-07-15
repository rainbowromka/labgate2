package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.orders;


import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ServiceOrder
{

    /**
     * Обязательный атрибут. Идентификатор исследования в справочнике
     * исследований.
     */
    @XmlAttribute(name = "ServiceID", required = true)
    private String id;

    /**
     * Необязательный атрибут. Имя исследования в ЛИС.
     */
    @XmlAttribute(name = "Name")
    private String name;

    /**
     * Необязательный атрибут. Код исследования в ЛИС.
     */
    @XmlAttribute(name = "Code")
    private String code;
}
