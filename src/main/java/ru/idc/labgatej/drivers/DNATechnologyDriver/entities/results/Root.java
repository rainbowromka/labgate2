package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.results;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Root
{
    /**
     * Заявка на исследование. В заказе может быть несколько заявок.
     */
    @XmlElement(name = "Inquiry")
    private List<Inquiry> inquiries;
}
