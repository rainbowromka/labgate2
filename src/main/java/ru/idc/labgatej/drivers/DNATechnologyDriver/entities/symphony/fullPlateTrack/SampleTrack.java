package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.fullPlateTrack;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(value = XmlAccessType.FIELD)
@Data
public class SampleTrack
{
    /**
     * Eluate volume of the sample (same for all samples) in μl.
     */
    @XmlElement(name = "SampleOutputVolume")
    String sampleOutputVolume;

    /**
     * Sample ID. This can be generated automatically by the QIAsymphony SP,
     * manually entered, or scanned during insertion of the tube carrier.
     */
    @XmlElement(name = "SampleCode")
    String sampleCode;

    /**
     * Scanned 2D bar code on the bottom of the Elution tube (optional).
     */
    @XmlElement(name = "EluateTubeBarcode")
    String eluateTubeBarcode;

    /**
     * SampleCode. When EluateTubeBarcode is not empty: Followed by one blank
     * followed by the EluateTubeBarcode.
     */
    @XmlElement(name = "SampleCodeWithEluateTubeBarcode")
    String SampleCodeWithEluateTubeBarcode;

    /**
     * Sample position on the sample carrier.
     */
    @XmlElement(name = "SamplePosition")
    String samplePosition;

    /**
     * ACS used for processing the sample.
     */
    @XmlElement(name = "AssaySet")
    String assaySet;

    /**
     * Name of the work list used for this sample. If a work list was not used,
     * this field is empty.
     */
    @XmlElement(name = "Worklist")
    String worklist;

    /**
     * The type of liquid-level detection (LLD) used for aspiration (“P” =
     * pressure, “C” = capacitive, “N” = no liquid-level detection).
     */
    @XmlElement(name = "AspirationMode")
    String aspirationMode;

    /**
     * The LLD method that was used for aspirating the IC added to the sample
     * (pressure, capacitive, or none). Empty if no IC was added to the sample.
     */
    @XmlElement(name = "ICAspirationMode")
    String icAspirationMode;

    /**
     * Information about the IC added to the sample; the position of the IC tube
     * in the IC carrier. Empty if no IC was added to the sample.
     */
    @XmlElement(name = "ICPosition")
    String icPosition;

    /**
     * Position of the eluate on the elution rack.
     */
    @XmlElement(name = "SampleOutputPos")
    String sampleOutputPos;

    /**
     * If using the plate carrier, this field is empty. If using the tube
     * carrier, this field displays the tube type.
     */
    @XmlElement(name = "Labware")
    String labware;

    /**
     * “1” indicates that the sample ID and/or the sample tube type was manually
     * modified.
     * “0” indicates that neither the sample ID nor the sample tube type was
     * manually modified.
     */
    @XmlElement(name = "ManuallyEdited")
    String manuallyEdited;

    /**
     * “1” if the sample ID was changed manually, “0“ otherwise.
     */
    @XmlElement(name = "SampleIdManuallyEdited")
    String sampleIdManuallyEdited;

    /**
     * “1” if the sample tube type was changed manually,
     * “0” otherwise.
     */
    @XmlElement(name = "LabwareManualEdited")
    String labwareManualEdited;

    /**
     * One or more elements. Each element contains information about a liquid
     * that was added to the sample during processing.
     * See “LiquidTrack” (page 24).
     */
    @XmlElement(name = "LiquidTrack")
    List<LiquidTrack> liquidTrack;

    /**
     * One or more messages that related to the status of this samples (pausing,
     * cancelling or finishing of the batch).
     */
    @XmlElement(name = "Message")
    List<String> message;

    /**
     * State of the sample.
     */
    @XmlElement(name = "SampleState")
    String sampleState;


    /**
     * History of sample states.
     * See “SampleStateItem”, page 25.
     */
    @XmlElement(name = "SampleStateItem")
    List<SampleStateItem> sampleStateItems;


    /**
     * Type of the sample.
     */
    @XmlElement(name = "SampleType")
    String sampleType;


    /**
     * Type of the sample as it is shown in HTML result file.
     */
    @XmlElement(name = "SampleTypeShort")
    String SampleTypeShort;


    /**
     * Reagent rack from which the beads and reagents for the sample were
     * aspirated. “1” and “2” are the respective reagent boxes; BufferBottle-1
     * is the buffer bottle.
     */
    @XmlElement(name = "ReagentRacks")
    String reagentRacks;


    /**
     * Reagent rack from which the enzymes for the sample were taken. “1” and
     * “2” are the respective reagent boxes.
     */
    @XmlElement(name = "EnzymeReagentRacks")
    String enzymeReagentRacks;


    /**
     * Minimal eluate volume in elution labware at the time of eluate transfer.
     */
    @XmlElement(name = "MinElutionVol")
    String minElutionVol;


    /**
     * Volume for transferring elution buffer into sample prep cartridges.
     */
    @XmlElement(name = "BufferVolume")
    String bufferVolume;


    /**
     * Internal parameter
     */
    @XmlElement(name = "KitRef")
    String kitRef;

    /**
     * Internal parameter
     */
    @XmlElement(name = "ICKitRef")
    String icKitRef;

    /**
     * Internal parameter
     */
    @XmlElement(name = "Concentration")
    String concentration;

    /**
     * Internal parameter
     */
    @XmlElement(name = "ConcentrationFormatted")
    String concentrationFormatted;

    /**
     * Internal parameter
     */
    @XmlElement(name = "ConcentrationUnit")
    String concentrationUnit;

    /**
     * Internal parameter
     */
    @XmlElement(name = "ICConcentration")
    String icConcentration;

    /**
     * Internal parameter
     */
    @XmlElement(name = "ICConcentrationFormatted")
    String icConcentrationFormatted;

    /**
     * Internal parameter
     */
    @XmlElement(name = "ICConcentrationUnit")
    String icConcentrationUnit;
}
