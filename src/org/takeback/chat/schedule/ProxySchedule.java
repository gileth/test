// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.schedule;

import java.util.Calendar;
import org.takeback.chat.utils.DateUtil;
import org.takeback.chat.entity.PcRateConfig;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import org.takeback.chat.entity.PcConfig;
import java.util.HashMap;
import org.takeback.chat.service.PcEggService;
import org.springframework.transaction.annotation.Transactional;
import java.util.Iterator;
import org.takeback.chat.entity.ProxyVote;
import java.util.Date;
import org.takeback.chat.entity.PcBackRecord;
import java.util.List;
import org.takeback.chat.entity.PubConfig;
import org.takeback.chat.service.admin.SystemConfigService;
import org.takeback.chat.utils.SmsUtil2;
import org.takeback.chat.entity.PubUser;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import org.takeback.chat.entity.GcRoom;
import org.takeback.chat.service.UserService;
import org.takeback.chat.entity.GcRoomMoney;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.dao.BaseDAO;
import org.springframework.stereotype.Service;

@Service
public class ProxySchedule
{
    @Autowired
    BaseDAO dao;
    
    @Transactional
    public void work() {
        final List<GcRoomMoney> ml = this.dao.findByHql("from GcRoomMoney");
        for (final GcRoomMoney grm : ml) {
            if (grm.getRestMoney() >= UserService.ROOM_FEE) {
                grm.setRestMoney(grm.getRestMoney() - UserService.ROOM_FEE);
            }
            else {
                if (grm.getRestMoney() > 0.0) {
                    final GcRoom rm = this.dao.get(GcRoom.class, grm.getRoomId());
                    final Integer uid = rm.getOwner();
                    this.dao.executeUpdate("update PubUser set money = money +:money  where id =:uid", (Map<String, Object>)ImmutableMap.of((Object)"money", (Object)grm.getRestMoney(), (Object)"uid", (Object)uid));
                }
                this.dao.executeUpdate("delete from GcRoom where id=:roomId", (Map<String, Object>)ImmutableMap.of((Object)"roomId", (Object)grm.getRoomId()));
                this.dao.executeUpdate("delete from GcRoomProperty where roomId=:roomId", (Map<String, Object>)ImmutableMap.of((Object)"roomId", (Object)grm.getRoomId()));
                this.dao.executeUpdate("delete from GcRoomMember where roomId=:roomId", (Map<String, Object>)ImmutableMap.of((Object)"roomId", (Object)grm.getRoomId()));
                this.dao.executeUpdate("delete from GcRoomMoney where roomId=:roomId", (Map<String, Object>)ImmutableMap.of((Object)"roomId", (Object)grm.getRoomId()));
            }
            if (grm.getRestMoney() <= UserService.ROOM_FEE * 4.0) {
                final Integer uid2 = this.dao.get(GcRoom.class, grm.getRoomId()).getOwner();
                final PubUser u = this.dao.get(PubUser.class, uid2);
                if (u.getMobile() == null) {
                    continue;
                }
                SmsUtil2.send(u.getMobile(), "\u60a8\u7684\u623f\u95f4\u4f59\u989d:" + grm.getRestMoney() + ",\u5f53\u4f4e\u4e8e50\u65f6\u623f\u95f4\u5c06\u88ab\u5220\u9664,\u8bf7\u53ca\u65f6\u5145\u503c!");
            }
        }
        final Map<String, List<Double>> waterConfigs = this.getWaterConfig();
        SystemConfigService.getInstance().getValue("b_rate");
        final Date startDate = this.getStartDate();
        final Date endDate = this.getEndDate();
        final String hql = "from PubUser where  userType<>'9' ";
        final List<PubUser> users = this.dao.findByHql(hql);
        for (final PubUser u2 : users) {
            if (u2.getId() == 156) {}
            if (u2.getParent() != null) {
                final List<Double> winSum = this.dao.findByHql("select coalesce(sum(inoutNum),0) from GcLotteryDetail where uid=:uid and createDate>:startDate and createDate<:endDate and inoutNum>0", (Map<String, Object>)ImmutableMap.of((Object)"uid", (Object)u2.getId(), (Object)"startDate", (Object)startDate, (Object)"endDate", (Object)endDate));
                final Double sum1 = winSum.get(0);
                final List<Double> loseSum = this.dao.findByHql("select coalesce(sum(inoutNum),0) from GcLotteryDetail where uid=:uid and createDate>:startDate and createDate<:endDate and inoutNum<0", (Map<String, Object>)ImmutableMap.of((Object)"uid", (Object)u2.getId(), (Object)"startDate", (Object)startDate, (Object)"endDate", (Object)endDate));
                final Double sum2 = loseSum.get(0);
                final Double sum3 = sum1 + Math.abs(sum2);
                final Double handRate = Double.valueOf(SystemConfigService.getInstance().getValue("conf_invit_rate"));
                if (handRate > 0.0) {
                    System.out.println(u2.getId() + "->" + u2.getParent() + ":" + sum3 + ">>" + sum3 * handRate);
                    this.dao.executeUpdate("update PubUser set money = money +:water where id =:uid", (Map<String, Object>)ImmutableMap.of((Object)"water", (Object)(sum3 * handRate), (Object)"uid", (Object)u2.getParent()));
                }
            }
            final PubConfig pc = this.dao.getUnique(PubConfig.class, "param", "water");
            final Double rate = Double.valueOf(pc.getVal());
            if (rate > 0.0) {
                if (rate >= 1.0) {
                    continue;
                }
                final String pcCountHql = "select coalesce(sum(userInout),0) from PcGameLog where uid=:uid and openTime>:startDate and openTime<:endDate";
                final List<Object> pcCounts = this.dao.findByHql(pcCountHql, (Map<String, Object>)ImmutableMap.of((Object)"uid", (Object)u2.getId(), (Object)"startDate", (Object)startDate, (Object)"endDate", (Object)endDate));
                final Double pcWater = Double.valueOf(pcCounts.get(0).toString());
                if (pcWater < 0.0) {
                    final Long count = this.dao.count("select count(*) from PcGameLog where uid =:uid and status <>0", (Map<String, Object>)ImmutableMap.of((Object)"uid", (Object)u2.getId()));
                    final Double waterMin = waterConfigs.get("w_min").get(0);
                    if (count >= waterMin) {
                        final Double water = this.getWater(waterConfigs, pcWater);
                        if (water > 0.0) {
                            final PcBackRecord backRecord = new PcBackRecord();
                            backRecord.setUid(u2.getId());
                            backRecord.setMoney(water);
                            backRecord.setUserInout(pcWater);
                            backRecord.setBackDate(new Date());
                            backRecord.setUserId(u2.getUserId());
                            this.dao.save(PcBackRecord.class, backRecord);
                            this.dao.executeUpdate("update PubUser set money = money +:water where id =:uid", (Map<String, Object>)ImmutableMap.of((Object)"water", (Object)water, (Object)"uid", (Object)u2.getId()));
                        }
                    }
                }
                if (!"2".equals(u2.getUserType())) {
                    continue;
                }
                final String proxyHql = "select coalesce(sum(freeze),0) from PcGameLog where parentId=:uid and (status='1' or status='2') and  betTime>:startDate and betTime<:endDate";
                if (u2.getId() == 156) {}
                final List<Object> proxySum = this.dao.findByHql(proxyHql, (Map<String, Object>)ImmutableMap.of((Object)"uid", (Object)u2.getId(), (Object)"startDate", (Object)startDate, (Object)"endDate", (Object)endDate));
                final Double teamWater = Double.valueOf(proxySum.get(0).toString());
                if (teamWater <= 0.0) {
                    continue;
                }
                final double proxyRate = this.getProxyBack(teamWater);
                final double backNum = teamWater * proxyRate;
                final ProxyVote v = new ProxyVote();
                v.setCacuDate(endDate);
                v.setUserId(u2.getUserId());
                v.setUid(u2.getId());
                v.setVote(backNum);
                v.setTotal(teamWater);
                final String addHql = "update PubUser set money=coalesce(money,0) + :vote where id =:uid";
                this.dao.executeUpdate(addHql, (Map<String, Object>)ImmutableMap.of((Object)"vote", (Object)backNum, (Object)"uid", (Object)u2.getId()));
                this.dao.save(ProxyVote.class, v);
            }
        }
    }
    
