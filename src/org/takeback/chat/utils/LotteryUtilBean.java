// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.utils;

import org.joda.time.LocalDateTime;
import java.text.DecimalFormat;

public class LotteryUtilBean
{
    private static DecimalFormat decimalFormat;
    private String currentStage;
    private String[] currentLuckyNumber;
    private String nextStage;
    private boolean canBet;
    private LocalDateTime nextOpenTime;
    private long nextOpenRestTime;
    public static final long freezeBetTime = 60L;
    private static Integer SED;
    
    public LotteryUtilBean() {
        this.canBet = true;
        final LocalDateTime now = LocalDateTime.now();
        final int hour = now.getHourOfDay();
        final String date = now.toString("yyyyMMdd");
        long stage = 0L;
        if (hour >= 0 && hour < 2) {
            final LocalDateTime time0 = new LocalDateTime().withTime(0, 0, 0, 0);
            final long phase = (now.toDate().getTime() - time0.toDate().getTime()) / 60000L;
            stage = phase / 5L;
            this.nextOpenTime = time0.plusMinutes((int)(5L * (stage + 1L)));
        }
        else if (hour >= 2 && hour < 10) {
            stage = 23L;
            this.nextOpenTime = LocalDateTime.now().withTime(10, 0, 0, 0);
        }
        else if (hour >= 10 && hour < 22) {
            final LocalDateTime time2 = new LocalDateTime().withTime(10, 0, 0, 0);
            final long phase = (now.toDate().getTime() - time2.toDate().getTime()) / 60000L;
            stage = phase / 10L + 24L;
            this.nextOpenTime = time2.plusMinutes((int)(10L * (phase / 10L + 1L)));
        }
        else if (hour >= 22) {
            final LocalDateTime time3 = new LocalDateTime().withTime(22, 0, 0, 0);
            final long phase = (now.toDate().getTime() - time3.toDate().getTime()) / 60000L;
            stage = phase / 5L + 96L;
            this.nextOpenTime = time3.plusMinutes((int)(5L * (phase / 5L + 1L)));
        }
        long next = stage + 1L;
        if (stage == 0L) {
            next = 1L;
            stage = 120L;
            this.nextStage = date + LotteryUtilBean.decimalFormat.format(next);
            this.currentStage = now.minusDays(1).toString("yyyyMMdd") + LotteryUtilBean.decimalFormat.format(stage);
        }
        else {
            this.currentStage = date + LotteryUtilBean.decimalFormat.format(stage);
            this.nextStage = date + LotteryUtilBean.decimalFormat.format(next);
        }
        if (this.nextOpenTime.getHourOfDay() == 2) {
            this.nextOpenTime = this.nextOpenTime.withHourOfDay(10);
        }
        this.nextOpenRestTime = (this.nextOpenTime.toDate().getTime() - now.toDate().getTime()) / 1000L;
        this.canBet = (this.nextOpenRestTime - 60L > 0L);
    }
    
    public String getCurrentStage() {
        return this.currentStage;
    }
    
    public String getNextStage() {
        return this.nextStage;
    }
    
    public boolean isCanBet() {
        return this.canBet;
    }
    
    public long getNextOpenRestTime() {
        return this.nextOpenRestTime;
    }
    
    public String[] getCurrentLuckyNumber() {
        return this.currentLuckyNumber;
    }
    
    public void setCurrentLuckyNumber(final String currentLuckyNumber) {
        this.currentLuckyNumber = currentLuckyNumber.split(",");
    }
    
    public static String getSequence() {
        final StringBuffer sb = new StringBuffer(String.valueOf(System.currentTimeMillis()));
        synchronized (LotteryUtilBean.SED) {
            final StringBuffer sb2 = sb;
            final Integer sed = LotteryUtilBean.SED;
            ++LotteryUtilBean.SED;
            sb2.append(sed);
            if (LotteryUtilBean.SED % 100 == 0) {
                LotteryUtilBean.SED = 10;
            }
        }
        return sb.toString();
    }
    
    public static void main(final String[] args) {
        final LotteryUtilBean lub = new LotteryUtilBean();
        System.out.println(lub.getCurrentStage());
        System.out.println(lub.getNextStage());
        System.out.println(lub.getNextOpenRestTime());
    }
    
    static {
        LotteryUtilBean.decimalFormat = new DecimalFormat("000");
        LotteryUtilBean.SED = 10;
    }
}
