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
public class Result {
	@XStreamAsAttribute
	private String name;

	@XStreamAsAttribute
	private String value;

	@XStreamAsAttribute
	private String disabled;

	@XStreamAsAttribute
	private String extrapolated;
}
