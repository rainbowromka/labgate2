package ru.idc.labgatej.model.multiskanfc;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@XStreamAlias("Result")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Concentration {
	@XStreamAsAttribute
	@XStreamAlias("calculatedmodel")
	private String calculatedModel;

	@XStreamAlias("Unit")
	private Unit unit;

	@XStreamAlias("Value")
	private Value value;

}
