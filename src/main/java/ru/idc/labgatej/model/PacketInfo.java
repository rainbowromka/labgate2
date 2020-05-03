package ru.idc.labgatej.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PacketInfo {
	HeaderInfo header;
	List<ResultInfo> results = new ArrayList<>();

	public void addResult(ResultInfo res) {
		results.add(res);
	}
}
