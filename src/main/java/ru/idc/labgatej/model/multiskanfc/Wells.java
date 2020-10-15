package ru.idc.labgatej.model.multiskanfc;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("Wells")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Wells {
	@XStreamImplicit
	private List<Well> wells = new ArrayList<>();
}
