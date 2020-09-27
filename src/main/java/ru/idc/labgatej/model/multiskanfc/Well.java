package ru.idc.labgatej.model.multiskanfc;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@XStreamAlias("Well")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Well {
	@XStreamAsAttribute
	private String address;

	@XStreamAlias("Sample")
	private Sample sample;
}
