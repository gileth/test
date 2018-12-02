// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import org.takeback.util.exp.ExpressionProcessor;
import org.takeback.util.converter.ConversionUtils;
import java.util.List;
import org.takeback.util.exception.CodedBaseRuntimeException;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListService;

@Service("inoutReportService")
public class InoutReportService extends MyListService
{
    @Transactional(readOnly = true)
    @Override
    public Map<String, Object> list(final Map<String, Object> req) {
        final String entityName = (String) req.get(InoutReportService.ENTITYNAME);
        if (StringUtils.isEmpty((CharSequence)entityName)) {
            throw new CodedBaseRuntimeException(404, "missing entityName");
        }
        final int limit = (Integer)req.get(InoutReportService.LIMIT);
        final int page = (Integer)req.get(InoutReportService.PAGE);
        final List<?> cnd = ConversionUtils.convert(req.get(InoutReportService.CND),List.class);
        String filter = null;
        if (cnd != null) {
            filter = ExpressionProcessor.instance().toString(cnd);
        }
        final String orderInfo = (String) req.get(InoutReportService.ORDERINFO);
        final List<?> ls = this.dao.query(entityName, filter, limit, page, orderInfo);
        this.afterList(ls);
        final long count = this.dao.totalSize(entityName, filter);
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("totalSize", count);
        result.put("body", ls);
        return result;
    }
}
