package ru.idc.labgatej.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.idc.labgatej.model.ArchiveInfo;
import ru.idc.labgatej.model.HeaderInfo;
import ru.idc.labgatej.model.ManufacturerRecord;
import ru.idc.labgatej.model.Order;
import ru.idc.labgatej.model.OrderInfo;
import ru.idc.labgatej.model.PacketInfo;
import ru.idc.labgatej.model.ResultInfo;

import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBManager {
	private static Logger logger = LoggerFactory.getLogger(DBManager.class);
	public Connection dbConnection = null;

	public void init(Configuration config) {
		//  Database credentials
		final String db_url = config.getParamValue("db.url");
		final String user = config.getParamValue("db.user");
		final String pass = config.getParamValue("db.password");

		logger.trace("Проверяем соединение с PostgreSQL JDBC");

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			logger.error("Не найден PostgreSQL JDBC Driver", e);
			return;
		}

		logger.trace("PostgreSQL JDBC Driver successfully connected");
		dbConnection = null;

		try {
			dbConnection = DriverManager.getConnection(db_url, user, pass);
		} catch (SQLException e) {
			logger.error("Не удалось подключиться к БД.", e);
			return;
		}

		if (dbConnection != null) {
			logger.info("Успешно подключились к БД");
		} else {
			logger.error("Не удалось подключиться к БД.");
		}
	}

	public void close() {
		if (dbConnection != null) {
			try {
				System.out.println("Closing connection to PostgreSQL");
				dbConnection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public List<Order> getOrders() throws SQLException {
		List<Order> result = new ArrayList<>();
		Statement statement = null;
		try {
			statement = dbConnection.createStatement();
			try {
				ResultSet rs = statement.executeQuery("SELECT * FROM lis.getTasks4CITM() ORDER BY is_aliquot, device_instance, p_route, test");
				try {
					while (rs.next()) {
						result.add(new Order(
							rs.getLong("task_id"),
							rs.getString("sample_id"),
							rs.getLong("device_instance"),
							rs.getString("p_device_code"),
							rs.getDouble("dilution_factor"),
							rs.getLong("test"),
							rs.getString("material"),
							rs.getString("test_code"),
							rs.getLong("cartnum"),
							rs.getString("fam"),
							rs.getString("sex"),
							rs.getDate("birthday"),
							rs.getLong("scheduled_profile"),
							rs.getLong("p_scheduled_invest"),
						  rs.getBoolean("is_aliquot"),
							rs.getLong("p_route"),
							rs.getLong("p_scheduled_container"),
							rs.getBoolean("p_manual_aliquot"),
							rs.getInt("p_task_type")
							));
					}
				} finally {
					rs.close();
				}
			} finally {
				statement.close();
			}
		} catch (SQLException e) {
			logger.error("", e);
			if ("This connection has been closed.".equals(e.getMessage())) {
				throw e;
			}
		}

		fillAliquots(result);

		return result;
	}

	private void fillAliquots(List<Order> orders) {
		if (orders.isEmpty()) return;

		String mainBarcode = orders.get(0).getBarcode();
		String aliquotBarcode = null;
		long routeId = -1;
		int idx = 1;
		for (Order order : orders) {
			if (order.getIsAliquot()) {
				if (routeId != order.getRouteId()) {
					routeId = order.getRouteId();
					aliquotBarcode = mainBarcode + "." + idx;
					idx++;
				}
				order.setAliquotBarcode(aliquotBarcode);
			}
		}
	}

	private String prepareString(String s) {
		return s != null && !s.isEmpty() ? s : null;
	}

	private String dateToSqlString(Date date) {
		if (date == null) return "NULL";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return dateFormat.format(date);
	}

	private java.sql.Date dateToSqlDate(Date date) {
		return date != null ? new java.sql.Date(date.getTime()) : null;
	}

	private String doubleToSqlString(Double d) {
		if (d == null) return "NULL";
		return String.valueOf(d);
	}

	public void saveResults(PacketInfo packetInfo, boolean isCitm) throws SQLException {
		HeaderInfo headerInfo = packetInfo.getHeader();
		OrderInfo orderInfo = packetInfo.getOrder();

		for (ResultInfo r: packetInfo.getResults()) {
			if (headerInfo != null && headerInfo.isQualityControl()) {
				r.setTest_type("CONTROL");
			}
			if (r.getSample_id() == null && orderInfo != null) {
				r.setSample_id(orderInfo.getBarcode());
			}
			if (isCitm) {
				saveResultCITM(r);
			} else {
				saveResult(r);
			}
		}
	}

	public void saveResult(ResultInfo res) throws SQLException {
		try {
			try (CallableStatement st = dbConnection.prepareCall("{call lis.add_raw_result2(" +
				"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {

				st.setString(1, prepareString(res.getDevice_name()));
				st.setString(2, prepareString(res.getInstrument_id()));
				st.setString(3, prepareString(res.getSample_id()));
				st.setString(4, prepareString(res.getTest_type()));
				st.setString(5, prepareString(res.getTest_code()));
				st.setString(6, prepareString(res.getSample_type()));
				st.setString(7, prepareString(res.getPriority()));
				st.setString(8, prepareString(res.getResult()));
				st.setString(9, doubleToSqlString(res.getDilution_factor()));
				st.setString(10, prepareString(res.getNormal_range_flag()));
				st.setString(11, prepareString(res.getContainer_type()));
				st.setString(12, prepareString(res.getUnits()));
				st.setString(13, prepareString(res.getResult_status()));
				st.setString(14, prepareString(res.getReagent_serial()));
				st.setString(15, prepareString(res.getReagent_lot()));
				st.setString(16, prepareString(res.getSequence_number()));
				st.setString(17, prepareString(res.getCarrier()));
				st.setString(18, prepareString(res.getPosition()));
				st.setDate(19, dateToSqlDate(res.getTest_started()));
				st.setDate(20, dateToSqlDate(res.getTest_completed()));
				st.setString(21, prepareString(res.getComment()));

				st.registerOutParameter(22, Types.BIGINT);

				st.execute();
			}
		} catch (SQLException e) {
			logger.error("", e);
			if ("This connection has been closed.".equals(e.getMessage())) {
				throw e;
			}
		}
	}

	public void saveResultCITM(ResultInfo res) throws SQLException {
		try {
			try (CallableStatement st = dbConnection.prepareCall("{call lis.add_raw_result_citm(" +
				"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {

				st.setString(1, prepareString(res.getDevice_name()));
				st.setString(2, prepareString(res.getInstrument_id()));
				st.setString(3, prepareString(res.getSample_id()));
				st.setString(4, prepareString(res.getTest_type()));
				st.setLong(5,  Long.parseLong(res.getTest_code()));
				st.setString(6, prepareString(res.getSample_type()));
				st.setString(7, prepareString(res.getPriority()));
				st.setString(8, prepareString(res.getResult()));
				st.setString(9, doubleToSqlString(res.getDilution_factor()));
				st.setString(10, prepareString(res.getNormal_range_flag()));
				st.setString(11, prepareString(res.getContainer_type()));
				st.setString(12, prepareString(res.getUnits()));
				st.setString(13, prepareString(res.getResult_status()));
				st.setString(14, prepareString(res.getReagent_serial()));
				st.setString(15, prepareString(res.getReagent_lot()));
				st.setString(16, prepareString(res.getSequence_number()));
				st.setString(17, prepareString(res.getCarrier()));
				st.setString(18, prepareString(res.getPosition()));
				st.setDate(19, dateToSqlDate(res.getTest_started()));
				st.setDate(20, dateToSqlDate(res.getTest_completed()));
				st.setString(21, prepareString(res.getComment()));

				st.registerOutParameter(22, Types.BIGINT);

				st.execute();
			}
		} catch (SQLException e) {
			logger.error("", e);
			if ("This connection has been closed.".equals(e.getMessage())) {
				throw e;
			}
		}
	}

	public void saveEvents(PacketInfo packetInfo) throws SQLException {
		if (packetInfo.getMRecords() == null || packetInfo.getMRecords().isEmpty()) return;

		for (ManufacturerRecord rec : packetInfo.getMRecords()) {
			// пока только информацию об архивировании пробирки умеем сохранять
			if (!(rec instanceof ArchiveInfo)) {
				continue;
			}
			try {
				try (CallableStatement st = dbConnection.prepareCall("{call lis.setContainerInTuberack(?, ?, ?)}")) {
					st.setString(1, prepareString(packetInfo.getOrder().getBarcode()));
					st.setString(2, prepareString(((ArchiveInfo) rec).getTuberack()));
					st.setInt(3, ((ArchiveInfo) rec).getPosition());

					st.execute();
				}
			} catch (SQLException e) {
				logger.error("", e);
				if ("This connection has been closed.".equals(e.getMessage())) {
					throw e;
				}
			}
		}
	}

	public void markOrderAsProcessed(long taskId) {
		try (Statement statement = dbConnection.createStatement()) {
			statement.executeUpdate("UPDATE lis.citm_query SET processed = now() WHERE citm_query_id = " + taskId);
		} catch (SQLException e) {
			logger.error("", e);
		}
	}

	public void markOrderAsFailed(long taskId, String msg) {
		try (Statement statement = dbConnection.createStatement()) {
			statement.executeUpdate("UPDATE lis.citm_query SET error_msg = " + msg + " WHERE citm_query_id = " + taskId);
		} catch (SQLException e) {
			logger.error("", e);
		}
	}

	public String genNewAliquotBarcode() {
		Statement statement = null;
		try {
			statement = dbConnection.createStatement();
			try {
				ResultSet rs = statement.executeQuery("select '0' || NEXTVAL('LIS.seqSecondBarcode')");
				rs.next();
				try {
					return rs.getString(1);
				} finally {
					rs.close();
				}
			} finally {
				statement.close();
			}
		} catch (SQLException e) {
			logger.error("", e);
		}
		return null;
	}

	public void registerAliquot(long scheduledContainerId, String barcode, long routeId) {
		try {
			try (CallableStatement st = dbConnection.prepareCall("{call lis.registersecondarycontainer(?, ?, ?, ?)}")) {

				st.setLong(1, scheduledContainerId);
				st.setString(2, prepareString(barcode));
				st.setNull(3, Types.BIGINT);
				st.setLong(4, routeId);

				st.execute();
			}
		} catch (SQLException e) {
			logger.error("", e);
		}
	}

	public void registerAliquots(List<Order> orders) {
		String aliquotBarcode = null;
		for (Order order: orders) {
			if (order.getIsAliquot() && !order.isManualAliquot()) {
				if (!order.getAliquotBarcode().equalsIgnoreCase(aliquotBarcode)) {
					aliquotBarcode = order.getAliquotBarcode();
					registerAliquot(order.getScheduledContainerId(), order.getAliquotBarcode(), order.getRouteId());
				}
			}
		}
	}

    public void savePakets(List<PacketInfo> packets, boolean isCitm) throws SQLException
    {
	    try {

	        dbConnection.setAutoCommit(false);

            for (PacketInfo packetInfo: packets) {
                saveResults(packetInfo, isCitm);
            }

            dbConnection.commit();
            dbConnection.setAutoCommit(true);
        } catch (SQLException e) {
	        dbConnection.rollback();
            logger.error("", e);
            throw e;
        }
    }
}
