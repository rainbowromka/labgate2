package ru.idc.labgatej.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class HeaderInfo {
	private String barcode;
	private boolean isQualityControl;
}
