package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.fullPlateTrack;


import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(value = XmlAccessType.FIELD)
@Data
public class ProcessStepResult
{
    /**
     * Denotes the process step. Only Lysis Temperature and Eluate Temperature
     * are used. Shaker Speed is not used.
     */
    @XmlElement(name = "ProcessStep")
    String processStep;

    /**
     * Lysis Temperature: “OK” indicates the temperature was reached; “not OK”
     * indicates the temperature could not be reached in time.
     *
     * Eluate Temperature: “OK” indicates eluate cooling was in the defined
     * temperature range; “not OK” indicates eluate cooling was not in the
     * defined temperature range; “disabled” indicates cooling was disabled;
     * “not required” indicates that eluate cooling was not required.
     *
     * Shaker Speed: Always unknown.
     */
    @XmlElement(name = "Result")
    String result;
}
