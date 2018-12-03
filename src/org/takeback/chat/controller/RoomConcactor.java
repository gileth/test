// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.controller;

import org.takeback.chat.service.admin.SystemConfigService;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import java.util.Date;
import java.util.HashMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.takeback.util.annotation.AuthPassport;
import org.takeback.chat.store.user.User;
import org.takeback.chat.entity.GcRoomMember;
import java.util.Iterator;
import org.takeback.chat.entity.GcRoom;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.takeback.chat.entity.GcRoomKickLog;
import java.util.List;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import org.takeback.chat.utils.DateUtil;
import org.springframework.web.util.WebUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMethod;
import org.takeback.mvc.ResponseUtils;
import java.io.Serializable;
import org.takeback.chat.store.room.Room;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import org.takeback.chat.store.user.UserStore;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.RoomService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping({ "/room" })
public class RoomConcactor
{
    @Autowired
    private RoomService roomService;
    @Autowired
    private RoomStore roomStore;
    @Autowired
    private UserStore userStore;
    private final Lock lock;
    
    public RoomConcactor() {
        this.lock = new ReentrantLock();
    }
    
    @RequestMapping(value = { "/{id}" }, method = { RequestMethod.GET })
    public ModelAndView getRoom(@PathVariable final String id) {
        final Room room = this.roomStore.get(id);
        if (room == null) {
            return ResponseUtils.jsonView(404, "\u623f\u95f4" + id + "\u4e0d\u5b58\u5728.");
        }
        System.out.println("");
        return ResponseUtils.jsonView(200, "success", room);
    }
    
    @RequestMapping(value = { "/authorize" }, method = { RequestMethod.POST })
    public ModelAndView validatePassword(@RequestBody final Map<String, String> params) {
        final String roomId = params.get("roomId");
        final String password = params.get("password");
        final Room room = this.roomStore.get(roomId);
        if (room == null) {
            return ResponseUtils.jsonView(404, "\u623f\u95f4" + roomId + "\u4e0d\u5b58\u5728.");
        }
        if (!room.isNeedPsw() || password.equals(room.getPsw())) {
            return ResponseUtils.jsonView(200, "success");
        }
        return ResponseUtils.jsonView(401, "incorrect password");
    }
    
    @RequestMapping({ "/join/{id}" })
    public ModelAndView joinIn(@PathVariable final String id, final HttpServletRequest request) {
        final Room room = this.roomStore.get(id);
        if (room == null) {
            return ResponseUtils.jsonView(404, "\u623f\u95f4" + id + "\u4e0d\u5b58\u5728.");
        }
        final String lastRoomId = (String)WebUtils.getSessionAttribute(request, "roomId");
        if (lastRoomId != null) {
            WebUtils.setSessionAttribute(request, "lastRoomId", lastRoomId);
        }
        WebUtils.setSessionAttribute(request, "roomId", id);
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        if (uid != null) {
            try {
                this.lock.lock();
                if (room.getPosition() >= room.getLimitNum()) {
                    return ResponseUtils.jsonView(530, "\u623f\u95f4\u5df2\u6ee1.");
                }
            }
            finally {
                this.lock.unlock();
            }
        }
        final List<GcRoomKickLog> kickLogs = this.roomService.findByHql("from GcRoomKickLog where kickTime>=:t and  roomId=:roomId and uid =:uid ",ImmutableMap.of("t", DateUtil.getDateBeforeSeconds(RoomService.KICK_TIME), "roomId", id, "uid", uid), 1, 1);
        if (kickLogs.size() > 0) {
            return ResponseUtils.jsonView(500, "\u7981\u6b62\u8fdb\u5165\u623f\u95f4.");
        }
        final Map<Object, Object> body = (Map<Object, Object>)Maps.newHashMap();
        body.put("room", room);
        body.put("uid", uid);
        return ResponseUtils.jsonView(200, "success", body);
    }
    
    @RequestMapping({ "/list/{pageNo}" })
    public ModelAndView rooms(@PathVariable final Integer pageNo, final HttpServletRequest request) {
        String s = "";
        final Map<String, Object> params = new HashMap<String,Object>();
        final String cata = request.getParameter("cata");
        final String type = request.getParameter("type");
        if (!StringUtils.isEmpty((CharSequence)cata)) {
            s = " and a.catalog = :p";
            params.put("p", cata);
        }
        if (!StringUtils.isEmpty((CharSequence)type)) {
            s = " and a.type = :p";
            params.put("p", type);
        }
        final List<GcRoom> rooms = this.roomService.findByHql("from GcRoom a where a.status !='9'" + s + " order by a.hot desc, a.createdate desc", params, 100, pageNo);
        if (rooms == null || rooms.size() == 0) {
            return ResponseUtils.jsonView(new ArrayList());
        }
        final List<Room> result = Lists.newArrayList();
        for (final GcRoom room : rooms) {
            result.add(this.roomStore.get(room.getId()));
        }
        return ResponseUtils.jsonView(result);
    }
    
