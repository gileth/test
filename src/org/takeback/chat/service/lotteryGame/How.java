// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.lotteryGame;

public class How
{
    private Double num;
    private String luckyNum;
    
    public How(final Double num) {
        this.num = num;
    }
    
    public How(final String luckyNum) {
        this.luckyNum = luckyNum;
    }
    
    public How(final Double num, final String luckyNum) {
        this.luckyNum = luckyNum;
        this.num = num;
    }
    
    public String getLuckyNum() {
        return this.luckyNum;
    }
    
    public void setLuckyNum(final String luckyNum) {
        this.luckyNum = luckyNum;
    }
    
    public Double getNum() {
        return this.num;
    }
    
    public void setNum(final Double num) {
        this.num = num;
    }
}
