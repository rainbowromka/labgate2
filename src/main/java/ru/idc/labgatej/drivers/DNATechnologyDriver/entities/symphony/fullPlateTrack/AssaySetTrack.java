package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.fullPlateTrack;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(value = XmlAccessType.FIELD)
@Data
public class AssaySetTrack
{
    /**
     * Name of the ACS; taken from the tag AssayControlSetName in the ACS file.
     */
    @XmlElement(name = "Name")
    String name;

    /**
     * Flag that specifies if the assay control set is a genuine QIAGEN file:
     * - “1” if ACS is a genuine QIAGEN file.
     * - “0” if ACS is customized file.
     */
    @XmlElement(name = "ACSAuthentic")
    String acsAuthentic;

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
     * One or more elements containing position and LLD mode.
     */
    @XmlElement(name = "ICPositionInfo")
    List<IcPositionInfo> icPositionInfo;
}
