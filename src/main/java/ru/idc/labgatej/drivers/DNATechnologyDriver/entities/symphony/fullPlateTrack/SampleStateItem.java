package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.fullPlateTrack;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(value = XmlAccessType.FIELD)
@Data
public class SampleStateItem
{
    /**
     * Sample status (i.e., valid, unclear, invalid, empty). “Empty” indicates
     * an unknown sample status.
     */
    @XmlElement(name = "SampleState")
    String sampleState;

    /**
     * Time at which the sample status changed.
     */
    @XmlElement(name = "Time")
    String time;

    /**
     * Command that initiated the change in sample state.
     */
    @XmlElement(name = "Command")
    String command;

    /**
     * Process step in which the sample state changed.
     */
    @XmlElement(name = "BioFB")
    String bioFB;

    /**
     * Error/message for change.
     */
    @XmlElement(name = "Reason")
    String reason;

    /**
     * Error/message code for change.
     */
    @XmlElement(name = "ReasonCode")
    String reasonCode;
}
