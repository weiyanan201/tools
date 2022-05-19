package wei.tools.model;

public class BrokenLimit {
    private Integer id;

    private String code;

    private String name;

    private String firstTime;

    private String lastTime;

    private String date;

    private Integer openCount;

    private String closeTimeMin;

    private String theme;

    private Float lossRate;

    private Float closePrice;

    private Integer brokenLimitType;

    private String limitDetail;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(String firstTime) {
        this.firstTime = firstTime;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getOpenCount() {
        return openCount;
    }

    public void setOpenCount(Integer openCount) {
        this.openCount = openCount;
    }

    public String getCloseTimeMin() {
        return closeTimeMin;
    }

    public void setCloseTimeMin(String closeTimeMin) {
        this.closeTimeMin = closeTimeMin;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Float getLossRate() {
        return lossRate;
    }

    public void setLossRate(Float lossRate) {
        this.lossRate = lossRate;
    }

    public Float getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(Float closePrice) {
        this.closePrice = closePrice;
    }

    public Integer getBrokenLimitType() {
        return brokenLimitType;
    }

    public void setBrokenLimitType(Integer brokenLimitType) {
        this.brokenLimitType = brokenLimitType;
    }

    public String getLimitDetail() {
        return limitDetail;
    }

    public void setLimitDetail(String limitDetail) {
        this.limitDetail = limitDetail;
    }

    @Override
    public String toString() {
        return "BrokenLimit{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", firstTime='" + firstTime + '\'' +
                ", lastTime='" + lastTime + '\'' +
                ", date='" + date + '\'' +
                ", openCount=" + openCount +
                ", closeTimeMin='" + closeTimeMin + '\'' +
                ", theme='" + theme + '\'' +
                ", lossRate=" + lossRate +
                ", closePrice=" + closePrice +
                ", brokenLimitType='" + brokenLimitType + '\'' +
                '}';
    }
}