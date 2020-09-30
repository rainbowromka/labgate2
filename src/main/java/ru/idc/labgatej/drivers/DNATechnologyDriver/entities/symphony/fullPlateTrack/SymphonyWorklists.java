package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.fullPlateTrack;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class SymphonyWorklists
{
    /**
     * The name of the worklist that was used during batch ordering.
     */
    @XmlElement(name = "Worklist")
    List<String> worklists;
}
