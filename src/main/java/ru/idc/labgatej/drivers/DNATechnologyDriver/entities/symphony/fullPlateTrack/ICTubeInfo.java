package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.fullPlateTrack;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(value = XmlAccessType.FIELD)
@Data
public class ICTubeInfo
{
    /**
     * Position of the IC tube in the IC carrier.
     */
    @XmlElement(name="ICTubePosition")
    String icTubePosition;

    /**
     * Labware selected at the GUI for this IC tube.
     */
    @XmlElement(name="ICTubeLabware")
    String icTubeLabware;
}
