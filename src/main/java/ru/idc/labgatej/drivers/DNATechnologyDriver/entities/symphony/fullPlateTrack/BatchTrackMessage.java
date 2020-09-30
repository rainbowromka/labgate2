package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.fullPlateTrack;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(value = XmlAccessType.FIELD)
@Data
public class BatchTrackMessage
{
    /**
     * Error code of the message that occurred.
     */
    @XmlElement(name = "MessageId")
    String messageId;

    /**
     * Text of the message.
     */
    @XmlElement(name = "MessageText")
    String messageText;

    /**
     * Timestamp of the message.
     */
    @XmlElement(name = "Timestamp")
    String timestamp;

    /**
     * If the error message is related to a specific script command, this
     * element contains the name of the command.
     */
    @XmlElement(name = "CommandName")
    String commandName;

    /**
     * If the cause of the message was triggered by an operator, their ID is
     * shown here.
     */
    @XmlElement(name = "Operator")
    String operator;

    /**
     * If the message relates to a specific sample, the ID of the sample is
     * given here. If the timestamp (within a certain range), message ID,
     * message text, and command relate to several samples, a comma-separated
     * list of affected sample IDs will be generated.
     */
    @XmlElement(name = "SampleId")
    String sampleId;

    /**
     * If the message relates to a specific sample, this element denotes the
     * position of the sample on the sample rack. If the timestamp (within
     * certain range), message ID, message text, and command relate to several
     * samples, a comma-separated list of affected sample positions will be
     * generated.
     */
    @XmlElement(name = "SamplePosition")
    String samplePosition;
}
