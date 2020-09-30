package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.fullPlateTrack;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(value = XmlAccessType.FIELD)
@Data
public class ReagentTrack
{
    /**
     * Name of the reagent.
     */
    @XmlElement(name = "Id")
    String id;

    /**
     * Position of the reagent on the reagent rack. For reagent cartridges: 1–7
     * are the 120 ml reagent wells, starting from the left, 8 is the bead well,
     * and 9–15 are the enzyme tubes, starting from the left.
     */
    @XmlElement(name = "Position")
    String position;

    /**
     * Lot number of reagent; same for all reagents.
     */
    @XmlElement(name = "Lot")
    String lot;

    /**
     * Volume of reagent.
     */
    @XmlElement(name = "Volume")
    String volume;

    /**
     * Expiration date of reagent; same for all reagents.
     */
    @XmlElement(name = "ExpirationDate")
    String expirationDate;

    /**
     * “1” if reagent is expired; ”0” if reagent not expired.
     */
    @XmlElement(name = "Expired")
    String expired;
}
