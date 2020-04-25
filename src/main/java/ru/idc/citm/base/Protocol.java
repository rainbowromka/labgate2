package ru.idc.citm.base;

import ru.idc.citm.model.HeaderInfo;
import ru.idc.citm.model.Order;
import ru.idc.citm.model.PacketInfo;
import ru.idc.citm.model.ResultInfo;

import java.util.List;

public interface Protocol {
	String makeOrder(List<Order> barcodes);
	PacketInfo parseMessage(String msg);
	HeaderInfo parseHeader(String msg);
	ResultInfo parseResult(String msg);
}
