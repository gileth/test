// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.context;

import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.BeanUtils;
import java.util.Map;
import java.util.HashMap;

public class Context extends HashMap<String, Object>
{
    private static final long serialVersionUID = 7821961495108859198L;
    public static final String APP_CONTEXT = "$applicationContext";
    public static final String WEB_SESSION = "$webSession";
    public static final String HTTP_REQUEST = "$httpRequest";
    public static final String HTTP_RESPONSE = "$httpResponse";
    public static final String DB_SESSION = "$dbSession";
    public static final String FROM_DOMAIN = "$fromDomain";
    public static final String CLIENT_IP_ADDRESS = "$clientIpAddress";
    public static final String USER_ROLE_TOKEN = "$urt";
    public static final String REQUEST_APPNODE_DEEP = "$requestAppNodeDeep";
    public static final String REQUEST_UNIT_DEEP = "$requestUnitDeep";
    public static final String EXP_BEAN = "$exp";
    public static final String ENTITY_CONTEXT = "$r";
    public static final String QUERY_CONTEXT = "$q";
    public static final String TENANT_ID = "$tenantId";
    public static final String RPC_INVOKE_HEADERS = "$rpcInvokeHeaders";
    public static final String UID = "$uid";
    public static final String INVITOR = "$invitor";
    private static String topCtxName;
    private static Context topCtx;
    
    public static Context instance() {
        return Context.topCtx;
    }
    
    public Context(final String name, final Context ctx) {
        Context.topCtxName = name;
        Context.topCtx = ctx;
        ((HashMap<String, Context>)this).put(Context.topCtxName, Context.topCtx);
    }
    
    public Context() {
        if (Context.topCtx != null) {
            ((HashMap<String, Context>)this).put(Context.topCtxName, Context.topCtx);
        }
    }
    
    public Context(final Map<String, Object> m) {
        super(m);
    }
    
    @Override
    public Object get(final Object key) {
        if (this.containsKey(key)) {
            return super.get(key);
        }
        try {
            return BeanUtils.getProperty(this, (String)key);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public void set(final String key, final Object v) {
        try {
            BeanUtils.setProperty(this, key, v);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    public <T> T get(final Object key, final Class<T> type) {
        final Object result = this.get(key);
        return ConversionUtils.convert(result, type);
    }
}
