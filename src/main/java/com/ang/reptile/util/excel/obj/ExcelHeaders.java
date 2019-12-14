package com.ang.reptile.util.excel.obj;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExcelHeaders {
    private XssFExcelObj excelObj;
    private LinkedHashMap<String, String> headers;

    public ExcelHeaders(XssFExcelObj excelObj) {
        this.excelObj = excelObj;
    }

    public ExcelHeaders addHeader(@NotNull String key, String value) {
        if (headers == null) {
            headers = new LinkedHashMap<>();
        }
        if (value == null || value.trim() == "") {
            value = key;
        }
        headers.put(key.trim(), value.trim());
        return this;
    }

    public ExcelHeaders addHeaders(Map<?, ?> headers) {
        for (Map.Entry<?, ?> stringStringEntry : headers.entrySet()) {
            addHeader(stringStringEntry.getKey().toString(), stringStringEntry.getValue().toString());
        }
        return this;
    }

    public String removeHeader(String key) {
        String value = headers.remove(key);
        return value;
    }

    public String getHeaderAlias(String key) {
        return headers.get(key);
    }

    public LinkedHashMap getHeaders() {
        return headers;
    }

    public Collection<String> values() {
        return headers.values();
    }

    public void finish(int sheetIndex) {
        excelObj.flushHeader(sheetIndex);
    }

    public void setExcelObj(XssFExcelObj excelObj) {
        this.excelObj = excelObj;
    }
}
