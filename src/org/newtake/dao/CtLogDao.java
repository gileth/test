package org.newtake.dao;

import java.util.List;

import org.newtake.model.CtLog;

public interface CtLogDao {

	CtLog find(String luckyNumber);

	List<CtLog> getListByGame100(String game100);

	void updateLog(CtLog record);

	void saveLog(CtLog record);

}
