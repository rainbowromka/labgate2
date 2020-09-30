package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.fullPlateTrack;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(value = XmlAccessType.FIELD)
@Data
public class LiquidTrack
{
    /**
     * Transferred liquid volume in μl.
     */
    @XmlElement(name = "Quantity")
    String quantity;

    /**
     * Time when the liquid was transferred.
     */
    @XmlElement(name = "Time")
    String time;

    /**
     * Name of the reagent or IC.
     */
    @XmlElement(name = "Type")
    String type;

    /**
     * “1” if the liquid is IC, “0” otherwise.
     */
    @XmlElement(name = "InternalControl")
    String internalControl;

    /**
     * For a reagent: Internal number of the reagent rack from which the liquid
     * was taken. For an internal control: “0”.
     */
    @XmlElement(name = "ReagentRackNo")
    String reagentRackNo;

    /**
     * For a reagent: The type of container from which the reagent was taken.For
     * internal control: Unknown.
     */
    @XmlElement(name = "ReagentSourceType")
    String reagentSourceType;
}
