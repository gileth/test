// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.mvc.controller.core;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Iterator;
import java.util.ArrayList;
import org.takeback.util.JSONUtils;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.takeback.core.dictionary.DictionaryController;
import org.takeback.core.dictionary.Dictionary;
import org.takeback.core.dictionary.DictionaryItem;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DicLocator
{
    @RequestMapping(value = { "/**/{id}.dc" }, method = { RequestMethod.GET })
    public List<DictionaryItem> get(@PathVariable final String id, @RequestParam(required = false) String node, @RequestParam(required = false, defaultValue = "3") final Integer sliceType, @RequestParam(required = false) final String query, @RequestParam(value = "filter", required = false) final String filter) {
        final Dictionary dic = DictionaryController.instance().get(id);
        if (dic == null) {
            return null;
        }
        if ("root".equals(node) || "0".equals(node)) {
            node = null;
        }
        final List<DictionaryItem> items = dic.getSlice(node, sliceType, query);
        if (!StringUtils.isEmpty((CharSequence)filter)) {
            final Map<String, String> map = JSONUtils.parse(filter, (Class<Map<String, String>>)Map.class);
            final List<DictionaryItem> filterItems = new ArrayList<DictionaryItem>();
            for (final DictionaryItem item : items) {
                boolean fit = true;
                for (final String k : map.keySet()) {
                    final String v = map.get(k);
                    if (!v.equals(item.getProperty(k))) {
                        fit = false;
                        break;
                    }
                }
                if (fit) {
                    filterItems.add(item);
                }
            }
            return filterItems;
        }
        return items;
    }
}
