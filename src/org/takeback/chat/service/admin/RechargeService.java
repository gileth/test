// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Date;
import java.io.Serializable;
import org.takeback.chat.entity.PubUser;
import org.takeback.chat.utils.DateUtil;
import java.util.HashMap;
import org.takeback.util.exception.CodedBaseRuntimeException;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListService;

@Service("rechargeService")
public class RechargeService extends MyListService
{
    @Transactional(readOnly = true)
    @Override
    public Map<String, Object> list(final Map<String, Object> req) {
        final String entityName = (String) req.get(RechargeService.ENTITYNAME);
        if (StringUtils.isEmpty((CharSequence)entityName)) {
            throw new CodedBaseRuntimeException(404, "missing entityName");
        }
        final int limit = (int) req.get(RechargeService.LIMIT);
        final int page = (int) req.get(RechargeService.PAGE);
        final Map<String, Object> myQuires = (Map<String, Object>) req.get("myQuires");
        final String orderInfo = (String) req.get(RechargeService.ORDERINFO);
        final StringBuffer hql = new StringBuffer(" from PubRecharge where status =2 ");
        final StringBuffer countHql = new StringBuffer("select count(*) from PubRecharge where status =2 ");
        final Map<String, Object> param = new HashMap<String, Object>();
        Date startTime = null;
        Date endTime = null;
        Long chargeTimes1 = null;
        Long chargeTimes2 = null;
        Double fee1 = null;
        Double fee2 = null;
        Integer queryUser = null;
        if (myQuires.containsKey("startTime") && myQuires.containsKey("endTime") && !"".equals(myQuires.get("startTime")) && !"".equals(myQuires.get("endTime"))) {
            startTime = DateUtil.getEndOfTheDay(myQuires.get("startTime").toString());
            endTime = DateUtil.getEndOfTheDay(myQuires.get("endTime").toString());
        }
        if (myQuires.containsKey("chargeTimes") && !"".equals(myQuires.get("chargeTimes"))) {
            final String[] chargeTimes3 = myQuires.get("chargeTimes").toString().split("-");
            chargeTimes1 = Long.valueOf(chargeTimes3[0]);
            chargeTimes2 = Long.valueOf(chargeTimes3[1]);
        }
        if (myQuires.containsKey("queryFee") && !"".equals(myQuires.get("queryFee"))) {
            final String[] fees = myQuires.get("queryFee").toString().split("-");
            fee1 = Double.valueOf(fees[0]);
            fee2 = Double.valueOf(fees[1]);
        }
        if (myQuires.containsKey("uid") && !"".equals(myQuires.get("uid"))) {
            queryUser = (Integer) myQuires.get("uid");
        }
        String idlimit = null;
        if (queryUser != null && !"".equals(queryUser)) {
            final PubUser user = this.dao.get(PubUser.class, (int)queryUser);
            if (user == null) {
                idlimit = "-1";
            }
            else {
                idlimit = user.getId().toString();
            }
        }
        else {
            idlimit = this.queryUids(chargeTimes1, chargeTimes2, startTime, endTime, fee1, fee2);
        }
        final Map<String, Object> param2 = new HashMap<String, Object>();
        if (startTime != null && endTime != null) {
            hql.append(" and finishtime>=:startTime and finishtime<=:endTime");
            countHql.append(" and finishtime>=:startTime and finishtime<=:endTime");
            param.put("startTime", startTime);
            param.put("endTime", endTime);
        }
        if (idlimit != null && idlimit.length() > 0) {
            hql.append(" and uid in (").append(idlimit).append(")");
            countHql.append(" and uid in (").append(idlimit).append(")");
        }
        hql.append(" order by ").append(orderInfo).append(" , uid ");
        final List<?> ls = this.dao.findByHqlPaging(hql.toString(), param, limit, page);
        final long count = this.dao.count(countHql.toString(), param);
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("totalSize", count);
        result.put("body", ls);
        return result;
    }
    
    @Transactional(readOnly = true)
    private String queryUids(final Long chargeTimes1, final Long chargeTimes2, final Date startTime, final Date endTime, final Double fee1, final Double fee2) {
        final StringBuffer uids = new StringBuffer();
        final StringBuffer limitHql = new StringBuffer(" select uid,count(*) as myCount ,sum(fee) as mySum from PubRecharge where status =2 ");
        final Map<String, Object> limitParam = new HashMap<String, Object>();
        if (startTime != null && endTime != null) {
            limitHql.append(" and finishtime>=:startTime and finishtime<=:endTime ");
            limitParam.put("startTime", startTime);
            limitParam.put("endTime", endTime);
        }
        limitHql.append(" group by uid ");
        if (fee1 != null && fee2 != null) {
            limitHql.append(" having sum(fee)>= :fee1 and sum(fee)<=:fee2 ");
            limitParam.put("fee1", fee1);
            limitParam.put("fee2", fee2);
        }
        if (chargeTimes1 != null && chargeTimes2 != null) {
            if (limitHql.indexOf("having") > 0) {
                limitHql.append(" and count(*)>= :count1 and count(*)<=:count2 ");
            }
            else {
                limitHql.append(" having count(*)>= :count1 and count(*)<=:count2 ");
            }
            limitParam.put("count1", chargeTimes1);
            limitParam.put("count2", chargeTimes2);
        }
        final StringBuffer ids = new StringBuffer();
        final List<Object[]> res = this.dao.findByHql(limitHql.toString(), limitParam);
        for (int i = 0; i < res.size(); ++i) {
            ids.append(res.get(i)[0]).append(",");
        }
        if (ids.length() > 0) {
            return ids.substring(0, ids.length() - 1);
        }
        return " -1 ";
    }
}
