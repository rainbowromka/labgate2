package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.orders;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class SampleOrder
{
    /**
     * Обязательный атрибут. Идентификатор образца, уникальный в рамках обмена.
     * Может совпадать с ID заявки (если в ЛИС сущности «заявка» и «образец»
     * идентичны, или в заявке допускается только 1 образец).
     */
    @XmlAttribute(name = "ID", required = true)
    private String id;

    /**
     * Обязательный атрибут. Маркировка пробирки с образцом.
     */
    @XmlAttribute(name = "Code", required = true)
    private String code;

    /**
     * Обязательный атрибут. Идентификатор биоматериала.
     */
    @XmlAttribute(name = "MatID", required = true)
    private String matID;

    /**
     * Необязательный атрибут. Дата создания заявки.
     */
    @XmlAttribute(name = "SamplingDate")
    private String samplingDate;

    /**
     * Service Исследование.
     */
    @XmlElement(name="Service")
    private List<ServiceOrder> serviceOrders;
}
