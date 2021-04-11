package ru.idc.labgatej.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ArchiveInfo extends ManufacturerRecord {
	private String tuberack;
	private int position;

}
