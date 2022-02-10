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
}