package ru.idc.labgatej.drivers;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.idc.labgatej.base.Codes;
import ru.idc.labgatej.base.DBManager;
import ru.idc.labgatej.base.SocketClientTransport;
import ru.idc.labgatej.base.TaskDualDriver;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.Transport;
import ru.idc.labgatej.base.protocols.ProtocolASTM;
import ru.idc.labgatej.model.Order;
import ru.idc.labgatej.model.PacketInfo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static ru.idc.labgatej.base.Codes.*;
import static ru.idc.labgatej.base.Codes.makeSendable;
import static ru.idc.labgatej.base.Consts.ERROR_TIMEOUT;

@Slf4j
public class CitmDriver extends TaskDualDriver<ProtocolASTM>
{
	private static final Logger eventsLogger = LoggerFactory.getLogger("events");

	private boolean can_send_tasks = false;
	private boolean can_receive_results = false;
	private boolean can_receive_events = false;

	private Transport transport4events;
	private DBManager dbManager4events;
	private boolean createAliquot;
	private Long delayForTasks;
	private Long delayForResults;
	////////////////

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

		this.dbManager4events = new DBManager();
		log.trace("Инициализация третьего подключения к БД...");
		dbManager4events.init(poolConnections);

		String s = config.getParamValue("citm.delay.tasks");
		delayForTasks = s != null
			? Long.parseLong(s)
			: 50;

		s = config.getParamValue("citm.delay.results");
		delayForResults = s != null
			? Long.parseLong(config.getParamValue("citm.delay.results"))
			: 50;

		s =	config.getParamValue("citm.send_tasks");
		can_send_tasks = s == null || Boolean.parseBoolean(s);

		s =	config.getParamValue("citm.receive_results");
		can_receive_results = s == null || Boolean.parseBoolean(s);

		s =	config.getParamValue("citm.receive_events");
		can_receive_events = s == null || Boolean.parseBoolean(s);

		createAliquot = !"off".equalsIgnoreCase(config.getParamValue("citm.aliquot"));

		if (can_send_tasks) {
			transport4tasks = new SocketClientTransport(config.getParamValue("citm.server"), Integer.parseInt(config.getParamValue("citm.port.task")));
			transport4tasks.init(2000);
		}
		if (can_receive_results) {
			transport4results = new SocketClientTransport(config.getParamValue("citm.server"), Integer.parseInt(config.getParamValue("citm.port.result")));
			transport4results.init(10000);
		}
		if (can_receive_events) {
			transport4events = new SocketClientTransport(config.getParamValue("citm.server"), Integer.parseInt(config.getParamValue("citm.port.events")));
			transport4events.init(10000);
		}

