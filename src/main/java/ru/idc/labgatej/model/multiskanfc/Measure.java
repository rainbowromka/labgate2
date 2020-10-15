package ru.idc.labgatej.model.multiskanfc;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@XStreamAlias("Measure")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Measure {
	@XStreamAsAttribute
	private String analyte;

	@XStreamAsAttribute
	private Date time;

	@XStreamAsAttribute
	@XStreamAlias("mainwavelength")
	private String mainWaveLength;

	@XStreamAsAttribute
	@XStreamAlias("referencewavelength")
	private String referenceWaveLength;

	@XStreamAsAttribute
	@XStreamAlias("IsValid")
	private String isValid;

	@XStreamAlias("Plate")
	private Plate plate;
}
