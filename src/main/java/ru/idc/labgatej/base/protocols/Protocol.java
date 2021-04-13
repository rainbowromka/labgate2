package ru.idc.labgatej.base.protocols;

import ru.idc.labgatej.model.HeaderInfo;
import ru.idc.labgatej.model.Order;
import ru.idc.labgatej.model.OrderClass;
import ru.idc.labgatej.model.OrderInfo;
import ru.idc.labgatej.model.PacketInfo;
import ru.idc.labgatej.model.ResultInfo;

import java.util.List;

public interface Protocol<T> {
	String makeOrder(T barcodes);
	PacketInfo parseMessage(String msg);
	HeaderInfo parseHeader(String msg);
	OrderInfo parseOrder(String msg);
	List<ResultInfo> parseResults(String msg);
}
