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
}
