package ru.idc.labgatej.model.multiskanfc;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@XStreamAlias("Sample")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Sample {
	@XStreamAsAttribute
	private String name;

	@XStreamAsAttribute
	private String description;

	@XStreamAsAttribute
	private String barcode;

	@XStreamAlias("Concentration")
	private Concentration concentration;

	@XStreamAlias("OpticalDencity")
	private OpticalDencity opticalDencity;

	@XStreamAlias("Evaluation")
	private Evaluation evaluation;
}
