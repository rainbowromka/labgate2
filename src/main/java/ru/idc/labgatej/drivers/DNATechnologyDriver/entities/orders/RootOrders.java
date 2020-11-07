package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.orders;


import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Корневой элемент файла xml заявки на исследование.
 */
@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class RootOrders
{
    /**
     * Заявка на исследование. Элемент должен быть вложен непосредственно в
     * корневой элемент. В заказе может быть несколько заявок.
     */
    @XmlElement(name = "Inquiry")
    List<InquryOrders> inquryOrders;
}
