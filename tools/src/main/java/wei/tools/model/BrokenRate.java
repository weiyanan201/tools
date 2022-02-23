package wei.tools.model;

import java.math.BigDecimal;

public class BrokenRate {
    private String date;

    private Integer limitCount;

    private Integer brokenCount;

    private BigDecimal brokenRate;

    private Integer firstLimitCount;

    private Integer firstBrokenCount;

    private BigDecimal firstBrokenRate;

    private Integer secondLimitCount;

    private Integer secondBrokenCount;

    private BigDecimal secondBrokenRate;

    private Integer thirdLimitCount;

    private Integer thirdBrokenCount;

    private BigDecimal thirdBrokenRate;

    private Integer moreLimitCount;

    private Integer moreBrokenCount;

    private BigDecimal moreBrokenRate;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(Integer limitCount) {
        this.limitCount = limitCount;
    }

    public Integer getBrokenCount() {
        return brokenCount;
    }

    public void setBrokenCount(Integer brokenCount) {
        this.brokenCount = brokenCount;
    }

    public BigDecimal getBrokenRate() {
        return brokenRate;
    }

    public void setBrokenRate(BigDecimal brokenRate) {
        this.brokenRate = brokenRate;
    }

    public Integer getFirstLimitCount() {
        return firstLimitCount;
    }

    public void setFirstLimitCount(Integer firstLimitCount) {
        this.firstLimitCount = firstLimitCount;
    }

    public Integer getFirstBrokenCount() {
        return firstBrokenCount;
    }

    public void setFirstBrokenCount(Integer firstBrokenCount) {
        this.firstBrokenCount = firstBrokenCount;
    }

    public BigDecimal getFirstBrokenRate() {
        return firstBrokenRate;
    }

    public void setFirstBrokenRate(BigDecimal firstBrokenRate) {
        this.firstBrokenRate = firstBrokenRate;
    }

    public Integer getSecondLimitCount() {
        return secondLimitCount;
    }

    public void setSecondLimitCount(Integer secondLimitCount) {
        this.secondLimitCount = secondLimitCount;
    }

    public Integer getSecondBrokenCount() {
        return secondBrokenCount;
    }

    public void setSecondBrokenCount(Integer secondBrokenCount) {
        this.secondBrokenCount = secondBrokenCount;
    }

    public BigDecimal getSecondBrokenRate() {
        return secondBrokenRate;
    }

    public void setSecondBrokenRate(BigDecimal secondBrokenRate) {
        this.secondBrokenRate = secondBrokenRate;
    }

    public Integer getThirdLimitCount() {
        return thirdLimitCount;
    }

    public void setThirdLimitCount(Integer thirdLimitCount) {
        this.thirdLimitCount = thirdLimitCount;
    }

    public Integer getThirdBrokenCount() {
        return thirdBrokenCount;
    }

    public void setThirdBrokenCount(Integer thirdBrokenCount) {
        this.thirdBrokenCount = thirdBrokenCount;
    }

    public BigDecimal getThirdBrokenRate() {
        return thirdBrokenRate;
    }

    public void setThirdBrokenRate(BigDecimal thirdBrokenRate) {
        this.thirdBrokenRate = thirdBrokenRate;
    }

    public Integer getMoreLimitCount() {
        return moreLimitCount;
    }

    public void setMoreLimitCount(Integer moreLimitCount) {
        this.moreLimitCount = moreLimitCount;
    }

    public Integer getMoreBrokenCount() {
        return moreBrokenCount;
    }

    public void setMoreBrokenCount(Integer moreBrokenCount) {
        this.moreBrokenCount = moreBrokenCount;
    }

    public BigDecimal getMoreBrokenRate() {
        return moreBrokenRate;
    }

    public void setMoreBrokenRate(BigDecimal moreBrokenRate) {
        this.moreBrokenRate = moreBrokenRate;
    }
}