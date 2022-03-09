package wei.tools.model;

/**
 * 时间段封板率
 */

import java.math.BigDecimal;

public class TimeSlotHoldRate {
    private String date;

    private Integer timeSlot;

    private Integer fCount;

    private BigDecimal brokenLost;

    private BigDecimal openRate;

    private BigDecimal maxRate;

    private BigDecimal minRate;

    private BigDecimal closeRate;

    private Integer isBroken;

    private Integer isFirst;

    private BigDecimal holdRate;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(Integer timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Integer getfCount() {
        return fCount;
    }

    public void setfCount(Integer fCount) {
        this.fCount = fCount;
    }

    public BigDecimal getBrokenLost() {
        return brokenLost;
    }

    public void setBrokenLost(BigDecimal brokenLost) {
        this.brokenLost = brokenLost;
    }

    public BigDecimal getOpenRate() {
        return openRate;
    }

    public void setOpenRate(BigDecimal openRate) {
        this.openRate = openRate;
    }

    public BigDecimal getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(BigDecimal maxRate) {
        this.maxRate = maxRate;
    }

    public BigDecimal getMinRate() {
        return minRate;
    }

    public void setMinRate(BigDecimal minRate) {
        this.minRate = minRate;
    }

    public BigDecimal getCloseRate() {
        return closeRate;
    }

    public void setCloseRate(BigDecimal closeRate) {
        this.closeRate = closeRate;
    }

    public Integer getIsBroken() {
        return isBroken;
    }

    public void setIsBroken(Integer isBroken) {
        this.isBroken = isBroken;
    }

    public Integer getIsFirst() {
        return isFirst;
    }

    public void setIsFirst(Integer isFirst) {
        this.isFirst = isFirst;
    }

    public BigDecimal getHoldRate() {
        return holdRate;
    }

    public void setHoldRate(BigDecimal holdRate) {
        this.holdRate = holdRate;
    }
}