package ru.idc.labgatej.drivers;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import ru.idc.labgatej.base.TaskDualDriver;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.protocols.ProtocolASTM;
import ru.idc.labgatej.model.Order;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static ru.idc.labgatej.base.Codes.ENQ;
import static ru.idc.labgatej.base.Codes.EOT;
import static ru.idc.labgatej.base.Codes.STX;
import static ru.idc.labgatej.base.Consts.ERROR_TIMEOUT;

@Slf4j
public class CitmDriver extends TaskDualDriver<ProtocolASTM>
{
	@Override
	protected List<Order> getOrders()
	throws SQLException
	{
		return dbManager4tasks.getOrders();
	}

	@Override
	protected String protocolMakeOrder(List<Order> orders) {
		return protocol.makeOrder(orders);
	}

	@Override
	protected void markOrderAsProcessed(long taskId)
	{
		dbManager4tasks.markOrderAsProcessed(taskId);
	}

	@Override
	protected void registerAliquots(List<Order> orders) {
		dbManager4tasks.registerAliquots(orders);
	}

	@Override
	protected void markOrderAsFailed(long taskId, String comment) {
		dbManager4tasks.markOrderAsFailed(taskId, comment);
	}

	@Override
	public void init(ComboPooledDataSource poolConnections, Configuration config) {
		super.init(poolConnections, config);
		protocol = new ProtocolASTM();
	}

	@Override
	protected String receiveResults() throws IOException {
		StringBuilder sb = new StringBuilder();
		int res;
		do {
			res = transport4results.readInt(true);
			if (res == ERROR_TIMEOUT) {
				log.trace("ждём данных");
			} else if (res == ENQ) {
				log.debug("нам хотят что-то прислать");
				sb.setLength(0);
				transport4results.sendMessage("<ACK>");
			} else if (res == STX) {
				String msg = transport4results.readMessage();
				sb.append(msg);
				transport4results.sendMessage("<ACK>");
			} else if (res == EOT) {
				log.debug("мы зафиксировали конец передачи");
				System.out.println(sb.toString());
				return sb.toString();
			} else {
				log.error("ошибка протокола");
			}
		} while (res != ERROR_TIMEOUT);
		return null;
	}

	// для отладки
	private String makeResults() {

		return "1H|\\^&|6347||^^^|||||||P||20200517130941<CR><ETX>B6<CR><LF>" +
				   "2P|1||?||^||||||||||||||||||20200517122737||<CR><ETX>43<CR><LF>" +
			     "3O|1|20005480||ALL|?|20200517122737|||||X||||1||||||||||F<CR><ETX>20<CR><LF>" +
			     "4R|1|^^^108^1^^^COBAS_CCEE^^^^^^|3.890|mmol/l||N||F||&S&SYSTEM^System||20200517080941|COBAS_CCEE<CR><ETX>65<CR><LF>" +
			     "5L|1|N<CR><ETX>08<CR><LF>";

//		return "1H|\\^&|5653||^^^|||||||P||20200516145049<CR><ETX>B9<CR><LF>" +
//					 "2P|1||?||^||||||||||||||||||20200516131727||<CR><ETX>41<CR><LF>" +
//					 "3O|1|20005317||ALL|?|20200516131727|||||X||||1||||||||||F<CR><ETX>1D<CR><LF>" +
//					 "4R|1|^^^44^1^^^COBAS_CCEE^^^^^^|49.000|umol/l||N||C||&S&SYSTEM^System||20200516092429|COBAS_CCEE<CR><ETX>65<CR><LF>" +
//					 "5L|1|N<CR><ETX>08<CR><LF>";
	}
}
