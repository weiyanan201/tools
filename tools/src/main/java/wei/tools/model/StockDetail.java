package wei.tools.model;

import java.math.BigDecimal;

public class StockDetail {
    private String code;

    private String name;

    private String dateStr;

    private BigDecimal openPrice;

    private BigDecimal openPriceRate;

    private BigDecimal closePrice;

    private BigDecimal closePriceRate;

    private BigDecimal diffPrice;

    private BigDecimal minPrice;

    private BigDecimal minPriceRate;

    private BigDecimal maxPrice;

    private BigDecimal maxPriceRate;

    private BigDecimal turnoverRate;

    private BigDecimal swingRate;

    private BigDecimal lastDayClosePrice;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public BigDecimal getOpenPriceRate() {
        return openPriceRate;
    }

    public void setOpenPriceRate(BigDecimal openPriceRate) {
        this.openPriceRate = openPriceRate;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    public BigDecimal getClosePriceRate() {
        return closePriceRate;
    }

    public void setClosePriceRate(BigDecimal closePriceRate) {
        this.closePriceRate = closePriceRate;
    }

    public BigDecimal getDiffPrice() {
        return diffPrice;
    }

    public void setDiffPrice(BigDecimal diffPrice) {
        this.diffPrice = diffPrice;
    }


    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMinPriceRate() {
        return minPriceRate;
    }

    public void setMinPriceRate(BigDecimal minPriceRate) {
        this.minPriceRate = minPriceRate;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public BigDecimal getMaxPriceRate() {
        return maxPriceRate;
    }

    public void setMaxPriceRate(BigDecimal maxPriceRate) {
        this.maxPriceRate = maxPriceRate;
    }

    public BigDecimal getTurnoverRate() {
        return turnoverRate;
    }

    public void setTurnoverRate(BigDecimal turnoverRate) {
        this.turnoverRate = turnoverRate;
    }

    public BigDecimal getSwingRate() {
        return swingRate;
    }

    public void setSwingRate(BigDecimal swingRate) {
        this.swingRate = swingRate;
    }

    public BigDecimal getLastDayClosePrice() {
        return lastDayClosePrice;
    }

    public void setLastDayClosePrice(BigDecimal lastDayClosePrice) {
        this.lastDayClosePrice = lastDayClosePrice;
    }

    @Override
    public String toString() {
        return "StockDetail{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", dateStr='" + dateStr + '\'' +
                ", openPrice=" + openPrice +
                ", openPriceRate=" + openPriceRate +
                ", closePrice=" + closePrice +
                ", closePriceRate=" + closePriceRate +
                ", diffPrice=" + diffPrice +
                ", minPrice=" + minPrice +
                ", minPriceRate=" + minPriceRate +
                ", maxPrice=" + maxPrice +
                ", maxPriceRate=" + maxPriceRate +
                ", turnoverRate=" + turnoverRate +
                ", swingRate=" + swingRate +
                ", lastDayClosePrice=" + lastDayClosePrice +
                '}';
    }
}