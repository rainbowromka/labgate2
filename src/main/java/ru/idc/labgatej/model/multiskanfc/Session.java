package ru.idc.labgatej.model.multiskanfc;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@XStreamAlias("Session")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Session {
	@XStreamAsAttribute
	private String name;

	@XStreamAsAttribute
	private Date time;

	@XStreamAlias("ResultsSummary")
	private ResultsSummary resultsSummary;
}
