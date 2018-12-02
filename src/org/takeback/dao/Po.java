// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.dao;

public class Po
{
    private int pageSize;
    private int pageNo;
    private String order;
    
    public Po(final int pageNo) {
        this.pageNo = pageNo;
    }
    
    public Po(final int pageSize, final int pageNo) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
    }
    
    public Po(final int pageSize, final int pageNo, final String order) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        this.order = order;
    }
    
    public Po(final String order) {
        this.order = order;
    }
    
    public int getPageSize() {
        return this.pageSize;
    }
    
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }
    
    public int getPageNo() {
        return this.pageNo;
    }
    
    public void setPageNo(final int pageNo) {
        this.pageNo = pageNo;
    }
    
    public String getOrder() {
        return this.order;
    }
    
    public void setOrder(final String order) {
        this.order = order;
    }
}
