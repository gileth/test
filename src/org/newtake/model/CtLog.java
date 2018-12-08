package org.newtake.model;

import java.util.Date;

public class CtLog extends BaseEntity {

	
	private String id;
	
	private String luckyNumber;
	
	private String dateline;
	
	private String game100;
	
	private String game300;
	
	private String result300;
	
	private String special;
	
	private String groupNum;
	
	private Date catchTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLuckyNumber() {
		return luckyNumber;
	}

	public void setLuckyNumber(String luckyNumber) {
		this.luckyNumber = luckyNumber;
	}

	public String getDateline() {
		return dateline;
	}

	public void setDateline(String dateline) {
		this.dateline = dateline;
	}

	public String getGame100() {
		return game100;
	}

	public void setGame100(String game100) {
		this.game100 = game100;
	}

	public String getGame300() {
		return game300;
	}

	public void setGame300(String game300) {
		this.game300 = game300;
	}

	public String getResult300() {
		return result300;
	}

	public void setResult300(String result300) {
		this.result300 = result300;
	}

	public String getSpecial() {
		return special;
	}

	public void setSpecial(String special) {
		this.special = special;
	}

	public String getGroupNum() {
		return groupNum;
	}

	public void setGroupNum(String groupNum) {
		this.groupNum = groupNum;
	}

	public Date getCatchTime() {
		return catchTime;
	}

	public void setCatchTime(Date catchTime) {
		this.catchTime = catchTime;
	}
	
	
}
