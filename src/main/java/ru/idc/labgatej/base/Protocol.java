package ru.idc.labgatej.base;

import ru.idc.labgatej.model.HeaderInfo;
import ru.idc.labgatej.model.Order;
import ru.idc.labgatej.model.PacketInfo;
import ru.idc.labgatej.model.ResultInfo;

import java.util.List;

public interface Protocol {
	String makeOrder(List<Order> barcodes);
	PacketInfo parseMessage(String msg);
	HeaderInfo parseHeader(String msg);
	ResultInfo parseResult(String msg);
}