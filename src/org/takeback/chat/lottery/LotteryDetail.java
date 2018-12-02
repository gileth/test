// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.lottery;

import java.util.Date;
import java.math.BigDecimal;

public class LotteryDetail
{
    private Integer uid;
    private BigDecimal coin;
    private Date createDate;
    
    public LotteryDetail(final Integer uid, final BigDecimal coin) {
        this.uid = uid;
        this.coin = coin;
        this.createDate = new Date();
    }
    
    public Date getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(final Date createDate) {
        this.createDate = createDate;
    }
    
    public Integer getUid() {
        return this.uid;
    }
    
    public void setUid(final Integer uid) {
        this.uid = uid;
    }
    
    public BigDecimal getCoin() {
        return this.coin.setScale(2, 4);
    }
    
    public void setCoin(final BigDecimal coin) {
        this.coin = coin;
    }
    
    @Override
    public String toString() {
        return this.uid + "-" + this.coin;
    }
}
