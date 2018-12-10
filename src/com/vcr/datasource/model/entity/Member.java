package com.vcr.datasource.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Member implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long uid;

    private String account;

    private Integer level;

    private String mobile;

    private String pwd;

    private String content;

    private Date createtime;

    private Short status;

    private String nickname;

    private Double credit;

    private Double balance;

    private String avatar;

    private String openid_qq;

    private String openid_wx;

    private String unionid_wx;

    private String steamurl;

    private Integer cachestatus;
    
    /**
     * 注册ip
     */
	private String reg_ip;	
	/**
     * 注册时间
     */
	private Date reg_time;	
	/**
     * 注册地址
     */
	private String reg_addr;	
	/**
     * 最后登录ip
     */
	private String lastlogin_ip;	
	/**
     * 最后登录时间
     */
	private Date lastlogin_time;	
	/**
     * 注册渠道id
     */
	private Integer reg_channelid;	
	/**
     * 注册来源(1：微信，2：QQ)
     */
	private Integer reg_res;	
    
    /**
    * 消费次数
    */
	private Integer expense_num;	
	/**
    * 总消费金额（排除余额、金钥匙支付）
    */
	private BigDecimal expense_amount;	
	/**
    * 仓库物品数
    */
	private Integer pro_num;
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Short getStatus() {
		return status;
	}
	public void setStatus(Short status) {
		this.status = status;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Double getCredit() {
		return credit;
	}
	public void setCredit(Double credit) {
		this.credit = credit;
	}
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getOpenid_qq() {
		return openid_qq;
	}
	public void setOpenid_qq(String openid_qq) {
		this.openid_qq = openid_qq;
	}
	public String getOpenid_wx() {
		return openid_wx;
	}
	public void setOpenid_wx(String openid_wx) {
		this.openid_wx = openid_wx;
	}
	public String getUnionid_wx() {
		return unionid_wx;
	}
	public void setUnionid_wx(String unionid_wx) {
		this.unionid_wx = unionid_wx;
	}
	public String getSteamurl() {
		return steamurl;
	}
	public void setSteamurl(String steamurl) {
		this.steamurl = steamurl;
	}
	public Integer getCachestatus() {
		return cachestatus;
	}
	public void setCachestatus(Integer cachestatus) {
		this.cachestatus = cachestatus;
	}
	public String getReg_ip() {
		return reg_ip;
	}
	public void setReg_ip(String reg_ip) {
		this.reg_ip = reg_ip;
	}
	public Date getReg_time() {
		return reg_time;
	}
	public void setReg_time(Date reg_time) {
		this.reg_time = reg_time;
	}
	public String getReg_addr() {
		return reg_addr;
	}
	public void setReg_addr(String reg_addr) {
		this.reg_addr = reg_addr;
	}
	public String getLastlogin_ip() {
		return lastlogin_ip;
	}
	public void setLastlogin_ip(String lastlogin_ip) {
		this.lastlogin_ip = lastlogin_ip;
	}
	public Date getLastlogin_time() {
		return lastlogin_time;
	}
	public void setLastlogin_time(Date lastlogin_time) {
		this.lastlogin_time = lastlogin_time;
	}
	public Integer getReg_channelid() {
		return reg_channelid;
	}
	public void setReg_channelid(Integer reg_channelid) {
		this.reg_channelid = reg_channelid;
	}
	public Integer getReg_res() {
		return reg_res;
	}
	public void setReg_res(Integer reg_res) {
		this.reg_res = reg_res;
	}
	public Integer getExpense_num() {
		return expense_num;
	}
	public void setExpense_num(Integer expense_num) {
		this.expense_num = expense_num;
	}
	public BigDecimal getExpense_amount() {
		return expense_amount;
	}
	public void setExpense_amount(BigDecimal expense_amount) {
		this.expense_amount = expense_amount;
	}
	public Integer getPro_num() {
		return pro_num;
	}
	public void setPro_num(Integer pro_num) {
		this.pro_num = pro_num;
	}
	@Override
	public String toString() {
		return "Member [uid=" + uid + ", account=" + account + ", mobile=" + mobile + ", createtime=" + createtime
				+ ", status=" + status + ", nickname=" + nickname + ", balance=" + balance + ", avatar=" + avatar
				+ ", reg_ip=" + reg_ip + "]";
	}	

}