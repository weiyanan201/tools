package wei.tools.entity;

/**
 * 情绪周期表 统计
 * @Author: weiyanan
 * @Date: 2022/2/8 14:57
 */
public class WenCaiQueryEntity {

    //上涨个数
    private int upCount ;
    //下跌个数
    private int dropCount;
    //涨停个数
    private int upLimitCount;
    //跌停个数
    private int dropLimitCount;
    //炸板个数
    private int breakCount;
    //首板个数
    private int firstCount;
    //2板个数
    private int secondCount;
    //3板上个数
    private int thirdCount;
    //3板以上个数
    private int moreCount;
    //3板个股
    private String thirdStr;
    //3板以上个股
    private String moreStr;


    public int getUpCount() {
        return upCount;
    }

    public void setUpCount(int upCount) {
        this.upCount = upCount;
    }

    public int getDropCount() {
        return dropCount;
    }

    public void setDropCount(int dropCount) {
        this.dropCount = dropCount;
    }

    public int getUpLimitCount() {
        return upLimitCount;
    }

    public void setUpLimitCount(int upLimitCount) {
        this.upLimitCount = upLimitCount;
    }

    public int getDropLimitCount() {
        return dropLimitCount;
    }

    public void setDropLimitCount(int dropLimitCount) {
        this.dropLimitCount = dropLimitCount;
    }

    public int getBreakCount() {
        return breakCount;
    }

    public void setBreakCount(int breakCount) {
        this.breakCount = breakCount;
    }

    public int getFirstCount() {
        return firstCount;
    }

    public void setFirstCount(int firstCount) {
        this.firstCount = firstCount;
    }

    public int getSecondCount() {
        return secondCount;
    }

    public void setSecondCount(int secondCount) {
        this.secondCount = secondCount;
    }

    public int getThirdCount() {
        return thirdCount;
    }

    public void setThirdCount(int thirdCount) {
        this.thirdCount = thirdCount;
    }

    public int getMoreCount() {
        return moreCount;
    }

    public void setMoreCount(int moreCount) {
        this.moreCount = moreCount;
    }

    public String getThirdStr() {
        return thirdStr;
    }

    public void setThirdStr(String thirdStr) {
        this.thirdStr = thirdStr;
    }

    public String getMoreStr() {
        return moreStr;
    }

    public void setMoreStr(String moreStr) {
        this.moreStr = moreStr;
    }

    @Override
    public String toString() {
        return "WenCaiQueryEntity{" +
                "upCount=" + upCount +
                ", dropCount=" + dropCount +
                ", upLimitCount=" + upLimitCount +
                ", dropLimitCount=" + dropLimitCount +
                ", breakCount=" + breakCount +
                ", firstCount=" + firstCount +
                ", secondCount=" + secondCount +
                ", thirdCount=" + thirdCount +
                ", moreCount=" + moreCount +
                ", thirdStr='" + thirdStr + '\'' +
                ", moreStr='" + moreStr + '\'' +
                '}';
    }
}
