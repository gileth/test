// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import java.util.List;
import java.util.HashMap;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;
import org.takeback.util.context.ContextUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import org.takeback.chat.entity.ValueControlLog;
import org.takeback.chat.utils.ValueControl;
import java.math.BigDecimal;
import org.takeback.chat.entity.PubUser;
import org.takeback.util.exception.CodedBaseRuntimeException;
import java.io.Serializable;
import org.takeback.chat.entity.GcRoom;
import java.util.Map;
import org.takeback.chat.store.user.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListServiceInt;

@Service("controlService")
public class ControlService extends MyListServiceInt
{
    @Autowired
    RoomStore roomStore;
    @Autowired
    UserStore userStore;
    
    @Transactional(rollbackFor = { Throwable.class })
    @Override
    public void save(final Map<String, Object> req) {
        final Map<String, Object> data = (Map<String, Object>) req.get("data");
        final String roomId = (String) data.get("roomId");
        final GcRoom r = this.dao.get(GcRoom.class, roomId);
        if (r == null) {
            throw new CodedBaseRuntimeException("错误的房间号!");
        }
        final Integer uid = Integer.valueOf(data.get("uid").toString());
        final PubUser u = this.dao.get(PubUser.class, uid);
        if (u == null) {
            throw new CodedBaseRuntimeException("错误的用户ID!");
        }
        final Double value = Double.valueOf(data.get("value").toString());
        ValueControl.setValue(roomId, uid, new BigDecimal(value));
        final ValueControlLog vcl = new ValueControlLog();
        vcl.setRoomId(roomId);
        vcl.setRoomName(r.getName());
        vcl.setUid(uid);
        vcl.setNickName(u.getNickName());
        vcl.setCreateDate(new Date());
        vcl.setVal(value);
        final HttpServletRequest request = (HttpServletRequest)ContextUtils.get("$httpRequest");
        vcl.setAdmin((String)WebUtils.getSessionAttribute(request, "$uid"));
        this.dao.save(ValueControlLog.class, vcl);
    }
    
    @Override
    public Map<String, Object> list(final Map<String, Object> req) {
        final int limit = (Integer) req.get(ControlService.LIMIT);
        final int page = (Integer) req.get(ControlService.PAGE);
        final List<?> ls = ValueControl.query();
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("totalSize", ls.size());
        result.put("body", ls);
        return result;
    }
}
