// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import org.slf4j.Logger;
import org.takeback.chat.service.lotteryGame.IGame;
import java.util.Map;
import org.takeback.chat.service.lotteryGame.Game100;
import java.util.List;
import org.springframework.stereotype.Service;
import org.takeback.service.BaseService;

@Service
public class LotteryOpenService extends BaseService
{
    private String sscNum;
    private String openNum;
    private List<Integer> users;
    private Game100 game100;
    private Map<String, IGame> games;
    private static final Logger log;
    
    public LotteryOpenService() {
        this.games = new HashMap<String, IGame>();
    }
    
    public void set(final String sscNum, final String openNum, final List<Integer> users) {
        this.sscNum = sscNum;
        this.openNum = openNum;
        this.users = users;
        this.game100 = new Game100(openNum);
        this.games.put("100", this.game100);
    }
    
    @Transactional
    public void run() {
    }
    
    static {
        log = LoggerFactory.getLogger((Class)LotteryOpenService.class);
    }
}
