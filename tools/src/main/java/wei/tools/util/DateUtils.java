package wei.tools.util;

import org.apache.commons.lang3.StringUtils;
import wei.tools.exception.ToolsException;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
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

    //dateFormat 正则校验
    private static Pattern datePattern = Pattern.compile("^(20\\d{2})-((0{1}[0-9]{1})|(1{1}[0-2]{1}))-((0{1}[1-9]{1})|([1-2]{1}[0-9]{1})|(3{1}[0-1]{1})){1}$");

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

    public static boolean checkFormat(String dateStr)  {

        Matcher matcher = datePattern.matcher(dateStr);
        if (matcher.find()){
            return true;
        }
        throw new ToolsException("日期格式不正确，请传入yyyy-MM-dd 格式");
    }

    public static void main(String[] args) throws ParseException {

//        String str = "[2022年1月4日的涨跌幅>0% (3309)2022年1月4日的涨跌幅<0% (1310)";
//        Pattern pattern = Pattern.compile("\\((\\d+)+\\).*\\((\\d+)+\\)");
//        Matcher matcher = pattern.matcher(str);
//        if (matcher.find()){
//            System.out.println(matcher.group(1));
//            System.out.println(matcher.group(2));
//        }

        System.out.println(DateUtils.checkFormat("2021-13-12"));

    }

}
