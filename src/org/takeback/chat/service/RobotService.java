// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import java.util.Map;
import org.takeback.util.BeanUtils;
import org.takeback.chat.entity.PubUser;
import java.util.HashMap;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import org.takeback.chat.store.user.RobotUser;
import java.util.List;
import org.springframework.stereotype.Service;
import org.takeback.service.BaseService;

@Service
public class RobotService extends BaseService
{
    private List<RobotUser> freeRobots;
    private Integer maxId;
    
    public RobotService() {
        this.freeRobots = new ArrayList<RobotUser>();
        this.maxId = null;
    }
    
    @Transactional(readOnly = true)
    public List<RobotUser> getRobots(final Integer num) {
        if (this.freeRobots.size() < num) {
            this.load(num - this.freeRobots.size());
        }
        return null;
    }
    
    @Transactional(readOnly = true)
    public List<RobotUser> load(final Integer num) {
        final String hql = "from PubUser where userType=9 and id>:id order by id asc";
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put("id", this.maxId);
        if (this.maxId == null) {
            param.put("id", 0);
        }
        final List<PubUser> users = this.dao.findByHqlPaging(hql, param, num, 1);
        if (users.size() > 0) {
            this.maxId = users.get(users.size() - 1).getId();
        }
        final List<RobotUser> robots = new ArrayList<RobotUser>();
        for (int i = 0; i < users.size(); ++i) {
            final RobotUser r = BeanUtils.map(users.get(i), RobotUser.class);
            robots.add(r);
        }
        return robots;
    }
}
