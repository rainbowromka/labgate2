package ru.idc.labgatej.model.multiskanfc;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@XStreamAlias("Measures")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Measures {
	@XStreamImplicit
	private List<Measure> measures;
}
