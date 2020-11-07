package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.rack;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ModificationRecord
{
    /**
     * Time the rack file was modified.
     */
    @XmlElement(name = "Timestamp")
    String timestamp;


    /**
     * ID of the batch that modified the rack file. “0” in case that no batch
     * was involved.
     */
    @XmlElement(name = "BatchID")
    String batchID;


    /**
     * Name of the instrument that modified the rack file.
     */
    @XmlElement(name = "Instrument")
    String instrument;


    /**
     * Description of the modification.
     */
    @XmlElement(name = "Comment")
    String comment;


    /**
     * Instrument or tool that modified the rack file.
     */
    @XmlElement(name = "InstrumentType")
    String instrumentType;
}
