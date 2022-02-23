package wei.tools.util;

import org.apache.commons.lang3.StringUtils;
import wei.tools.exception.ToolsException;

import javax.script.*;
import java.io.*;
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
    private static Pattern unSymbolDatePattern = Pattern.compile("^(20\\d{2})((0{1}[0-9]{1})|(1{1}[0-2]{1}))((0{1}[1-9]{1})|([1-2]{1}[0-9]{1})|(3{1}[0-1]{1})){1}$");

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

    public static boolean checkUnSymbolFormat(String dateStr)  {

        Matcher matcher = unSymbolDatePattern.matcher(dateStr);
        if (matcher.find()){
            return true;
        }
        throw new ToolsException("日期格式不正确，请传入yyyyMMdd 格式");
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

    public static String str;

    public static void main(String[] args) throws Exception {
//        testJSFile();
        String str = "20220222";
        Matcher matcher = unSymbolDatePattern.matcher(str);
        if (matcher.find()){
            System.out.println(matcher.group());
        }
    }

    private static void testJSFile() throws Exception {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("javascript");
        engine.eval(readJSFile());
        Invocable inv = (Invocable) engine;
        Object res = (Object) inv.invokeFunction("v");
        System.out.println("res:" + res);
    }

    private static String readJSFile() throws Exception {
        StringBuffer script = new StringBuffer();
        File file = new File("C:\\Users\\homay\\Desktop\\aes.min.js");
        FileReader filereader = new FileReader(file);
        BufferedReader bufferreader = new BufferedReader(filereader);
        String tempString = null;
        while ((tempString = bufferreader.readLine()) != null) {
            script.append(tempString).append("\n");
        }
        bufferreader.close();
        filereader.close();
        return script.toString();
    }

}
