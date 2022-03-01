package wei.tools.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 *
 * @Author: weiyanan
 * @Date: 2022/2/16 15:01
 */
public class DecimalUtils {

    private static DecimalFormat df = new DecimalFormat("#.00");

    public static BigDecimal getBigDecimal(Float number){
        return new BigDecimal(number).setScale(2,BigDecimal.ROUND_HALF_UP);
    }

    public static float roundValue(float number){
        return new BigDecimal(number).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static float roundValue(BigDecimal bigDecimal){
        return bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static void main(String[] args) {
        BigDecimal bigDecimal = new BigDecimal(133.446667);
        System.out.println(covertPercent(bigDecimal));
    }

    /**
     * 两位小数百分号
     * @param bigDecimal
     * @return
     */
    public static String covertPercent(BigDecimal bigDecimal){
        DecimalFormat df = new DecimalFormat("0.00%");
        return df.format(bigDecimal.floatValue());
    }

    public static String covertPercent(float number){
        DecimalFormat df = new DecimalFormat("0.00%");
        return df.format(number);
    }

}
