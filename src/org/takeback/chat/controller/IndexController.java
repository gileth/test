// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Iterator;
import java.util.List;
import org.takeback.mvc.ResponseUtils;
import org.takeback.chat.store.room.Room;
import org.springframework.web.servlet.ModelAndView;
import org.takeback.chat.schedule.ProxySchedule;
import org.takeback.chat.store.user.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.stereotype.Controller;

@Controller("indexController")
public class IndexController
{
    @Autowired
    private RoomStore roomStore;
    @Autowired
    private UserStore userStore;
    @Autowired
    ProxySchedule ps;
    
    @RequestMapping(value = { "/totalOnlineCount" }, method = { RequestMethod.GET })
    public ModelAndView getTotalOnlineCout() {
        final List<Room> rooms = this.roomStore.getByCatalog("");
        long count = this.userStore.size();
        if (rooms != null && !rooms.isEmpty()) {
            for (final Room room : rooms) {
                count += room.getGuests().size();
            }
        }
        return ResponseUtils.jsonView(200, "ok", count);
    }
    
    @RequestMapping({ "/test" })
    public ModelAndView test(final HttpServletRequest request) {
        this.ps.work();
        return new ModelAndView();
    }
}
