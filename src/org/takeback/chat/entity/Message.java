// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import java.util.UUID;
import java.util.Date;

public class Message
{
    private String msgId;
    private String type;
    private Integer sender;
    private String nickName;
    private String headImg;
    private Object content;
    private Date msgTime;
    private String cmd;
    public static final String TXT = "TXT";
    public static final String RED = "RED";
    public static final String IMG = "IMG";
    public static final String VOC = "VOC";
    public static final String ORD = "ORD";
    public static final String CMD = "CMD";
    public static final String TXT_SYS = "TXT_SYS";
    public static final String TXT_ALERT = "TXT_ALERT";
    public static final String RED_SYS = "RED_SYS";
    public static final String PC_MSG = "PC_MSG";
    
    public Message() {
        this.type = "TXT";
        this.msgId = UUID.randomUUID().toString().replace("-", "");
    }
    
    public Message(final String type, final Integer sender, final Object content) {
        this();
        this.type = type;
        this.sender = sender;
        this.content = content;
    }
    
    public Message(final String cmd, final Object cmdContent) {
        this("ORD", 0, cmdContent);
        this.cmd = cmd;
    }
    
    public String getMsgId() {
        return this.msgId;
    }
    
    public void setMsgId(final String msgId) {
        this.msgId = msgId;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public Integer getSender() {
        return this.sender;
    }
    
    public void setSender(final Integer sender) {
        this.sender = sender;
    }
    
    public String getNickName() {
        return this.nickName;
    }
    
    public void setNickName(final String nickName) {
        this.nickName = nickName;
    }
    
    public String getHeadImg() {
        return this.headImg;
    }
    
    public void setHeadImg(final String headImg) {
        this.headImg = headImg;
    }
    
    public Object getContent() {
        return this.content;
    }
    
    public void setContent(final Object content) {
        this.content = content;
    }
    
    public Date getMsgTime() {
        if (this.msgTime == null) {
            this.msgTime = new Date();
        }
        return this.msgTime;
    }
    
    public void setMsgTime(final Date msgTime) {
        this.msgTime = msgTime;
    }
    
    public String getCmd() {
        return this.cmd;
    }
    
    public void setCmd(final String cmd) {
        this.cmd = cmd;
    }
}
