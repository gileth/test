// 
// Decompiled by Procyon v0.5.30
// 

package web.wx.utils;

import net.sf.ezmorph.Morpher;
import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.util.JSONUtils;
import java.util.Collection;
import net.sf.json.processors.JsonValueProcessor;
import java.util.Date;
import net.sf.json.JsonConfig;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONArray;
import java.util.Map;
import net.sf.json.JSONObject;

public class JsonUtil
{
    public static final String JSON_ATTRIBUTE = "json";
    public static final String JSON_ATTRIBUTE1 = "json1";
    public static final String JSON_ATTRIBUTE2 = "json2";
    public static final String JSON_ATTRIBUTE3 = "json3";
    public static final String JSON_ATTRIBUTE4 = "json4";
    
    public static Object getDTO(final String jsonString, final Class clazz) {
        JSONObject jsonObject = null;
        try {
            setDataFormat2JAVA();
            jsonObject = JSONObject.fromObject((Object)jsonString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return JSONObject.toBean(jsonObject, clazz);
    }
    
    public static Object getDTO(final String jsonString, final Class clazz, final Map map) {
        JSONObject jsonObject = null;
        try {
            setDataFormat2JAVA();
            jsonObject = JSONObject.fromObject((Object)jsonString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return JSONObject.toBean(jsonObject, clazz, map);
    }
    
    public static Object[] getDTOArray(final String jsonString, final Class clazz) {
        setDataFormat2JAVA();
        final JSONArray array = JSONArray.fromObject((Object)jsonString);
        final Object[] obj = new Object[array.size()];
        for (int i = 0; i < array.size(); ++i) {
            final JSONObject jsonObject = array.getJSONObject(i);
            obj[i] = JSONObject.toBean(jsonObject, clazz);
        }
        return obj;
    }
    
    public static Object[] getDTOArray(final String jsonString, final Class clazz, final Map map) {
        setDataFormat2JAVA();
        final JSONArray array = JSONArray.fromObject((Object)jsonString);
        final Object[] obj = new Object[array.size()];
        for (int i = 0; i < array.size(); ++i) {
            final JSONObject jsonObject = array.getJSONObject(i);
            obj[i] = JSONObject.toBean(jsonObject, clazz, map);
        }
        return obj;
    }
    
    public static List getDTOList(final String jsonString, final Class clazz) {
        setDataFormat2JAVA();
        final JSONArray array = JSONArray.fromObject((Object)jsonString);
        final List list = new ArrayList();
        for(int i=0;i<array.size();i++) {
            list.add(JSONObject.toBean(array.getJSONObject(i), clazz));
        }
        return list;
    }
    
    public static List getDTOList(final String jsonString, final Class clazz, final Map map) {
        setDataFormat2JAVA();
        final JSONArray array = JSONArray.fromObject((Object)jsonString);
        final List list = new ArrayList();
        for(int i=0;i<array.size();i++) {
            list.add(JSONObject.toBean(array.getJSONObject(i), clazz));
        }
        return list;
    }
    
    public static Map getMapFromJson(final String jsonString) {
        setDataFormat2JAVA();
        final JSONObject jsonObject = JSONObject.fromObject((Object)jsonString);
        final Map map = new HashMap();
        final Iterator iter = jsonObject.keys();
        while (iter.hasNext()) {
            final String key = (String) iter.next();
            map.put(key.trim(), jsonObject.get(key));
        }
        return map;
    }
    
    public static Object[] getObjectArrayFromJson(final String jsonString) {
        final JSONArray jsonArray = JSONArray.fromObject((Object)jsonString);
        return jsonArray.toArray();
    }
    
    public static String getJSONString(final Object object) throws Exception {
        String jsonString = null;
        final JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor((Class)Date.class, (JsonValueProcessor)new JsonDateValueProcessor());
        if (object != null) {
            if (object instanceof Collection || object instanceof Object[]) {
                jsonString = JSONArray.fromObject(object, jsonConfig).toString();
            }
            else {
                jsonString = JSONObject.fromObject(object, jsonConfig).toString();
            }
        }
        return (jsonString == null) ? "{}" : jsonString;
    }
    
    public static String getJSONString(final Object object, final String[] excludes) throws Exception {
        String jsonString = null;
        final JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor((Class)Date.class, (JsonValueProcessor)new JsonDateValueProcessor());
        jsonConfig.setExcludes(excludes);
        if (object != null) {
            if (object instanceof Collection || object instanceof Object[]) {
                jsonString = JSONArray.fromObject(object, jsonConfig).toString();
            }
            else {
                jsonString = JSONObject.fromObject(object, jsonConfig).toString();
            }
        }
        return (jsonString == null) ? "{}" : jsonString;
    }
    
    private static void setDataFormat2JAVA() {
        JSONUtils.getMorpherRegistry().registerMorpher((Morpher)new DateMorpher(new String[] { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss" }));
    }
    
    public static void main(final String[] arg) throws Exception {
        final String s = "[{old:'1',new:'2'},{old:'3',new:'4'}]";
        System.out.println(" object : " + getObjectArrayFromJson(s)[0]);
    }
}
