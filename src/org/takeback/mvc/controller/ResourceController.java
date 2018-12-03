// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.mvc.controller;

import java.util.Iterator;
import java.lang.reflect.Method;
import java.io.OutputStream;
import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.export.ExcelUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Date;
import org.takeback.util.ApplicationContextHolder;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.io.File;
import org.apache.commons.lang3.StringUtils;
import java.io.Serializable;
import org.takeback.mvc.ResponseUtils;
import org.springframework.web.util.WebUtils;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import java.text.DecimalFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping({ "/**/file" })
public class ResourceController
{
    private DecimalFormat decimalFormat;
    private static final String SERVICE = "service";
    private static final String METHOD = "method";
    private static final String FILENAME = "filename";
    private String fileDirectory;
    
    public ResourceController() {
        this.decimalFormat = new DecimalFormat("#0.00");
        this.fileDirectory = "/resources/upload";
    }
    
    @RequestMapping(value = { "upload" }, method = { RequestMethod.POST })
    public ModelAndView upload(@RequestParam("file") final CommonsMultipartFile file, @RequestParam(required = false) final String filePath, final HttpServletRequest request) {
        final Object uid = WebUtils.getSessionAttribute(request, "$uid");
        if (uid == null) {
            return ResponseUtils.jsonView(403, "not login");
        }
        if (file.getSize() == 0L) {
            return ResponseUtils.jsonView(404, "filesize is 0");
        }
        final String oFilename = file.getOriginalFilename();
        final String suffix = oFilename.contains(".") ? oFilename.substring(oFilename.lastIndexOf(".")) : "";
        final String deskName = StringUtils.join((Object[])new Serializable[] { System.currentTimeMillis(), suffix });
        final File directory = new File(request.getSession().getServletContext().getRealPath(org.springframework.util.StringUtils.isEmpty(filePath) ? this.fileDirectory : filePath));
        if (!directory.exists()) {
            directory.mkdirs();
        }
        final HashMap map = new HashMap();
        map.put("filename", deskName);
        if (file.getSize() < 10485.0f) {
            map.put("size", this.decimalFormat.format(file.getSize() / 1024.0f) + "KB");
        }
        else {
            map.put("size", this.decimalFormat.format(file.getSize() / 1024.0f / 1024.0f) + "MB");
        }
        try {
            final File deskFile = new File(directory.getAbsolutePath() + "/" + deskName);
            file.transferTo(deskFile);
            return ResponseUtils.jsonView(ImmutableMap.of("code", 200, "success",true,"body", map));
        }
        catch (IOException e) {
            return ResponseUtils.jsonView(500, "failure", deskName);
        }
    }
    
    @RequestMapping(value = { "download" }, method = { RequestMethod.POST })
    public void download(@RequestParam("file") final CommonsMultipartFile file, final HttpServletRequest request) {
    }
    
    @RequestMapping(value = { "exportExcel" }, method = { RequestMethod.GET })
    public ModelAndView exportExcel(final HttpServletRequest request, final HttpServletResponse response) {
        final String uid = (String)WebUtils.getSessionAttribute(request, "$uid");
        final Long urt = (Long)WebUtils.getSessionAttribute(request, "$urt");
        if (uid == null || urt == null) {
            return ResponseUtils.jsonView(401, "notLogon");
        }
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        final Map properties = this.ParameterMap2Map(request.getParameterMap());
        if (org.springframework.util.StringUtils.isEmpty(properties.get("service")) || org.springframework.util.StringUtils.isEmpty(properties.get("method")) || org.springframework.util.StringUtils.isEmpty(properties.get("filename"))) {
            return ResponseUtils.jsonView(402, "missing service or method.");
        }
        final String service = properties.get("service").toString();
        final String method = properties.get("method").toString();
        final String filename = properties.get("filename").toString();
        if (!ApplicationContextHolder.containBean(service)) {
            return ResponseUtils.jsonView(403, "service is not defined in spring.");
        }
        response.reset();
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("content-disposition", "attachment;filename=" + filename + sdf.format(new Date()) + ".xls");
        OutputStream fOut = null;
        try {
            fOut = (OutputStream)response.getOutputStream();
        }
        catch (IOException e5) {
            return ResponseUtils.jsonView(404, "get OutputStream error!");
        }
        properties.remove("service");
        properties.remove("method");
        properties.remove("filename");
        final LinkedHashMap<String, List<?>> datamap = new LinkedHashMap<String, List<?>>();
        final Object s = ApplicationContextHolder.getBean(service);
        Method m = null;
        Object r = null;
        try {
            m = s.getClass().getDeclaredMethod(method, Map.class);
            r = m.invoke(s, properties);
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
            return ResponseUtils.jsonView(405, "\u5bfc\u51fa\u6587\u4ef6\u51fa\u9519\uff01" + e.getMessage());
        }
        catch (InvocationTargetException e2) {
            e2.printStackTrace();
            return ResponseUtils.jsonView(406, "\u5bfc\u51fa\u6587\u4ef6\u51fa\u9519\uff01" + e2.getMessage());
        }
        catch (IllegalAccessException e3) {
            e3.printStackTrace();
            return ResponseUtils.jsonView(407, "\u5bfc\u51fa\u6587\u4ef6\u51fa\u9519\uff01" + e3.getMessage());
        }
        final ExcelUtil.ExcelExportData setInfo = ConversionUtils.convert(r, ExcelUtil.ExcelExportData.class);
        try {
            fOut.write(ExcelUtil.export2Stream(setInfo).toByteArray());
            fOut.flush();
        }
        catch (Exception e4) {
            e4.printStackTrace();
        }
        return null;
    }
    
    public Map ParameterMap2Map(final Map properties) {
        final Map returnMap = new HashMap();
        final Iterator entries = properties.entrySet().iterator();
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            final Map.Entry entry = (Entry) entries.next();
            name = (String) entry.getKey();
            final Object valueObj = entry.getValue();
            if (null == valueObj) {
                value = "";
            }
            else if (valueObj instanceof String[]) {
                final String[] values = (String[])valueObj;
                for (int i = 0; i < values.length; ++i) {
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            }
            else {
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }
}
