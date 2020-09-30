package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.rack;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


/**
 * Корневой элемент файла xml заявки на исследование.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Rack")
@Data
public class SymphonyRack
{

    /**
     * Version number of the file format used. Files with an unsupported version
     * number cannot be used
     */
    @XmlElement(name = "SerializeVersion")
    Integer serializeVersion;

    /**
     * Rack ID.
     */
    @XmlElement(name = "RackId")
    String rackId;

    /**
     * Rack type.
     */
    @XmlElement(name = "RackLabware")
    String rackLabware;

    /**
     * Time of rack file creation. Valid timestamp format: yyyyMMdd HH:mm:ss.zzz
     * or yyyyMMdd HH:mm:ss.
     */
    @XmlElement(name = "CreationTimestamp")
    String creationTimestamp;

    /**
     * Type of rack (i.e., “Sample” for QIAsymphony SP input racks, “Eluate” for
     * QIAsymphony SP output racks and QIAsymphony AS input racks, and “Assay”
     * for QIAsymphony AS output racks).
     * Note: "In QS 5.0 the system writes rack files for normalization racks
     * with value ” Normalization”, in QS 4 it does not write rack files for
     * normalization racks.
     */
    @XmlElement(name = "RackUsageType")
    String rackUsageType;

    /**
     * “1” indicates that the rack file is generated by the QIAsymphony
     * Management Console from a *.csv file.
     */
    @XmlElement(name = "CSVConverted")
    String CSVConverted;

    /**
     * Indicates system that currently uses rack file. In case of a system crash
     * the crashed system ensures an unlock on restart. “QIAsymphony” indicates
     * shared usage of a rack file by SP and AS.
     */
    @XmlElement(name = "RackLockType")
    String rackLockType;

    /**
     * A position on the rack. All positions of a rack file are described
     * explicitly regardless of empty state. Rack positions are ordered by
     * number (0-based). See “RackPosition”, page 108.
     */
    @XmlElement(name = "RackPosition")
    List<RackPosition> rackPosition;

    /**
     * See “ModificationRecord”, page 110.
     */
    @XmlElement(name = "modificationRecord")
    List<ModificationRecord> modificationRecord;

}
