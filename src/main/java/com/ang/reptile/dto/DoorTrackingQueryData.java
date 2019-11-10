package com.ang.reptile.dto;

import java.io.Serializable;

public class DoorTrackingQueryData implements Serializable {

    private static final long serialVersionUID = 301214874998062228L;
    private String companyCode;
    private String wd;
    private String srvSaleCountStart;
    private String srvSaleCountEnd;
    private Integer pageSize;
    private Integer page;

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getWd() {
        return wd;
    }

    public void setWd(String wd) {
        this.wd = wd;
    }

    public String getSrvSaleCountStart() {
        return srvSaleCountStart;
    }

    public void setSrvSaleCountStart(String srvSaleCountStart) {
        this.srvSaleCountStart = srvSaleCountStart;
    }

    public String getSrvSaleCountEnd() {
        return srvSaleCountEnd;
    }

    public void setSrvSaleCountEnd(String srvSaleCountEnd) {
        this.srvSaleCountEnd = srvSaleCountEnd;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
