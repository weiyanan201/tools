package wei.tools.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: weiyanan
 * @Date: 2022/2/8 14:52
 */
public class DateUtils {

    private static String dateFormat="yyyy-MM-dd";

    public static String getDateFormat(){
        return dateFormat;
    }

    public static String coverToUnSymbol(String dateStr){
        return dateStr.replaceAll("-","");
    }

    public static String getTodayStr(){
        DateFormat format = new SimpleDateFormat(dateFormat);
        Date today = new Date();
        return format.format(today);
    }

    public static void main(String[] args) {

//        String str = "[2022年1月4日的涨跌幅>0% (3309)2022年1月4日的涨跌幅<0% (1310)";
//        Pattern pattern = Pattern.compile("\\((\\d+)+\\).*\\((\\d+)+\\)");
//        Matcher matcher = pattern.matcher(str);
//        if (matcher.find()){
//            System.out.println(matcher.group(1));
//            System.out.println(matcher.group(2));
//        }
        System.out.println(DateUtils.getTodayStr());

    }

}