    double getProxyBack(final double water) {
        double[] steps;
        int i;
        for (steps = PcEggService.getSteps(SystemConfigService.getInstance().getValue("b_depart")), i = 0; i < steps.length && steps[i] < water; ++i) {}
        final double[] steps2 = PcEggService.getSteps(SystemConfigService.getInstance().getValue("b_rate"));
        return steps2[i];
    }
    
    private Double getWater(final Map<String, List<Double>> confs, Double money) {
        if (money >= 0.0) {
            return 0.0;
        }
        money = Math.abs(money);
        Double water = 0.0;
        for (int i = 1; i <= confs.size(); ++i) {
            final String key = "w_" + i;
            final List<Double> l = confs.get(key);
            if (l == null) {
                return water;
            }
            if (money < l.get(0)) {
                break;
            }
            water = money * l.get(1) / 100.0;
        }
        return water;
    }
    
    @Transactional
    public Map<String, List<Double>> getWaterConfig() {
        final Map<String, List<Double>> config = new HashMap<String, List<Double>>();
        final List<PcConfig> list = this.dao.findByHql("from PcConfig where param like 'w_%' order by id");
        for (final PcConfig c : list) {
            config.put(c.getParam(), this.getValues(c.getVal()));
        }
        return config;
    }
    
    private List<Double> getValues(final String text) {
        final List<Double> res = new ArrayList<Double>();
        final String pattern = "\u3010[0-9]+\u3011";
        final Pattern p = Pattern.compile(pattern);
        final Matcher m = p.matcher(text);
        while (m.find()) {
            res.add(Double.valueOf(m.group().replaceAll("[\u3010\u3011]", "")));
        }
        return res;
    }
    
    @Transactional(readOnly = true)
    public Map<String, List<PcRateConfig>> getPcRateConfigs() {
        final Map<String, List<PcRateConfig>> rates = new HashMap<String, List<PcRateConfig>>();
        final List<PcRateConfig> list = this.dao.findByHql("from PcRateConfig order by id, catalog ");
        for (final PcRateConfig config : list) {
            List<PcRateConfig> prc = rates.get(config.getCatalog());
            if (prc == null) {
                prc = new ArrayList<PcRateConfig>();
                rates.put(config.getCatalog(), prc);
            }
            prc.add(config);
        }
        return rates;
    }
    
    private Date getStartDate() {
        final Date d = DateUtil.getStartOfToday();
        final Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(11, -24);
        return c.getTime();
    }
    
    private Date getEndDate() {
        final Date d = DateUtil.getStartOfToday();
        final Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c.getTime();
    }
}
