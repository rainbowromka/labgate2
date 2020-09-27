package ru.idc.labgatej.model.multiskanfc;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@XStreamAlias("ResultsSummary")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ResultsSummary {
	@XStreamAlias("Общие")
	private CommonResult commonResult;
}
