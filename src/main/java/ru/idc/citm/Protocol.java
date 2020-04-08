package ru.idc.citm;

import java.util.List;

public interface Protocol {
	String makeOrder(List<String> barcodes);
}
