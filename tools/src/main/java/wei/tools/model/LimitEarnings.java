package wei.tools.model;

import java.math.BigDecimal;

/**
 * 涨停、炸板收益-概率-统计
 */
public class LimitEarnings {
    private String date;

    private String limitType;

    private Integer limitNum;

    private BigDecimal avgOpen;

    private BigDecimal avgClose;

    private BigDecimal avgMax;

    private BigDecimal avgMin;

    private Integer isBroken;

    private String emotionalFlag;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLimitType() {
        return limitType;
    }

    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    public BigDecimal getAvgOpen() {
        return avgOpen;
    }

    public void setAvgOpen(BigDecimal avgOpen) {
        this.avgOpen = avgOpen;
    }

    public BigDecimal getAvgClose() {
        return avgClose;
    }

    public void setAvgClose(BigDecimal avgClose) {
        this.avgClose = avgClose;
    }

    public BigDecimal getAvgMax() {
        return avgMax;
    }

    public void setAvgMax(BigDecimal avgMax) {
        this.avgMax = avgMax;
    }

    public BigDecimal getAvgMin() {
        return avgMin;
    }

    public void setAvgMin(BigDecimal avgMin) {
        this.avgMin = avgMin;
    }

    public Integer getIsBroken() {
        return isBroken;
    }

    public void setIsBroken(Integer isBroken) {
        this.isBroken = isBroken;
    }

    public String getEmotionalFlag() {
        return emotionalFlag;
    }

    public void setEmotionalFlag(String emotionalFlag) {
        this.emotionalFlag = emotionalFlag;
    }
}