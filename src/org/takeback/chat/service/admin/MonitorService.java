// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.takeback.chat.utils.NumberUtil;
import org.springframework.transaction.annotation.Transactional;
import org.takeback.chat.entity.ControlModel;
import org.takeback.util.exception.CodedBaseRuntimeException;
import java.util.HashMap;
import org.takeback.util.exp.ExpressionProcessor;
import org.takeback.util.converter.ConversionUtils;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.GameMonitor;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListServiceInt;

@Service("monitorService")
public class MonitorService extends MyListServiceInt
{
    @Autowired
    GameMonitor gameMonitor;
    
    @Override
    public Map<String, Object> list(final Map<String, Object> req) {
        final List<?> cnd = ConversionUtils.convert(req.get(MonitorService.CND), (Class<List<?>>)List.class);
        String filter = null;
        List<?> ls = null;
        if (cnd != null) {
            filter = ExpressionProcessor.instance().toString(cnd);
            if (filter.indexOf("and") > 0) {
                filter = filter.split("and")[1];
            }
            final String[] exp = filter.split("=");
            final String key = exp[0].replaceAll(" ", "");
            final String value = exp[1].replaceAll("'", "").replaceAll(" ", "").replaceAll("\\)", "");
            if ("".equals(key) || "".equals(value)) {
                ls = this.gameMonitor.userList();
            }
            else if ("uid".equals(key)) {
                ls = this.gameMonitor.listByUid(Integer.valueOf(value));
            }
            else if ("roomId".equals(key)) {
                ls = this.gameMonitor.listByRoomId(value);
            }
        }
        else {
            ls = this.gameMonitor.userList();
        }
        System.out.println("................>>" + filter + "::::" + this.gameMonitor.userList().size());
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("totalSize", ls.size());
        result.put("body", ls);
        return result;
    }
    
    @Transactional(readOnly = true)
    @Override
    public Object load(final Map<String, Object> req) {
        final Object pkey = req.get("id");
        final Long id = Long.valueOf(pkey.toString());
        final ControlModel c = this.gameMonitor.getById(id);
        if (c == null) {
            throw new CodedBaseRuntimeException("\u8bb0\u5f55\u4e22\u5931!");
        }
        this.afterLoad(c);
        return c;
    }
    
    @Transactional
    @Override
    public void save(final Map<String, Object> req) {
        final Map<String, Object> data = req.get("data");
        final Long id = Long.valueOf(data.get("id").toString());
        final Double targetRate = Double.valueOf(data.get("targetRateText").toString());
        final String suggests = (data.get("suggests") == null) ? "" : data.get("suggests").toString();
        final ControlModel c = this.gameMonitor.getById(id);
        if (c == null) {
            throw new CodedBaseRuntimeException("\u8bb0\u5f55\u4e22\u5931!");
        }
        c.setTargetRate(NumberUtil.round(targetRate / 100.0));
        c.setSuggests(suggests);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void clear(final Map<String, Object> req) {
        this.gameMonitor.cleanUsers();
    }
}
