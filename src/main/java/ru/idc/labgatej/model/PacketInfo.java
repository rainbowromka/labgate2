package ru.idc.labgatej.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PacketInfo {
	HeaderInfo header;
	OrderInfo order;
	List<ResultInfo> results = new ArrayList<>();
	// специфичная для каждого производителя информация
	List<ManufacturerRecord> mRecords = new ArrayList<>();

	public void addResult(ResultInfo res) {
		results.add(res);
	}

	public void addResult(List<ResultInfo> results) {
		this.results.addAll(results);
	}

	public void setDeviceCode(String code) {
		results.forEach(r -> r.setDevice_name(code));
	}

	public void addManufacturerRecord(ManufacturerRecord rec) {
		mRecords.add(rec);
	}
}
