package wei.tools.util;

import org.apache.commons.lang3.StringUtils;
import wei.tools.exception.ToolsException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: weiyanan
 * @Date: 2022/2/8 14:52
 */
public class DateUtils {


    private static String dateFormatStr="yyyy-MM-dd";

    //dateFormat 正则校验
    private static Pattern datePattern = Pattern.compile("^(20\\d{2})-((0{1}[0-9]{1})|(1{1}[0-2]{1}))-((0{1}[1-9]{1})|([1-2]{1}[0-9]{1})|(3{1}[0-1]{1})){1}$");

    public static String getDateFormat(){
        return dateFormatStr;
    }

    public static String coverToUnSymbol(String dateStr){
        if (StringUtils.isBlank(dateStr)){
            return null;
        }
        return dateStr.replaceAll("-","");
    }

    public static String getTodayStr(){
        DateFormat format = new SimpleDateFormat(dateFormatStr);
        Date today = new Date();
        return format.format(today);
    }

    public static boolean checkFormat(String dateStr)  {

        Matcher matcher = datePattern.matcher(dateStr);
        if (matcher.find()){
            return true;
        }
        throw new ToolsException("日期格式不正确，请传入yyyy-MM-dd 格式");
    }

    /**
     * 获取下个日期的日子
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static String getNextDay(String dateStr) throws ParseException {

        DateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
        Date date = dateFormat.parse(dateStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.DAY_OF_MONTH,1);

        return dateFormat.format(calendar.getTime());
    }

    /**
     *
     * @param day1
     * @param day2
     * @return day1-day2
     * @throws ParseException
     */
    public static int diffBetweenDays(String day1,String day2) throws ParseException {
        checkFormat(day1);
        checkFormat(day2);
        DateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
        Date date1 = dateFormat.parse(day1);
        Date date2 = dateFormat.parse(day2);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);

        Long betweenDays = (calendar1.getTimeInMillis() - calendar2.getTimeInMillis()) / (1000 * 3600 * 24);//计算相差天数
        return betweenDays.intValue();
    }

    public static void main(String[] args) throws ParseException {

//        String str = "[2022年1月4日的涨跌幅>0% (3309)2022年1月4日的涨跌幅<0% (1310)";
//        Pattern pattern = Pattern.compile("\\((\\d+)+\\).*\\((\\d+)+\\)");
//        Matcher matcher = pattern.matcher(str);
//        if (matcher.find()){
//            System.out.println(matcher.group(1));
//            System.out.println(matcher.group(2));
//        }


        System.out.println(DateUtils.getNextDay("2021-01-31"));

    }

}
