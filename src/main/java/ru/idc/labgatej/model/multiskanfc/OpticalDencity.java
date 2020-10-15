package ru.idc.labgatej.model.multiskanfc;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@XStreamAlias("OpticalDencity")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@XStreamConverter(value= ToAttributedValueConverter.class, strings={"text"})
public class OpticalDencity {
	private String text;
}
