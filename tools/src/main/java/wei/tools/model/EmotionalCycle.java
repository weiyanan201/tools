package wei.tools.model;

import java.math.BigDecimal;

public class EmotionalCycle {
    private String dateStr;

    private Integer upCount;

    private Integer dropCount;

    private Integer upLimit;

    private Integer dropLimit;

    private Integer brokenLimit;

    private Integer firstLimit;

    private Integer secondLimit;

    private Integer thirdLimit;

    private String thirdLimitStr;

    private Integer moreLimit;

    private String moreLimitStr;

    private BigDecimal earningLimitRate;

    private BigDecimal earningFirstLimitRate;

    private BigDecimal earningSequenceLimitReate;

    private BigDecimal earningBrokenLimitRate;

    private String hotBusinessOrderLimit;

    private String hotBusinessOrderRose;

    private String hotThemeOrderLimit;

    private String hotThemeOrderRose;

    public EmotionalCycle(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public Integer getUpCount() {
        return upCount;
    }

    public void setUpCount(Integer upCount) {
        this.upCount = upCount;
    }

    public Integer getDropCount() {
        return dropCount;
    }

    public void setDropCount(Integer dropCount) {
        this.dropCount = dropCount;
    }

    public Integer getUpLimit() {
        return upLimit;
    }

    public void setUpLimit(Integer upLimit) {
        this.upLimit = upLimit;
    }

    public Integer getDropLimit() {
        return dropLimit;
    }

    public void setDropLimit(Integer dropLimit) {
        this.dropLimit = dropLimit;
    }

    public Integer getBrokenLimit() {
        return brokenLimit;
    }

    public void setBrokenLimit(Integer brokenLimit) {
        this.brokenLimit = brokenLimit;
    }

    public Integer getFirstLimit() {
        return firstLimit;
    }

    public void setFirstLimit(Integer firstLimit) {
        this.firstLimit = firstLimit;
    }

    public Integer getSecondLimit() {
        return secondLimit;
    }

    public void setSecondLimit(Integer secondLimit) {
        this.secondLimit = secondLimit;
    }

    public Integer getThirdLimit() {
        return thirdLimit;
    }

    public void setThirdLimit(Integer thirdLimit) {
        this.thirdLimit = thirdLimit;
    }

    public Integer getMoreLimit() {
        return moreLimit;
    }

    public void setMoreLimit(Integer moreLimit) {
        this.moreLimit = moreLimit;
    }

    public BigDecimal getEarningLimitRate() {
        return earningLimitRate;
    }

    public void setEarningLimitRate(BigDecimal earningLimitRate) {
        this.earningLimitRate = earningLimitRate;
    }

    public BigDecimal getEarningFirstLimitRate() {
        return earningFirstLimitRate;
    }

    public void setEarningFirstLimitRate(BigDecimal earningFirstLimitRate) {
        this.earningFirstLimitRate = earningFirstLimitRate;
    }

    public BigDecimal getEarningSequenceLimitReate() {
        return earningSequenceLimitReate;
    }

    public void setEarningSequenceLimitReate(BigDecimal earningSequenceLimitReate) {
        this.earningSequenceLimitReate = earningSequenceLimitReate;
    }

    public BigDecimal getEarningBrokenLimitRate() {
        return earningBrokenLimitRate;
    }

    public void setEarningBrokenLimitRate(BigDecimal earningBrokenLimitRate) {
        this.earningBrokenLimitRate = earningBrokenLimitRate;
    }

    public String getHotBusinessOrderLimit() {
        return hotBusinessOrderLimit;
    }

    public void setHotBusinessOrderLimit(String hotBusinessOrderLimit) {
        this.hotBusinessOrderLimit = hotBusinessOrderLimit;
    }

    public String getHotBusinessOrderRose() {
        return hotBusinessOrderRose;
    }

    public void setHotBusinessOrderRose(String hotBusinessOrderRose) {
        this.hotBusinessOrderRose = hotBusinessOrderRose;
    }

    public String getHotThemeOrderLimit() {
        return hotThemeOrderLimit;
    }

    public void setHotThemeOrderLimit(String hotThemeOrderLimit) {
        this.hotThemeOrderLimit = hotThemeOrderLimit;
    }

    public String getHotThemeOrderRose() {
        return hotThemeOrderRose;
    }

    public void setHotThemeOrderRose(String hotThemeOrderRose) {
        this.hotThemeOrderRose = hotThemeOrderRose;
    }

    public String getThirdLimitStr() {
        return thirdLimitStr;
    }

    public void setThirdLimitStr(String thirdLimitStr) {
        this.thirdLimitStr = thirdLimitStr;
    }

    public String getMoreLimitStr() {
        return moreLimitStr;
    }

    public void setMoreLimitStr(String moreLimitStr) {
        this.moreLimitStr = moreLimitStr;
    }

    @Override
    public String toString() {
        return "EmotionalCycle{" +
                "dateStr='" + dateStr + '\'' +
                ", upCount=" + upCount +
                ", dropCount=" + dropCount +
                ", upLimit=" + upLimit +
                ", dropLimit=" + dropLimit +
                ", brokenLimit=" + brokenLimit +
                ", firstLimit=" + firstLimit +
                ", secondLimit=" + secondLimit +
                ", thirdLimit=" + thirdLimit +
                ", thirdLimitStr='" + thirdLimitStr + '\'' +
                ", moreLimit=" + moreLimit +
                ", moreLimitStr='" + moreLimitStr + '\'' +
                ", earningLimitRate=" + earningLimitRate +
                ", earningFirstLimitRate=" + earningFirstLimitRate +
                ", earningSequenceLimitReate=" + earningSequenceLimitReate +
                ", earningBrokenLimitRate=" + earningBrokenLimitRate +
                ", hotBusinessOrderLimit='" + hotBusinessOrderLimit + '\'' +
                ", hotBusinessOrderRose='" + hotBusinessOrderRose + '\'' +
                ", hotThemeOrderLimit='" + hotThemeOrderLimit + '\'' +
                ", hotThemeOrderRose='" + hotThemeOrderRose + '\'' +
                '}';
    }

    public String openQueryToString(){
        return "EmotionalCycle{" +
                "dateStr='" + dateStr + '\'' +
                ", upCount=" + upCount +
                ", dropCount=" + dropCount +
                ", upLimit=" + upLimit +
                ", dropLimit=" + dropLimit +
                ", brokenLimit=" + brokenLimit +
                ", firstLimit=" + firstLimit +
                ", secondLimit=" + secondLimit +
                ", thirdLimit=" + thirdLimit +
                ", thirdLimitStr='" + thirdLimitStr + '\'' +
                ", moreLimit=" + moreLimit +
                ", moreLimitStr='" + moreLimitStr + '\'' +
                ", hotBusinessOrderLimit='" + hotBusinessOrderLimit + '\'' +
                '}';
    }
}