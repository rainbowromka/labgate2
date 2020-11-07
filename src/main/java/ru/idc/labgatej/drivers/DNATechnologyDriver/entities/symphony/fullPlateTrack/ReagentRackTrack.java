package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.fullPlateTrack;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(value = XmlAccessType.FIELD)
@Data
public class ReagentRackTrack
{
    /**
     * Denotes the type of reagent rack. “Buffer bottle” indicates a buffer
     * bottle. “Accessory trough” indicates an alcohol trough, and the reagent
     * rack number is displayed if the rack is a reagent box.
     */
    @XmlElement(name = "ReagentRackLabel")
    String reagentRackLabel;

    /**
     * Reagent box: Combination of Ident No, Lot, Internal No, Enzyme Rack Lot
     * Id, and internal slot number. Buffer bottle: Bar code that was entered
     * or scanned. Accessory trough: Accessory-Trough-5 or Accessory-Trough12.
     */
    @XmlElement(name = "Id")
    String id;

    /**
     * Reagent box: “IdentNo” identifies the type of reagent box that was used
     * (e.g., AXpH DNA Kit (192). Buffer bottle: Bar code that was entered or
     * scanned. Accessory trough:Field remains empty.
     */
    @XmlElement(name = "IdentNo")
    String identNo;

    /**
     * For the first deployed buffer bottle in the buffer bottle slot, the
     * logical name is “BufferBottle-1”. If the buffer bottle is exchanged,
     * the number is incremented (e.g., “BufferBottle-2”, “BufferBottle-3”,
     * etc).
     */
    @XmlElement(name = "LogicalName")
    String logicalName;

    /**
     * The slot on which the reagent rack was placed. Reagent box:
     * “Reagentbox-1” or “Reagentbox-2”. Buffer bottle: “BufferBottle-1”.
     * Accessory trough: “Accessory-Trough-5” or “AccessoryTrough-12”.
     */
    @XmlElement(name = "LastSlotName")
    String lastSlotName;

    /**
     * The slot on which the reagent rack was loaded. Reagent cartridge:
     * “Reagentbox-1” or “Reagentbox-2”. Buffer bottle: “BufferBottle-1”.
     * Accessory trough: “Accessory-Trough-5” or “AccessoryTrough-12”.
     */
    @XmlElement(name = "AllSlotNames")
    String allSlotNames;

    /**
     * The lot number of the reagent rack. Reagent box: Lot number that was read
     * from the bar code of the first reagent (lot number is the same for all
     * reagents). Buffer bottle: This field is “Unknown”. Accessory trough: This
     * field is “Unknown”.
     */
    @XmlElement(name = "Lot")
    String lot;

    /**
     * Reagent box: Name of the reagent rack, including catalog no.
     * Buffer bottle: “BufferBottle60Rack”.
     * Accessory trough: “AlcoTroughRack”.
     */
    @XmlElement(name = "Name")
    String name;

    /**
     * Internal number of the reagent rack. If using a Reagent box (i.e., a
     * ReagentRackTrack exists and InternalNo is within range), print InternalNo
     * as “Reagent rack number” within the “Reagent information” table.
     */
    @XmlElement(name = "InternalNo")
    String internalNo;

    /**
     * Reagent box and buffer bottle: Earliest expiration date with respect to
     * the reagents in the rack. If the bar code did not contain an expiration
     * date, this field is empty. Accessory trough: This field remains empty.
     */
    @XmlElement(name = "ExpirationDate")
    String expirationDate;

    /**
     * Reagent box: ID read from the beadwell bar code. Buffer bottle and
     * accessory trough: This field remains empty.
     */
    @XmlElement(name = "BeadwellID")
    String beadwellID;

    /**
     * Reagent box: Lot number read from the beadwell bar code. Buffer bottle
     * and accessory trough: Field remains empty.
     */
    @XmlElement(name = "BeadwellLotID")
    String beadwellLotID;

    /**
     * Reagent box: ID read from the enzyme rack bar code. Buffer bottle and
     * accessory trough: Field remains empty.
     */
    @XmlElement(name = "EnzymRackId")
    String enzymRackId;

    /**
     * Reagent box: Lot number read from the enzyme rack bar code. Buffer bottle
     * and accessory trough: Field remains empty.
     */
    @XmlElement(name = "EnzymRackLotId")
    String enzymRackLotId;

    /**
     * “Passed” if lot numbers and IDs of all reagents and the enzyme rack are
     * matching. Otherwise, “Failed”.
     */
    @XmlElement(name = "Homogeneity")
    String homogeneity;

    /**
     * One or more elements containing information about the reagents in this
     * reagent rack.
     * See “ReagentTrack”, page 34.
     */
    @XmlElement(name = "ReagentTrack")
    List<ReagentTrack> reagentTracks;
}
