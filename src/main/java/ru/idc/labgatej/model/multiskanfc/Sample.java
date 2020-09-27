package ru.idc.labgatej.model.multiskanfc;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("Sample")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Sample {
	@XStreamAsAttribute
	private String name;

	@XStreamAsAttribute
	private String group;

	@XStreamImplicit
	private List<Result> results = new ArrayList<Result>();
}
