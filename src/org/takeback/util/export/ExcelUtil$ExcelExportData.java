// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.export;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedHashMap;

public static class ExcelExportData
{
    private LinkedHashMap<String, List<?>> dataMap;
    private String[] titles;
    private List<String[]> columnNames;
    private List<HashMap<String, String>> info;
    private String statistics;
    private List<String[]> fieldNames;
    
    public List<String[]> getFieldNames() {
        return this.fieldNames;
    }
    
    public void setFieldNames(final List<String[]> fieldNames) {
        this.fieldNames = fieldNames;
    }
    
    public String[] getTitles() {
        return this.titles;
    }
    
    public void setTitles(final String[] titles) {
        this.titles = titles;
    }
    
    public List<String[]> getColumnNames() {
        return this.columnNames;
    }
    
    public void setColumnNames(final List<String[]> columnNames) {
        this.columnNames = columnNames;
    }
    
    public LinkedHashMap<String, List<?>> getDataMap() {
        return this.dataMap;
    }
    
    public void setDataMap(final LinkedHashMap<String, List<?>> dataMap) {
        this.dataMap = dataMap;
    }
    
    public List<HashMap<String, String>> getInfo() {
        return this.info;
    }
    
    public void setInfo(final List<HashMap<String, String>> info) {
        this.info = info;
    }
    
    public String getStatistics() {
        return this.statistics;
    }
    
    public void setStatistics(final String statistics) {
        this.statistics = statistics;
    }
}
