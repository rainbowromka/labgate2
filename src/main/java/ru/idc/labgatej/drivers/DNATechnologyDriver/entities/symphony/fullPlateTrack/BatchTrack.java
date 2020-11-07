package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.fullPlateTrack;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@Data
@XmlAccessorType(value = XmlAccessType.FIELD)
public class BatchTrack
{
    /**
     * Name of the script (protocol) related to the entire batch (i.e., to all
     * samples). Taken from the tag ScriptName of the *.xml file of the
     * protocol.
     */
    @XmlElement(name = "ScriptName")
    String scriptName;

    /**
     * Script version of the protocol. May be emtpy if the script
     * version in the protocol is emtpy.
     */
    @XmlElement(name = "ScriptVersion")
    String scriptVersion;

    /**
     * Batch ID.
     */
    @XmlElement(name = "BatchID")
    String batchID;

    /**
     * User ID for whoever set up the batch.
     */
    @XmlElement(name = "Operator")
    String operator;

    /**
     * User ID for whoever started the run.
     */
    @XmlElement(name = "StartedByOperator")
    String startedByOperator;

    /**
     * Time the batch was queued (i.e., set up is finished).
     */
    @XmlElement(name = "orderingTime")
    String orderingTime;

    /**
     * Time batch processing was started.
     */
    @XmlElement(name = "StartOfRun")
    String startOfRun;

    /**
     * Time batch processing was finished (by completion or cancellation).
     */
    @XmlElement(name = "EndOfRun")
    String endOfRun;

    /**
     * Run mode of the SP batch.
     */
    @XmlElement(name = "RunMode")
    String runMode;

    /**
     * “1” if plate carriers were used for samples.
     * “0” if tube carriers were used for samples.
     */
    @XmlElement(name = "IsPlateMode")
    String isPlateMode;

    /**
     * Slot of the sample rack; 1–4 if a tube carrier was used; 6–9 if a plate
     * carrier was used.
     */
    @XmlElement(name = "SampleRackNo")
    String sampleRackNo;

    /**
     * ID of the eluate rack containing the eluate of this batch. Same value as
     * FullPlateTrack/ PlateID.
     */
    @XmlElement(name = "eluateRackID")
    String eluateRackID;

    /**
     * Empty in case of tube carrier; in case of rack carrier, the sample rack
     * ID is given.
     */
    @XmlElement(name = "SampleRackID")
    String sampleRackID;

    /**
     * Tube for tube carrier. For rack carriers, the chosen rack type is given.
     */
    @XmlElement(name = "SampleRackType")
    String sampleRackType;

    /**
     * Number of the slot on the eluate drawer where the eluate rack of the
     * batch is placed. Same value as FullPlateTrack/ SlotNo.
     */
    @XmlElement(name = "EluateSlotNo")
    String eluateSlotNo;

    /**
     * “Passed” if all samples in the result file are valid.
     * “Failed” if at least one sample is invalid.
     * “Unclear” if at least one sample is unclear and neither is invalid.
     */
    @XmlElement(name = "AllSamplesOK")
    String allSamplesOK;

    /**
     * “1” if the script uses eluate cooling functionality.
     * “0” if the script does not use eluate cooling.
     */
    @XmlElement(name = "NeedsEluateCooling")
    String needsEluateCooling;

    /**
     * Not used.
     */
    @XmlElement(name = "IvD")
    String ivD;

    /**
     * Internal parameter.
     */
    @XmlElement(name = "ShowSourceConcentration")
    String showSourceConcentration;

    /**
     * Specifies which volume to print bold in the HTML result file.
     */
    @XmlElement(name = "DisplayEluateVolumeOnScreen")
    String displayEluateVolumeOnScreen;

    /**
     * Specifies whether to show the elution volume, the minimal eluate volume
     * in elution labware at the time of eluate transfer, or both, respectively,
     * in the HTML result file.
     */
    @XmlElement(name = "DisplayEluateVolumeInResultFile")
    String displayEluateVolumeInResultFile;

    /**
     * Specifies whether to show the volume for transferring elution buffer into
     * sample prep cartridges in the HTML result file.
     */
    @XmlElement(name = "DisplayBufferVolumeInResultFile")
    String displayBufferVolumeInResultFile;

    /**
     * A list of work lists that were used for ordering the batch. See
     * “Worklists”, page 20.
     */
    @XmlElement(name = "Worklists")
    SymphonyWorklists worklists;

    /**
     * 1–24 samples that were processed in the batch.
     * See “SampleTrack”, page 21.
     */
    @XmlElement(name = "SampleTrack")
    List<SampleTrack> sampleTracks;

    /**
     *
     */
    @XmlElement(name = "Message")
    List<BatchTrackMessage> messages;

    /**
     * 3 elements: Contains temperature supervision information for lysis or
     * eluate cooling. The element for shaker speed is unused.
     * See “ProcessStepResult”, page 28.
     */
    @XmlElement(name = "ProcessStepResult")
    List<ProcessStepResult> processStepResults;

    /**
     * One or more elements containing information about the ACS that were used
     * in the batch.
     * See “AssaySetTrack”, page 29.
     */
    @XmlElement(name = "AssaySetTrack")
    List<AssaySetTrack> assaySetTracks;

    /**
     * Internal parameter.
     */
    @XmlElement(name = "SPLoadedReagentTrack")
    String SPLoadedReagentTrack;
}
