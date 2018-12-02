// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import org.springframework.transaction.annotation.Transactional;
import java.util.Iterator;
import java.util.HashMap;
import org.takeback.chat.entity.PK10;
import java.util.ArrayList;
import com.google.common.collect.ImmutableMap;
import org.takeback.chat.utils.DateUtil;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.takeback.service.BaseService;

@Service
public class PK10Service extends BaseService
{
    @Transactional
    public Map<String, List<List<String>>> getData(final String date) {
        Date d = null;
        if (date != null) {
            final SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");
            try {
                d = format.parse(date);
            }
            catch (ParseException e) {
                d = new Date();
            }
        }
        if (d == null) {
            d = new Date();
        }
        final Date start = DateUtil.getStartOfTheDay(d);
        final Date end = DateUtil.getEndOfTheDay(d);
        final List<PK10> list = this.dao.findByHql("from PK10 where openTime>:start and openTime <:end order by number asc", (Map<String, Object>)ImmutableMap.of("start", (Object)start, "end", (Object)end));
        final List<List<String>> bs1 = new ArrayList<List<String>>();
        final List<List<String>> ds1 = new ArrayList<List<String>>();
        final List<List<String>> lh1 = new ArrayList<List<String>>();
        final List<List<String>> he = new ArrayList<List<String>>();
        for (int i = 0; i < 10; ++i) {
            final List<String> bsData = new ArrayList<String>();
            final List<String> dsData = new ArrayList<String>();
            for (final PK10 pk : list) {
                final String lucky = pk.getLucky().split(",")[i];
                final Integer num = Integer.parseInt(lucky);
                if (num > 5) {
                    bsData.add("\u5927[" + pk.getNumber() + ":" + lucky + "]");
                }
                else {
                    bsData.add("\u5c0f[" + pk.getNumber() + ":" + lucky + "]");
                }
                if (num % 2 == 0) {
                    dsData.add("\u53cc[" + pk.getNumber() + ":" + lucky + "]");
                }
                else {
                    dsData.add("\u5355[" + pk.getNumber() + ":" + lucky + "]");
                }
            }
            bs1.add(bsData);
            ds1.add(dsData);
        }
        for (int i = 0; i < 5; ++i) {
            final List<String> lhData = new ArrayList<String>();
            for (final PK10 pk2 : list) {
                final String lucky2 = pk2.getLucky().split(",")[i];
                final Integer num2 = Integer.parseInt(lucky2);
                final String lucky3 = pk2.getLucky().split(",")[9 - i];
                final Integer num3 = Integer.parseInt(lucky3);
                if (num2 > num3) {
                    lhData.add("\u9f99");
                }
                else {
                    lhData.add("\u864e");
                }
            }
            lh1.add(lhData);
        }
        final List<String> dshe = new ArrayList<String>();
        final List<String> dxhe = new ArrayList<String>();
        for (final PK10 pk2 : list) {
            final String lucky2 = pk2.getLucky().split(",")[0];
            final String lucky4 = pk2.getLucky().split(",")[1];
            final Integer num4 = Integer.parseInt(lucky2);
            final Integer num3 = Integer.parseInt(lucky4);
            if ((num4 + num3) % 2 == 0) {
                dshe.add("\u53cc");
            }
            else {
                dshe.add("\u5355");
            }
            if (num4 + num3 >= 11) {
                dxhe.add("\u5927");
            }
            else {
                dxhe.add("\u5c0f");
            }
        }
        he.add(dshe);
        he.add(dxhe);
        final Map<String, List<List<String>>> data = new HashMap<String, List<List<String>>>();
        data.put("daxiao", bs1);
        data.put("danshuang", ds1);
        data.put("longhu", lh1);
        data.put("he", he);
        final List<List<String>> newInfo = new ArrayList<List<String>>();
        final List<PK10> latest = this.dao.findByHqlPaging("from PK10 order by id desc ", 1, 1);
        if (latest != null && latest.size() != 0) {
            final PK10 newOne = latest.get(0);
            final List<String> newData = new ArrayList<String>();
            newData.add(newOne.getNumber());
            newData.add(newOne.getLucky());
            newData.add(newOne.getOpenTime().toString());
            newInfo.add(newData);
        }
        data.put("new", newInfo);
        return data;
    }
}
