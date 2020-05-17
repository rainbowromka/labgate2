package ru.idc.labgatej.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class HeaderInfo {
	/**
	 * A unique number or other ID that uniquely identifies the transmission for use in network.
	 */
	private String transmissionId;
	private boolean isQualityControl;
}