		protocol = new ProtocolASTM();
	}

	@Override
	public void close() {
		if (transport4events != null) {
			transport4events.close();
		}

		super.close();
	}

	private String receiveEvents()  throws IOException {
		StringBuilder sb = new StringBuilder();
		int res;
		do {
			res = transport4events.readInt(true);
			if (res == ERROR_TIMEOUT) {
				eventsLogger.trace("ждём данных");
			} else if (res == ENQ) {
				eventsLogger.debug("нам хотят что-то прислать");
				sb.setLength(0);
				transport4events.sendMessage("<ACK>");
			} else if (res == STX) {
				String msg = transport4events.readMessage();
				sb.append(msg);
				transport4events.sendMessage("<ACK>");
			} else if (res == EOT) {
				eventsLogger.debug("мы зафиксировали конец передачи");
				System.out.println(sb.toString());
				return sb.toString();
			} else {
				eventsLogger.error("ошибка протокола");
			}
		} while (res != ERROR_TIMEOUT);
		return null;
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

	@Override
	public void sendTasks() throws IOException, SQLException {
		String msg;
		List<Order> orders;
		boolean hasErrors = false;
		long taskId;
		int cnt = 0;
		int res;
		do {
			orders = getOrders();
			msg = protocolMakeOrder(orders);
			if (!msg.isEmpty()) {
				taskId = orders.get(0).getTaskId();
				transport4tasks.sendMessage("<ENQ>");
				res = transport4tasks.readInt();
				if (res == Codes.ACK) {
					log.debug("нас готовы слушать");
					transport4tasks.sendMessage(msg);
					res = transport4tasks.readInt();
					if (res == Codes.ACK) {
						log.debug("Наше сообщение приняли");
						// нужно помечать задание как обработанное в БД
						markOrderAsProcessed(taskId);
						if (createAliquot) {
							registerAliquots(orders);
						}
					} else if (res == Codes.NAK) {
						log.debug("Наше сообщение не понравилось почему-то");
						hasErrors = true;
						markOrderAsFailed(taskId, "Наше сообщение не понравилось почему-то");
					}	else {
						log.error("ошибка протокола");
						hasErrors = true;
						markOrderAsFailed(taskId, "ошибка протокола");
					}
					transport4tasks.sendMessage("<EOT>");
				} else if (res == Codes.NAK) {
					log.debug("нас не готовы слушать, ждём 10 секунд");
					// надо подождать не меньше 10 секунд
					hasErrors = true;
				} else {
					if (res == Codes.ENQ) {
						log.error("нас перебили");
					} else {
						log.error("ошибка протокола");
					}
					hasErrors = true;
				}
				cnt++;
			}
		} while (!hasErrors && !msg.isEmpty() && cnt < 5);
	}

	@Override
	public void loop() throws IOException, InterruptedException, SQLException {
		if (can_send_tasks && !transport4tasks.isReady()) return;
		if (can_receive_results && !transport4results.isReady()) return;
		if (can_receive_events && !transport4events.isReady()) return;

		Runnable task = () -> {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					sendTasks();
					Thread.sleep(delayForTasks);
				}
			} catch (Exception e) {
				log.error("Ошибка в потоке отправки заданий", e);
				Thread.currentThread().interrupt();
			}
		};

		Thread tasksThread = new Thread(task);
		if (can_send_tasks) {
			tasksThread.start();
		}

		//---------------
		Runnable events = () -> {
			try {
				String msg = null;
				while (!Thread.currentThread().isInterrupted()) {
					try {
						msg = receiveEvents();

						if (msg != null && !msg.isEmpty()) {
							eventsLogger.info(msg);
							PacketInfo packetInfo = protocol.parseMessage(makeSendable(msg));
							dbManager4results.saveEvents(packetInfo);
						}
					} catch (Exception e) {
						log.error("Ошибка в потоке получения событий", e);
						// пока просто игнорируем ошибки для уведомлений
					}
					Thread.sleep(delayForResults);
				}
			} catch (Exception e) {
				log.error("Ошибка в потоке получения событий", e);
				Thread.currentThread().interrupt();
			}
		};

		Thread eventsThread = new Thread(events);
		if (can_receive_events) {
			eventsThread.start();
		}

		String msg = null;
		while (true) {
			if (can_send_tasks && !tasksThread.isAlive()) {
				throw new IOException();
			}

			if (can_receive_events && !eventsThread.isAlive()) {
				throw new IOException();
			}

			msg = can_receive_results ? receiveResults() : null;

			if (msg != null && !msg.isEmpty()) {
				PacketInfo packetInfo = protocol.parseMessage(makeSendable(msg));
				dbManager4results.saveResults(packetInfo, true);
			}
			Thread.sleep(delayForResults);
		}
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

	// для отладки
	private String makeEvents() {
		return "1H|\\^&|5763545||^^^|||||||P||20210319125020<CR>P|1||?||^||||||||||||||||||20210316083003|||||||||<CR>" +
			"O|1|20216800|^18^74|^^^^^^^COBAS_P512|||||||X|||20210319125019|1|||||||||COBAS_P512|P<CR>" +
			"M|1|EQU^RO^^1.0|COBAS_P512^^^cobas p512^LAB1^SAMPLEEVENT^ARC<ETB_>77<CR><LF>" +
			"2HIV|20210319125019<CR>" +
			"M|2|SAC^RO^^1.0|||20216800|||1|20210319125019|P||18|74||||{cobas p512} {cobas p512.ARCHIVE_S} {18 (74)}<CR>" +
			"L|1|N<CR>" +
			"<ETX>7D<CR><LF>";
	}

}
