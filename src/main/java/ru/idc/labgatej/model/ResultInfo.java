package ru.idc.labgatej.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class ResultInfo implements Serializable {
	private String device_name;
	private String instrument_id;
	private String sample_id;
	private String test_type;
	private String test_code;
	private String sample_type;
	private String priority;
	private String result;
	private Double dilution_factor;
	private String normal_range_flag;
	private String container_type;
	private String units;
	private String result_status;
	private String reagent_serial;
	private String reagent_lot;
	private String sequence_number;
	private String carrier;
	private String position;
	private Date test_started;
	private Date test_completed;
	private String comment;

	public ResultInfo() {
		sample_type = "SAMPLE";
		priority = "R";
		result_status = "F";
		normal_range_flag = "N";
	}
}