    @AuthPassport
    @RequestMapping({ "/members/{id}" })
    public ModelAndView members(@PathVariable final String id) {
        final Room room = this.roomStore.get(id);
        if (room == null) {
            return ResponseUtils.jsonView(Lists.newArrayList());
        }
        if ("G022".equals(room.getType())) {
            final List<GcRoomMember> ls = this.roomService.findByProperty(GcRoomMember.class, "roomId", id);
            final List<User> members = new ArrayList<User>();
            for (final GcRoomMember m : ls) {
                members.add(this.userStore.get(m.getUid()));
            }
            return ResponseUtils.jsonView(members);
        }
        return ResponseUtils.jsonView(room.getUsers().values());
    }
    
    @AuthPassport
    @RequestMapping(value = { "/props" }, method = { RequestMethod.GET })
    public ModelAndView getRoomProps(@RequestParam("roomId") final String roomId, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        final Room room = this.roomStore.get(roomId);
        if (room == null) {
            return ResponseUtils.jsonView(404, "\u623f\u95f4" + roomId + "\u4e0d\u5b58\u5728.");
        }
        if (!room.getOwner().equals(uid)) {
            return ResponseUtils.jsonView(404, "\u65e0\u6743\u6267\u884c\u8be5\u64cd\u4f5c!");
        }
        final Map<String, Object> res = new HashMap<String, Object>();
        res.put("room", this.roomService.get(GcRoom.class, roomId));
        res.put("props", this.roomService.getRoomProps(roomId));
        res.put("qunInfo", this.roomService.getQunInfo(roomId));
        return ResponseUtils.jsonView(res);
    }
    
    @AuthPassport
    @RequestMapping(value = { "/myMembers" }, method = { RequestMethod.GET })
    public ModelAndView getMyMembers(@RequestParam final String roomId, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        final Room room = this.roomStore.get(roomId);
        if (room == null) {
            return ResponseUtils.jsonView(404, "\u623f\u95f4" + roomId + "\u4e0d\u5b58\u5728.");
        }
        if (!room.getOwner().equals(uid)) {
            return ResponseUtils.jsonView(404, "\u65e0\u6743\u6267\u884c\u8be5\u64cd\u4f5c!");
        }
        final List<GcRoomMember> ls = null;//this.roomService.findByProperties(GcRoomMember.class, (Map<Object, Object>)ImmutableMap.of("roomId", roomId));
        return ResponseUtils.jsonView(ls);
    }
    
    @AuthPassport
    @RequestMapping(value = { "/myMemberDetail" }, method = { RequestMethod.GET })
    public ModelAndView myMemberDetail(@RequestParam final Integer id, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        final GcRoomMember m = this.roomService.get(GcRoomMember.class, id);
        final String roomId = m.getRoomId();
        final Room room = this.roomStore.get(roomId);
        if (room == null) {
            return ResponseUtils.jsonView(404, "\u623f\u95f4" + roomId + "\u4e0d\u5b58\u5728.");
        }
        if (!room.getOwner().equals(uid)) {
            return ResponseUtils.jsonView(404, "\u65e0\u6743\u6267\u884c\u8be5\u64cd\u4f5c!");
        }
        return ResponseUtils.jsonView(m);
    }
    
    @AuthPassport
    @RequestMapping(value = { "/kick" }, method = { RequestMethod.GET })
    public ModelAndView kick(@RequestParam final Integer id, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        final GcRoomMember m = this.roomService.get(GcRoomMember.class, id);
        final String roomId = m.getRoomId();
        final Room room = this.roomStore.get(roomId);
        if (room == null) {
            return ResponseUtils.jsonView(404, "\u623f\u95f4" + roomId + "\u4e0d\u5b58\u5728.");
        }
        if (!room.getOwner().equals(uid)) {
            return ResponseUtils.jsonView(500, "\u65e0\u6743\u6267\u884c\u8be5\u64cd\u4f5c!");
        }
        if (m != null) {
            this.roomService.delete(GcRoomMember.class, m);
            final GcRoomKickLog gl = new GcRoomKickLog();
            gl.setRoomId(roomId);
            gl.setUid(m.getUid());
            gl.setKickTime(new Date());
            this.roomService.save(GcRoomKickLog.class, gl);
            final Map<Integer, User> users = room.getUsers();
            if (users.containsKey(m.getUid())) {
                final User user = this.userStore.get(m.getUid());
                room.left(user);
                final Message msg = new Message("ORD", 0, "kick");
                MessageUtils.sendCMD(user, "kick", msg);
            }
        }
        return ResponseUtils.jsonView(200);
    }
    
