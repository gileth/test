// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.export;

import java.util.LinkedHashMap;
import org.apache.poi.ss.usermodel.IndexedColors;
import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import java.util.Iterator;
import java.util.Set;
import java.io.OutputStream;
import org.apache.commons.lang3.StringUtils;
import java.util.List;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelUtil
{
    private static HSSFWorkbook wb;
    private static CellStyle titleStyle;
    private static Font titleFont;
    private static CellStyle dateStyle;
    private static Font dateFont;
    private static CellStyle headStyle;
    private static Font headFont;
    private static CellStyle contentStyle;
    private static Font contentFont;
    private static CellStyle infoStyle;
    
    public static boolean export2File(final ExcelExportData setInfo, final String outputExcelFileName) throws Exception {
        return FileUtil.write(outputExcelFileName, export2ByteArray(setInfo), true, true);
    }
    
    public static byte[] export2ByteArray(final ExcelExportData setInfo) throws Exception {
        return export2Stream(setInfo).toByteArray();
    }
    
    public static ByteArrayOutputStream export2Stream(final ExcelExportData setInfo) throws Exception {
        init();
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final Set<Map.Entry<String, List<?>>> set = setInfo.getDataMap().entrySet();
        final String[] sheetNames = new String[setInfo.getDataMap().size()];
        int sheetNameNum = 0;
        for (final Map.Entry<String, List<?>> entry : set) {
            sheetNames[sheetNameNum] = entry.getKey();
            ++sheetNameNum;
        }
        final HSSFSheet[] sheets = getSheets(setInfo.getDataMap().size(), sheetNames);
        int sheetNum = 0;
        for (final Map.Entry<String, List<?>> entry2 : set) {
            final List<?> objs = entry2.getValue();
            createTableTitleRow(setInfo, sheets, sheetNum);
            if (setInfo.getInfo() != null && setInfo.getInfo().size() != 0) {
                createTableInfoRow(setInfo, sheets, sheetNum);
            }
            creatTableHeadRow(setInfo, sheets, sheetNum);
            final String[] fieldNames = setInfo.getFieldNames().get(sheetNum);
            int rowNum = 4;
            for (final Object obj : objs) {
                final HSSFRow contentRow = sheets[sheetNum].createRow(rowNum);
                contentRow.setHeight((short)300);
                final HSSFCell[] cells = getCells(contentRow, setInfo.getFieldNames().get(sheetNum).length);
                int cellNum = 1;
                if (fieldNames != null) {
                    for (int num = 0; num < fieldNames.length; ++num) {
                        Object value = null;
                        if (obj instanceof Map) {
                            value = ((Map)obj).get(fieldNames[num]);
                        }
                        else {
                            value = ReflectionUtil.invokeGetterMethod(obj, fieldNames[num]);
                        }
                        cells[cellNum].setCellValue((value == null) ? "" : value.toString());
                        ++cellNum;
                    }
                }
                ++rowNum;
            }
            if (StringUtils.isNotEmpty((CharSequence)setInfo.getStatistics())) {
                creatTableStatisticsRow(setInfo, sheets, sheetNum, rowNum);
            }
            adjustColumnSize(sheets, sheetNum, fieldNames);
            ++sheetNum;
        }
        ExcelUtil.wb.write((OutputStream)outputStream);
        return outputStream;
    }
    
    private static void init() {
        ExcelUtil.wb = new HSSFWorkbook();
        ExcelUtil.titleFont = (Font)ExcelUtil.wb.createFont();
        ExcelUtil.titleStyle = (CellStyle)ExcelUtil.wb.createCellStyle();
        ExcelUtil.dateStyle = (CellStyle)ExcelUtil.wb.createCellStyle();
        ExcelUtil.dateFont = (Font)ExcelUtil.wb.createFont();
        ExcelUtil.headStyle = (CellStyle)ExcelUtil.wb.createCellStyle();
        ExcelUtil.headFont = (Font)ExcelUtil.wb.createFont();
        ExcelUtil.contentStyle = (CellStyle)ExcelUtil.wb.createCellStyle();
        ExcelUtil.contentFont = (Font)ExcelUtil.wb.createFont();
        ExcelUtil.infoStyle = (CellStyle)ExcelUtil.wb.createCellStyle();
        initTitleCellStyle();
        initTitleFont();
        initDateCellStyle();
        initDateFont();
        initHeadCellStyle();
        initHeadFont();
        initContentCellStyle();
        initContentFont();
        initInfoCellStyle();
    }
    
    private static void adjustColumnSize(final HSSFSheet[] sheets, final int sheetNum, final String[] fieldNames) {
        for (int i = 0; i < fieldNames.length + 1; ++i) {
            sheets[sheetNum].autoSizeColumn(i, true);
        }
    }
    
    private static void createTableTitleRow(final ExcelExportData setInfo, final HSSFSheet[] sheets, final int sheetNum) {
        final CellRangeAddress titleRange = new CellRangeAddress(0, 0, 0, setInfo.getFieldNames().get(sheetNum).length);
        sheets[sheetNum].addMergedRegion(titleRange);
        final HSSFRow titleRow = sheets[sheetNum].createRow(0);
        titleRow.setHeight((short)800);
        final HSSFCell titleCell = titleRow.createCell(0);
        titleCell.setCellStyle(ExcelUtil.titleStyle);
        titleCell.setCellValue(setInfo.getTitles()[sheetNum]);
    }
    
    private static void createTableDateRow(final ExcelExportData setInfo, final HSSFSheet[] sheets, final int sheetNum) {
        final CellRangeAddress dateRange = new CellRangeAddress(1, 1, 0, setInfo.getFieldNames().get(sheetNum).length);
        sheets[sheetNum].addMergedRegion(dateRange);
        final HSSFRow dateRow = sheets[sheetNum].createRow(1);
        dateRow.setHeight((short)350);
        final HSSFCell dateCell = dateRow.createCell(0);
        dateCell.setCellStyle(ExcelUtil.dateStyle);
        dateCell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }
    
    private static void createTableInfoRow(final ExcelExportData setInfo, final HSSFSheet[] sheets, final int sheetNum) {
        HSSFRow dateRow = sheets[sheetNum].createRow(1);
        for (int i = 0; i < 6; ++i) {
            if (i == 3) {
                dateRow = sheets[sheetNum].createRow(2);
            }
            for (int j = 0; j < 2; ++j) {
                final CellRangeAddress dateRange = new CellRangeAddress((i + 3) / 3, (i + 3) / 3, i % 3 * 4 + j, i % 3 * 4 + j * 3);
                sheets[sheetNum].addMergedRegion(dateRange);
                dateRow.setHeight((short)600);
                final HSSFCell dateCell = dateRow.createCell(i % 3 * 4 + j);
                dateCell.setCellValue((String)setInfo.getInfo().get(i).get((j == 0) ? "text" : "value"));
                dateCell.setCellStyle(ExcelUtil.infoStyle);
            }
        }
    }
    
    private static void creatTableHeadRow(final ExcelExportData setInfo, final HSSFSheet[] sheets, final int sheetNum) {
        final HSSFRow headRow = sheets[sheetNum].createRow(3);
        headRow.setHeight((short)350);
        final HSSFCell snCell = headRow.createCell(0);
        snCell.setCellStyle(ExcelUtil.headStyle);
        snCell.setCellValue("\u5e8f\u53f7");
        for (int num = 1, len = setInfo.getColumnNames().get(sheetNum).length; num <= len; ++num) {
            final HSSFCell headCell = headRow.createCell(num);
            headCell.setCellStyle(ExcelUtil.headStyle);
            headCell.setCellValue(setInfo.getColumnNames().get(sheetNum)[num - 1]);
        }
    }
    
    private static HSSFSheet[] getSheets(final int num, final String[] names) {
        final HSSFSheet[] sheets = new HSSFSheet[num];
        for (int i = 0; i < num; ++i) {
            sheets[i] = ExcelUtil.wb.createSheet(names[i]);
        }
        return sheets;
    }
    
    private static HSSFCell[] getCells(final HSSFRow contentRow, final int num) {
        final HSSFCell[] cells = new HSSFCell[num + 1];
        for (int i = 0, len = cells.length; i < len; ++i) {
            (cells[i] = contentRow.createCell(i)).setCellStyle(ExcelUtil.contentStyle);
        }
        cells[0].setCellValue((double)(contentRow.getRowNum() - 3));
        return cells;
    }
    
    private static void creatTableStatisticsRow(final ExcelExportData setInfo, final HSSFSheet[] sheets, final int sheetNum, final int num) {
        final CellRangeAddress dateRange = new CellRangeAddress(num, num, 0, setInfo.getFieldNames().get(sheetNum).length);
        sheets[sheetNum].addMergedRegion(dateRange);
        final HSSFRow dateRow = sheets[sheetNum].createRow(num);
        dateRow.setHeight((short)400);
        final HSSFCell dateCell = dateRow.createCell(0);
        dateCell.setCellStyle(ExcelUtil.infoStyle);
        dateCell.setCellValue(setInfo.getStatistics());
    }
    
    private static void initTitleCellStyle() {
        ExcelUtil.titleStyle.setAlignment((short)2);
        ExcelUtil.titleStyle.setVerticalAlignment((short)1);
        ExcelUtil.titleStyle.setFont(ExcelUtil.titleFont);
        ExcelUtil.titleStyle.setFillBackgroundColor(IndexedColors.SKY_BLUE.index);
    }
    
    private static void initDateCellStyle() {
        ExcelUtil.dateStyle.setAlignment((short)6);
        ExcelUtil.dateStyle.setVerticalAlignment((short)1);
        ExcelUtil.dateStyle.setFont(ExcelUtil.dateFont);
        ExcelUtil.dateStyle.setFillBackgroundColor(IndexedColors.SKY_BLUE.index);
    }
    
    private static void initHeadCellStyle() {
        ExcelUtil.headStyle.setAlignment((short)2);
        ExcelUtil.headStyle.setVerticalAlignment((short)1);
        ExcelUtil.headStyle.setFont(ExcelUtil.headFont);
        ExcelUtil.headStyle.setFillBackgroundColor(IndexedColors.YELLOW.index);
        ExcelUtil.headStyle.setBorderTop((short)2);
        ExcelUtil.headStyle.setBorderBottom((short)1);
        ExcelUtil.headStyle.setBorderLeft((short)1);
        ExcelUtil.headStyle.setBorderRight((short)1);
        ExcelUtil.headStyle.setTopBorderColor(IndexedColors.BLUE.index);
        ExcelUtil.headStyle.setBottomBorderColor(IndexedColors.BLUE.index);
        ExcelUtil.headStyle.setLeftBorderColor(IndexedColors.BLUE.index);
        ExcelUtil.headStyle.setRightBorderColor(IndexedColors.BLUE.index);
    }
    
    private static void initInfoCellStyle() {
        ExcelUtil.infoStyle.setAlignment((short)2);
        ExcelUtil.infoStyle.setVerticalAlignment((short)1);
        ExcelUtil.infoStyle.setFont(ExcelUtil.headFont);
        ExcelUtil.infoStyle.setFillBackgroundColor(IndexedColors.SKY_BLUE.index);
        ExcelUtil.infoStyle.setWrapText(true);
    }
    
    private static void initContentCellStyle() {
        ExcelUtil.contentStyle.setAlignment((short)2);
        ExcelUtil.contentStyle.setVerticalAlignment((short)1);
        ExcelUtil.contentStyle.setFont(ExcelUtil.contentFont);
        ExcelUtil.contentStyle.setBorderTop((short)1);
        ExcelUtil.contentStyle.setBorderBottom((short)1);
        ExcelUtil.contentStyle.setBorderLeft((short)1);
        ExcelUtil.contentStyle.setBorderRight((short)1);
        ExcelUtil.contentStyle.setTopBorderColor(IndexedColors.BLUE.index);
        ExcelUtil.contentStyle.setBottomBorderColor(IndexedColors.BLUE.index);
        ExcelUtil.contentStyle.setLeftBorderColor(IndexedColors.BLUE.index);
        ExcelUtil.contentStyle.setRightBorderColor(IndexedColors.BLUE.index);
        ExcelUtil.contentStyle.setWrapText(true);
    }
    
    private static void initTitleFont() {
        ExcelUtil.titleFont.setFontName("\u534e\u6587\u6977\u4f53");
        ExcelUtil.titleFont.setFontHeightInPoints((short)20);
        ExcelUtil.titleFont.setBoldweight((short)700);
        ExcelUtil.titleFont.setCharSet((byte)1);
        ExcelUtil.titleFont.setColor(IndexedColors.BLUE_GREY.index);
    }
    
    private static void initDateFont() {
        ExcelUtil.dateFont.setFontName("\u96b6\u4e66");
        ExcelUtil.dateFont.setFontHeightInPoints((short)10);
        ExcelUtil.dateFont.setBoldweight((short)700);
        ExcelUtil.dateFont.setCharSet((byte)1);
        ExcelUtil.dateFont.setColor(IndexedColors.BLUE_GREY.index);
    }
    
    private static void initHeadFont() {
        ExcelUtil.headFont.setFontName("\u5b8b\u4f53");
        ExcelUtil.headFont.setFontHeightInPoints((short)10);
        ExcelUtil.headFont.setBoldweight((short)700);
        ExcelUtil.headFont.setCharSet((byte)1);
        ExcelUtil.headFont.setColor(IndexedColors.BLUE_GREY.index);
    }
    
    private static void initContentFont() {
        ExcelUtil.contentFont.setFontName("\u5b8b\u4f53");
        ExcelUtil.contentFont.setFontHeightInPoints((short)10);
        ExcelUtil.contentFont.setBoldweight((short)400);
        ExcelUtil.contentFont.setCharSet((byte)1);
        ExcelUtil.contentFont.setColor(IndexedColors.BLUE_GREY.index);
    }
    
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
}
