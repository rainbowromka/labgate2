package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.fullPlateTrack;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(value = XmlAccessType.FIELD)
@Data
public class InternalControlTrack
{
    /**
     * Name of the IC as stated in ACS.
     */
    @XmlElement(name = "ICName")
    String icName;

    /**
     * ID of the IC (bar code from IC tube).
     */
    @XmlElement(name = "ICBarcode")
    String icBarcode;

    /**
     * One or more elements containing information about the tubes that contain
     * this IC. See “ICTubeInfo”, page 36.
     */
    @XmlElement(name = "ICTubeInfo")
    List<ICTubeInfo> icTubeInfo;

    /**
     * One or more elements. Contains the names of the ACS that use this IC.
     */
    @XmlElement(name = "AssayControlSetName")
    String assayControlSetName;
}