    @AuthPassport
    @RequestMapping(value = { "/setPartner" }, method = { RequestMethod.GET })
    public ModelAndView setPartner(@RequestParam final Integer id, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        final GcRoomMember m = this.roomService.get(GcRoomMember.class, id);
        final String roomId = m.getRoomId();
        final Room room = this.roomStore.get(roomId);
        if (room == null) {
            return ResponseUtils.jsonView(404, "\u623f\u95f4" + roomId + "\u4e0d\u5b58\u5728.");
        }
        if (!room.getOwner().equals(uid)) {
            return ResponseUtils.jsonView(500, "\u65e0\u6743\u6267\u884c\u8be5\u64cd\u4f5c!");
        }
        if (m != null) {
            m.setIsPartner("1");
            this.roomService.update(GcRoomMember.class, m);
        }
        return ResponseUtils.jsonView(200);
    }
    
    @AuthPassport
    @RequestMapping(value = { "/cancelPartner" }, method = { RequestMethod.GET })
    public ModelAndView cancelPartner(@RequestParam final Integer id, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        final GcRoomMember m = this.roomService.get(GcRoomMember.class, id);
        final String roomId = m.getRoomId();
        final Room room = this.roomStore.get(roomId);
        if (room == null) {
            return ResponseUtils.jsonView(404, "\u623f\u95f4" + roomId + "\u4e0d\u5b58\u5728.");
        }
        if (!room.getOwner().equals(uid)) {
            return ResponseUtils.jsonView(500, "\u65e0\u6743\u6267\u884c\u8be5\u64cd\u4f5c!");
        }
        if (m != null) {
            m.setIsPartner("0");
            this.roomService.update(GcRoomMember.class, m);
        }
        return ResponseUtils.jsonView(200);
    }
    
    @AuthPassport
    @RequestMapping(value = { "/saveRate" }, method = { RequestMethod.GET })
    public ModelAndView saveRate(@RequestParam final Integer id, @RequestParam final Double rate, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        final GcRoomMember m = this.roomService.get(GcRoomMember.class, id);
        final String roomId = m.getRoomId();
        final Room room = this.roomStore.get(roomId);
        if (room == null) {
            return ResponseUtils.jsonView(404, "\u623f\u95f4" + roomId + "\u4e0d\u5b58\u5728.");
        }
        if (!room.getOwner().equals(uid)) {
            return ResponseUtils.jsonView(500, "\u65e0\u6743\u6267\u884c\u8be5\u64cd\u4f5c!");
        }
        final List<GcRoomMember> ls = null;// this.roomService.findByProperties(GcRoomMember.class, (Map<Object, Object>)ImmutableMap.of("roomId", roomId));
        Double fullRateExceptCurrent = 0.0;
        for (final GcRoomMember mb : ls) {
            if (!mb.getId().equals(m.getId())) {
                fullRateExceptCurrent += mb.getRate();
            }
        }
        if (m != null) {
            if (fullRateExceptCurrent + rate > 100.0) {
                return ResponseUtils.jsonView(500, "\u53ef\u8bbe\u7f6e\u6700\u5927\u80a1\u4efd:" + (100.0 - fullRateExceptCurrent));
            }
            m.setRate(rate);
            this.roomService.update(GcRoomMember.class, m);
        }
        return ResponseUtils.jsonView(200);
    }
    
    @AuthPassport
    @RequestMapping(value = { "/updateProp" }, method = { RequestMethod.GET })
    public ModelAndView updateProp(@RequestParam final String roomId, @RequestParam final String key, @RequestParam String value, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        final Room r = this.roomStore.get(roomId);
        if (r == null || !r.getOwner().equals(uid)) {
            return ResponseUtils.jsonView(500, "\u65e0\u6743\u6267\u884c\u8be5\u64cd\u4f5c!");
        }
        try {
            this.roomService.modifyRoomInfo(roomId, key, value);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.jsonView(500, e.getMessage());
        }
        return ResponseUtils.jsonView(200);
    }
    
    @AuthPassport
    @RequestMapping(value = { "/dismiss" }, method = { RequestMethod.GET })
    public ModelAndView dismiss(@RequestParam final String roomId, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        try {
            final Double restMoney = this.roomService.dismissRoom(roomId, uid);
            return ResponseUtils.jsonView(200, "ok", restMoney);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.jsonView(500, e.getMessage());
        }
    }
    
    @AuthPassport
    @RequestMapping(value = { "/addMoney" }, method = { RequestMethod.GET })
    public ModelAndView addMoney(@RequestParam final String roomId, @RequestParam final Integer money, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        try {
            this.roomService.addMoney(roomId, uid, money);
            return ResponseUtils.jsonView(200, "ok");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.jsonView(500, e.getMessage());
        }
    }
    
    @RequestMapping(value = { "/roomApplyConfig" }, method = { RequestMethod.GET })
    public ModelAndView roomApplyConfig(final HttpServletRequest request) {
        final Double d = Double.valueOf(SystemConfigService.getInstance().getValue("conf_room_money").toString());
        return ResponseUtils.jsonView(200, "success", d);
    }
}
