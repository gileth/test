// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.controller;

import java.util.Iterator;
import java.util.HashMap;
import org.takeback.chat.entity.PcEggLog;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.WebUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.takeback.util.annotation.AuthPassport;
import org.takeback.chat.entity.PcRateConfig;
import java.util.List;
import java.util.Map;
import org.takeback.mvc.ResponseUtils;
import org.springframework.web.servlet.ModelAndView;
import org.takeback.chat.service.PK10Service;
import org.takeback.chat.service.PcEggService;
import org.takeback.chat.store.room.RoomStore;
import org.takeback.chat.store.user.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.UserService;
import org.springframework.stereotype.Controller;

@Controller
public class PcEggController
{
    @Autowired
    private UserService userService;
    @Autowired
    private UserStore userStore;
    @Autowired
    private RoomStore roomStore;
    @Autowired
    private PcEggService eggService;
    @Autowired
    private PK10Service pk10Service;
    
    @AuthPassport
    @RequestMapping(value = { "/rates" }, method = { RequestMethod.GET })
    public ModelAndView getRates() {
        final Map<String, List<PcRateConfig>> list = this.eggService.getPcRateConfigs();
        if (list != null && !list.isEmpty()) {
            return ResponseUtils.jsonView(200, "OK", list);
        }
        return ResponseUtils.jsonView(400, "no data.");
    }
    
    @AuthPassport
    @RequestMapping(value = { "/pc/bet" }, method = { RequestMethod.POST })
    public ModelAndView bet(@RequestBody final Map<String, String> params, final HttpServletRequest request) {
        final int num = Integer.parseInt(params.get("num"));
        final String key = params.get("key");
        final double money = Double.parseDouble(params.get("money"));
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        final String roomId = (String)WebUtils.getSessionAttribute(request, "roomId");
        try {
            this.eggService.bet(num, key, money, uid, roomId);
            return ResponseUtils.jsonView(200, num + "\u671f\u6210\u529f\u6295\u6ce8,\u795d\u541b\u597d\u8fd0!");
        }
        catch (Exception e) {
            return ResponseUtils.jsonView(500, e.getMessage());
        }
    }
    
    @AuthPassport
    @RequestMapping(value = { "/pc/cancelBet" }, method = { RequestMethod.POST })
    public ModelAndView cancelBet(@RequestBody final Map<String, String> params, final HttpServletRequest request) {
        final int num = Integer.parseInt(params.get("num"));
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        try {
            final double money = this.eggService.cancelBet(num, uid);
            return ResponseUtils.jsonView(200, "\u4f60\u5df2\u53d6\u6d88\u672c\u671f\u6240\u6709\u4e0b\u6ce8\uff1a" + money + "\u91d1\u5e01\uff0c\u8bf7\u6838\u5bf9\u8d26\u6237\u4f59\u989d\uff0c\u5982\u6709\u7591\u95ee\u8bf7\u7b2c\u4e00\u65f6\u95f4\u8054\u7cfb\u6211\u4eec\uff01");
        }
        catch (Exception e) {
            return ResponseUtils.jsonView(500, e.getMessage());
        }
    }
    
    @RequestMapping(value = { "/test/open" }, method = { RequestMethod.GET })
    public ModelAndView testOpen(final HttpServletRequest request) {
        final Integer num = 793399;
        final String exp = "8+2+4";
        final String lucky = "14";
        try {
            this.eggService.open(num, exp, lucky);
            return ResponseUtils.jsonView(200, "\u5f00\u5956\u6210\u529f!");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.jsonView(500, e.getMessage());
        }
    }
    
    @RequestMapping(value = { "/pk" }, method = { RequestMethod.GET })
    public ModelAndView pk(final String date, final HttpServletRequest request) {
        return ResponseUtils.jsonView(200, "ok", this.pk10Service.getData(date));
    }
    
    @RequestMapping({ "/pc/getPcEggLog" })
    public ModelAndView getPcEggLog(@RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize, final HttpServletRequest request) {
        if (pageNo <= 0) {
            pageNo = 1;
        }
        if (pageSize > 20) {
            pageSize = 20;
        }
        final String hql = "from PcEggLog order by id desc ";
        final List<PcEggLog> list = this.eggService.findByHql(hql, null, pageSize, pageNo);
        if (list == null || list.isEmpty()) {
            return ResponseUtils.jsonView(null);
        }
        final List<Map<String, Object>> records = new ArrayList<Map<String, Object>>(list.size());
        for (final PcEggLog pcEggLog : list) {
            final Map<String, Object> data = new HashMap<String, Object>();
            data.put("id", pcEggLog.getId());
            data.put("lucky", pcEggLog.getLucky());
            data.put("openTime", pcEggLog.getOpenTime());
            final Integer intVal = Integer.valueOf(pcEggLog.getLucky());
            if (intVal % 2 == 0) {
                data.put("dan", false);
            }
            else {
                data.put("dan", true);
            }
            if (intVal >= 14) {
                data.put("da", true);
            }
            else if (intVal <= 13) {
                data.put("da", false);
            }
            final PcEggService eggService = this.eggService;
            for (final int i : PcEggService.red) {
                if (i == intVal) {
                    data.put("color", "red");
                }
            }
            final PcEggService eggService2 = this.eggService;
            for (final int i : PcEggService.green) {
                if (i == intVal) {
                    data.put("color", "green");
                }
            }
            final PcEggService eggService3 = this.eggService;
            for (final int i : PcEggService.blue) {
                if (i == intVal) {
                    data.put("color", "blue");
                }
            }
            records.add(data);
        }
        return ResponseUtils.jsonView(records);
    }
}
