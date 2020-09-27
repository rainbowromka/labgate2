package ru.idc.labgatej.model.multiskanfc;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@XStreamAlias("Sessions")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Sessions {
	@XStreamAlias("Session")
	private Session session;
}
