package wei.tools.model;

public class TradingDay {

    public static int TRADING_TRUE = 1;
    public static int TRADING_FALSE = 0;

    private String dayStr;

    private String cnDay;

    private Integer status=0;

    private Integer isTrading;

    public String getDayStr() {
        return dayStr;
    }

    public void setDayStr(String dayStr) {
        this.dayStr = dayStr;
    }

    public String getCnDay() {
        return cnDay;
    }

    public void setCnDay(String cnDay) {
        this.cnDay = cnDay;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsTrading() {
        return isTrading;
    }

    public void setIsTrading(Integer isTrading) {
        this.isTrading = isTrading;
    }

    @Override
    public String toString() {
        return "TradingDay{" +
                "dayStr='" + dayStr + '\'' +
                ", cnDay='" + cnDay + '\'' +
                ", status=" + status +
                ", isTrading=" + isTrading +
                '}';
    }
}