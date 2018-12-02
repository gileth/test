// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import org.springframework.web.util.WebUtils;
import org.takeback.util.context.ContextUtils;
import javax.servlet.http.HttpServletRequest;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import org.takeback.chat.entity.GcRoom;
import org.takeback.util.exception.CodedBaseRuntimeException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListServiceInt;

@Service("roomFeeService")
public class RoomFeeService extends MyListServiceInt
{
    @Autowired
    RoomStore roomStore;
    
    @Transactional
    @Override
    public void save(final Map<String, Object> req) {
        final Map<String, Object> data = (Map<String, Object>) req.get("data");
        final String roomId = (String) data.get("roomId");
        if (roomId == null) {
            throw new CodedBaseRuntimeException("\u623f\u95f4\u53f7\u4e0d\u80fd\u4e3a\u7a7a");
        }
        final GcRoom rm = this.dao.get(GcRoom.class, roomId);
        if (rm == null) {
            throw new CodedBaseRuntimeException("\u9519\u8bef\u7684\u623f\u95f4\u53f7");
        }
        Double fee = 0.0;
        try {
            fee = Double.valueOf((String) data.get("val"));
        }
        catch (Exception e) {
            throw new CodedBaseRuntimeException("\u9519\u8bef\u7684\u6570\u503c");
        }
        final String hql = "update GcRoom set sumfee = COALESCE(sumfee,0) - :val where sumfee>:val and  id=:roomId";
        final int effected = this.dao.executeUpdate(hql, ImmutableMap.of("val", fee, "roomId", roomId));
        if (effected == 0) {
            throw new CodedBaseRuntimeException("\u8d85\u51fa\u53ef\u7528\u989d\u5ea6\uff1a" + rm.getSumFee());
        }
        final HttpServletRequest request = (HttpServletRequest)ContextUtils.get("$httpRequest");
        final String admin = (String)WebUtils.getSessionAttribute(request, "$uid");
        data.put("admin", admin);
        data.put("createDate", new Date());
        data.put("roomName", rm.getName());
        super.save(req);
    }
}
