// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.user;

public class UserFundStatistics
{
    private Double accumulatedIncome;
    private Double expectIncome;
    private Double recyclePrincipal;
    private Double totalFinanAmount;
    private Double repayAmount;
    private Double alreadyRepayAmount;
    private Double totalInvest;
    private Integer countInvest;
    private Integer countFinanAmount;
    
    public UserFundStatistics() {
        this.accumulatedIncome = 0.0;
        this.expectIncome = 0.0;
        this.recyclePrincipal = 0.0;
        this.totalFinanAmount = 0.0;
        this.repayAmount = 0.0;
        this.alreadyRepayAmount = 0.0;
        this.totalInvest = 0.0;
        this.countInvest = 0;
        this.countFinanAmount = 0;
    }
    
    public Double getAccumulatedIncome() {
        return this.accumulatedIncome;
    }
    
    public void setAccumulatedIncome(final Double accumulatedIncome) {
        this.accumulatedIncome = accumulatedIncome;
    }
    
    public void addAccumulatedIncome(final Double accumulatedIncome) {
        this.accumulatedIncome += accumulatedIncome;
    }
    
    public Double getExpectIncome() {
        return this.expectIncome;
    }
    
    public void setExpectIncome(final Double expectIncome) {
        this.expectIncome = expectIncome;
    }
    
    public void addExpectIncome(final Double expectIncome) {
        this.expectIncome += expectIncome;
    }
    
    public Double getRecyclePrincipal() {
        return this.recyclePrincipal;
    }
    
    public void setRecyclePrincipal(final Double recyclePrincipal) {
        this.recyclePrincipal = recyclePrincipal;
    }
    
    public void addRecyclePrincipal(final Double recyclePrincipal) {
        this.recyclePrincipal += recyclePrincipal;
    }
    
    public Double getTotalFinanAmount() {
        return this.totalFinanAmount;
    }
    
    public void setTotalFinanAmount(final Double totalFinanAmount) {
        this.totalFinanAmount = totalFinanAmount;
    }
    
    public void addTotalFinanAmount(final Double totalFinanAmount) {
        this.totalFinanAmount += totalFinanAmount;
    }
    
    public Double getRepayAmount() {
        return this.repayAmount;
    }
    
    public void setRepayAmount(final Double repayAmount) {
        this.repayAmount = repayAmount;
    }
    
    public void addRepayAmount(final Double repayAmount) {
        this.repayAmount += repayAmount;
    }
    
    public Double getAlreadyRepayAmount() {
        return this.alreadyRepayAmount;
    }
    
    public void setAlreadyRepayAmount(final Double alreadyRepayAmount) {
        this.alreadyRepayAmount = alreadyRepayAmount;
    }
    
    public void addAlreadyRepayAmount(final Double alreadyRepayAmount) {
        this.alreadyRepayAmount += alreadyRepayAmount;
    }
    
    public void setCountInvest(final Integer countInvest) {
        this.countInvest = countInvest;
    }
    
    public void setTotalInvest(final Double totalInvest) {
        this.totalInvest = totalInvest;
    }
    
    public void setCountFinanAmount(final Integer countFinanAmount) {
        this.countFinanAmount = countFinanAmount;
    }
    
    public Double getTotalInvest() {
        return this.totalInvest;
    }
    
    public void addTotalInvest(final Double totalInvest) {
        this.totalInvest += totalInvest;
    }
    
    public Integer getCountInvest() {
        return this.countInvest;
    }
    
    public void addCountInvest(final Integer countInvest) {
        this.countInvest += countInvest;
    }
    
    public Integer getCountFinanAmount() {
        return this.countFinanAmount;
    }
    
    public void addCountFinanAmount(final Integer countFinanAmount) {
        this.countFinanAmount += countFinanAmount;
    }
}
