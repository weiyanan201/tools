package wei.tools.model;

public class Calendar {

    private String dayStr;

    private String cnDay;

    private Integer status=0;

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

    @Override
    public String toString() {
        return "Calendar{" +
                ", dayStr='" + dayStr + '\'' +
                ", cnDay='" + cnDay + '\'' +
                ", status=" + status +
                '}';
    }
}