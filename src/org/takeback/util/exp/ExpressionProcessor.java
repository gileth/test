// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp;

import org.takeback.util.context.ContextUtils;
import org.takeback.util.JSONUtils;
import java.util.Iterator;
import java.util.List;
import org.takeback.util.exp.exception.ExprException;
import java.util.concurrent.ConcurrentHashMap;

public class ExpressionProcessor
{
    private static final String BASE_LANG = "base";
    private static ConcurrentHashMap<String, ExpressionSet> languages;
    private static ConcurrentHashMap<String, ExpressionProcessor> instances;
    private String language;
    
    public ExpressionProcessor() {
        this("base");
    }
    
    private ExpressionProcessor(final String lang) {
        this.language = lang;
        ExpressionProcessor.instances.put(this.language, this);
    }
    
    public static ExpressionProcessor instance(final String lang) throws ExprException {
        if (lang == null) {
            return instance();
        }
        if (!ExpressionProcessor.languages.containsKey(lang)) {
            throw new ExprException("expr language[" + lang + "] is not found.");
        }
        ExpressionProcessor o = null;
        if (!ExpressionProcessor.instances.containsKey(lang)) {
            o = new ExpressionProcessor(lang);
        }
        else {
            o = ExpressionProcessor.instances.get(lang);
        }
        return o;
    }
    
    public static ExpressionProcessor instance() throws ExprException {
        return instance("base");
    }
    
    public void setExpressionSets(final List<ExpressionSet> langs) {
        for (final ExpressionSet lang : langs) {
            this.addExpressionSet(lang.getName(), lang);
        }
    }
    
    public void addExpressionSet(final String nm, final ExpressionSet es) {
        ExpressionProcessor.languages.put(nm, es);
    }
    
    public void addExpressionSet(final ExpressionSet es) {
        this.addExpressionSet("base", es);
    }
    
    private Expression getExpression(final String nm) {
        Expression expr = null;
        if (ExpressionProcessor.languages.containsKey(this.language)) {
            expr = ExpressionProcessor.languages.get(this.language).getExpression(nm);
        }
        if (expr == null) {
            expr = ExpressionProcessor.languages.get("base").getExpression(nm);
        }
        return expr;
    }
    
    private Expression lookup(final List<?> ls) throws ExprException {
        if (ls == null || ls.isEmpty()) {
            throw new ExprException("expr list is empty.");
        }
        final String nm = (String)ls.get(0);
        final Expression expr = this.getExpression(nm);
        if (expr == null) {
            throw new ExprException("expr[" + nm + "] not found.");
        }
        return expr;
    }
    
    private List<?> parseStr(final String exp) throws ExprException {
        try {
            final List<?> ls = JSONUtils.parse(exp,List.class);
            return ls;
        }
        catch (Exception e) {
            throw new ExprException(e);
        }
    }
    
    public Object run(final String exp) throws ExprException {
        return this.run(this.parseStr(exp));
    }
    
    public String toString(final String exp) throws ExprException {
        return this.toString(this.parseStr(exp));
    }
    
    public Object run(final List<?> ls) throws ExprException {
        return this.lookup(ls).run(ls, this);
    }
    
    public String toString(final List<?> ls) throws ExprException {
        return this.lookup(ls).toString(ls, this);
    }
    
    public String toString(final String exp, final boolean forPreparedStatement) throws ExprException {
        this.configExpressionContext(forPreparedStatement);
        return this.toString(exp);
    }
    
    public String toString(final List<?> ls, final boolean forPreparedStatement) throws ExprException {
        this.configExpressionContext(forPreparedStatement);
        return this.toString(ls);
    }
    
    private void configExpressionContext(final boolean forPreparedStatement) {
        ExpressionContextBean bean;
        if (ContextUtils.hasKey("$exp")) {
            bean = ContextUtils.get("$exp", ExpressionContextBean.class);
            bean.clearPatameters();
        }
        else {
            bean = new ExpressionContextBean();
            ContextUtils.put("$exp", bean);
        }
        bean.setForPreparedStatement(forPreparedStatement);
    }
    
    static {
        ExpressionProcessor.languages = new ConcurrentHashMap<String, ExpressionSet>();
        ExpressionProcessor.instances = new ConcurrentHashMap<String, ExpressionProcessor>();
    }
}
