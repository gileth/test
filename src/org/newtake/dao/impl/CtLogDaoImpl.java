package org.newtake.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.newtake.dao.CtLogDao;
import org.newtake.model.CtLog;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import com.mysql.jdbc.Statement;

@Repository
public class CtLogDaoImpl extends BaseDaoImpl implements CtLogDao {

	@Override
	public CtLog find(String luckyNumber) {
		String sql = "select id, luckyNumber, dateline, game100, game300, result300, special, groupNum, catchTime from ct_log where luckyNumber = ? limit 1";

		return find(sql, CtLog.class, luckyNumber);
	}

	@Override
	public List<CtLog> getListByGame100(String game100) {
		String sql = "select id, luckyNumber, dateline, game100, game300, result300, special, groupNum, catchTime from ct_log where game100 = ?";
		return this.findList(CtLog.class, sql, game100);
	}

	@Override
	public void updateLog(CtLog record) {
		this.jdbcTemplate.update("update ct_log set game100=?,game300=? ,result300=? where luckyNumber=? ",
				record.getGame100(), record.getGame300(), record.getResult300(), record.getLuckyNumber());
	}

	@Override
	public void saveLog(CtLog record) {
		String sql = "INSERT INTO ct_log (id, luckyNumber, dateline, game100, game300, result300, special, groupNum, catchTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, record.getId());
				ps.setString(2, record.getLuckyNumber());
				ps.setString(3, record.getDateline());
				ps.setString(4, record.getGame100());
				ps.setString(5, record.getGame300());
				ps.setString(6, record.getResult300());
				ps.setString(7, record.getSpecial());
				ps.setString(8, record.getGroupNum());
				ps.setTimestamp(9, record.getCatchTime() != null ? new Timestamp(record.getCatchTime().getTime())
						: new Timestamp(new Date().getTime()));
				return ps;
			}

		});
	}

}
