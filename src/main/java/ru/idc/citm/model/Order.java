package ru.idc.citm.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
public class Order {
	private final Long taskId;
	private final String barcode;
	private final Long deviceInstanceId;
	private final String deviceCode;
	private final Double dilution_factor;
	private final Long testId;
	private final String material;
	private final String testCode;
	private final Long cartnum;
	private final String fam;
	private final String sex;
	private final Date birthday;
	private final Long scheduledProfileId;
	private final Long scheduledInvestId;
}
