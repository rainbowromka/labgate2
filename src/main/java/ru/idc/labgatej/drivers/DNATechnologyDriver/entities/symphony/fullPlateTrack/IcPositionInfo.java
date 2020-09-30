package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.fullPlateTrack;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(value = XmlAccessType.FIELD)
@Data
public class IcPositionInfo {
    /**
     * Position of the IC on the tube carrier.
     */
    @XmlElement(name="ICPosition")
    String icPosition;

    /**
     * Aspiration mode that was used for LLD (capacitive, pressure, or none).
     */
    @XmlElement(name="ICAspirationMode")
    String icAspirationMode;
}
