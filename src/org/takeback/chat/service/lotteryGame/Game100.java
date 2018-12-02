// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.lotteryGame;

import org.takeback.util.params.ParamUtils;

public class Game100 implements IGame
{
    private String luckyNumber;
    private String[] arr;
    String pos4_5;
    Double rate3;
    Double rate2;
    Double rate1;
    
    public Game100(final String luckyNumber) {
        this.rate3 = Double.valueOf(ParamUtils.getParam("game_100_6"));
        this.rate2 = Double.valueOf(ParamUtils.getParam("game_100_5"));
        this.rate1 = Double.valueOf(ParamUtils.getParam("game_100_1"));
        this.luckyNumber = luckyNumber;
        this.arr = luckyNumber.split(",");
        this.pos4_5 = new StringBuffer(this.arr[3]).append(this.arr[4]).toString();
    }
    
    @Override
    public How how(final String realBet) {
        if (!"6".equals(realBet)) {
            if (!"5".equals(realBet)) {
                if ("4".equals(realBet)) {
                    final Integer dx = Integer.valueOf(this.pos4_5);
                    if (dx < 50) {
                        return new How(this.rate1, realBet);
                    }
                    return new How(Double.valueOf(-1.0));
                }
                else if ("3".equals(realBet)) {
                    final Integer dx = Integer.valueOf(this.pos4_5);
                    if (dx > 49) {
                        return new How(this.rate1, realBet);
                    }
                    return new How(Double.valueOf(-1.0));
                }
                else if ("2".equals(realBet)) {
                    final Integer ds = Integer.valueOf(this.arr[4]);
                    if (ds % 2 == 0) {
                        return new How(this.rate1, realBet);
                    }
                    return new How(Double.valueOf(-1.0));
                }
                else if ("1".equals(realBet)) {
                    final Integer ds = Integer.valueOf(this.arr[4]);
                    if (ds % 2 != 0) {
                        return new How(this.rate1, realBet);
                    }
                    return new How(Double.valueOf(-1.0));
                }
            }
        }
        return new How(Double.valueOf(-1.0));
    }
}
