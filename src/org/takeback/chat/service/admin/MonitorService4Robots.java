// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service("monitorService4Robots")
public class MonitorService4Robots extends MonitorService
{
    @Override
    public Map<String, Object> list(final Map<String, Object> req) {
        final List<?> ls = this.gameMonitor.robotsList();
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("totalSize", ls.size());
        result.put("body", ls);
        return result;
    }
}
