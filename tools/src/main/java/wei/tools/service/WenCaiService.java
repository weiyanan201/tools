package wei.tools.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import wei.tools.dao.UpLimitMapper;
import wei.tools.model.*;
import wei.tools.util.CollectionUtils;
import wei.tools.util.DateUtils;
import wei.tools.util.DecimalUtils;

import javax.annotation.PostConstruct;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: weiyanan
 * @Date: 2022/2/8 14:57
 */
@Service
public class WenCaiService {

    private static String wencaiUrl = "http://www.iwencai.com/customized/chart/get-robot-data";

    private static Logger logger = LoggerFactory.getLogger(WenCaiService.class);

    //情绪周期表维度
    public static String QUERY_FORMAT_ZHANGTING= "%s涨停;非st;非新股；%s连续涨停天数>=1；上市时间超过50天";
    public static String QUERY_FORMAT_UP_DROP = "%s上涨个股；%s下跌个股；";
    public static String QUERY_FORMAT_DIETING = "%s 跌停;非st；";
    public static String QUERY_FORMAT_BROKEN = "%s炸板；非st";

    //情绪周期表维度--历史数据
    public static String QUERY_FORMAT_ZHANGTING_HISTORY= "%s涨停；%s连续涨停天数>=1；上市时间超过50天";
    public static String QUERY_FORMAT_DIETING_HISTORY = "%s 跌停";
    public static String QUERY_FORMAT_BROKEN_HISTORY = "%s炸板";

    //行业 涨停个数维度
    public static String QUERY_FORMAT_BUSINESS_UPLIMIT = "%s 板块涨幅及涨停个数；同花顺行业指数；按 %s 涨停个数从大到小排序";
    //行业 涨幅维度
    public static String QUERY_FORMAT_BUSINESS_ROSE = "%s 板块涨幅及涨停个数；同花顺行业指数；按 %s 涨跌幅从大到小排序";
    //题材 涨停个数维度
    public static String QUERY_FORMAT_THEME_UPLIMIT = "%s 板块涨幅及涨停个数；同花顺概念指数；按 %s 涨停个数从大到小排序";
    //题材 涨幅维度
    public static String QUERY_FORMAT_THEME_ROSE = "%s 板块涨幅及涨停个数；同花顺概念指数；按 %s 涨跌幅从大到小排序";
    //

    //涨停字段
    private static String FIELD_LIMIT_TYPE_FORMAT = "涨停类型[%s]";
    private static String FIELD_LIMIT_REASON_FORMAT = "涨停原因类别[%s]";
    private static String FIELD_CODE = "股票代码";
    private static String FIELD_SEQUENCE_TYPE_FORMAT = "几天几板[%s]";
    private static String FIELD_SEQUENCE_COUNT_FORMAT = "连续涨停天数[%s]";
    private static String FIELD_NAME = "股票简称";
    private static String FIELD_LAST_TIME_FORMAT = "最终涨停时间[%s]";
    private static String FIELD_OPEN_COUNT_FORMAT = "涨停开板次数[%s]";
    private static String FIELD_FIRST_TIME_FORMAT = "首次涨停时间[%s]";

    //跌停字段
    private static String FIELD_DROP_TYPE_FORMAT = "跌停类型[%s]";
    private static String FIELD_DROP_SEASON_FORMAT = "跌停原因类型[%s]";
    private static String FIELD_DROP_CODE = "股票代码";
    private static String FIELD_DROP_NAME = "股票简称";
    private static String FIELD_DROP_SEQUENCE_COUNT_FORMAT = "连续跌停天数[%s]";

    //破板字段
    private static String FIELD_BROKEN_CODE = "code";
    private static String FIELD_BROKEN_CLOSE_TIME_MIN_FORMAT = "涨停封板时长[%s]";
    private static String FIELD_BROKEN_OPEN_COUNT_FORMAT = "涨停开板次数[%s]";
    private static String FIELD_BROKEN_LOSS_RATE = "最新涨跌幅";
    private static String FIELD_BROKEN_NAME = "股票简称";
    private static String FIELD_BROKEN_FIRST_TIME_FORMAT = "首次涨停时间[%s]";
    private static String FIELD_BROKEN_LAST_TIME_FORMAT = "涨停时间明细[%s]";
    private static String FIELD_BROKEN_CLOSE_PRICE = "最新价";

    //板块字段
    private static String FIELD_BUSINESS_NAME = "指数简称";
    private static String FIELD_BUSINESS_ROSE_FORMAT = "指数@涨跌幅:前复权[%s]";
    private static String FIELD_BUSINESS_LIMIT_FORMAT = "指数@涨停家数[%s]";

    //上涨下跌提取
    private static Pattern upDropCountPattern = Pattern.compile("\\((\\d+)+\\).*\\((\\d+)+\\)");

    @Value("${review.filePath}")
    private String excelFilePath ;

    @Autowired
    private ApiService apiService;
    @Autowired
    private StockDetailService stockDetailService;
    @Autowired
    private UpLimitMapper upLimitMapper;
    @Autowired
    private TradingDayService tradingDayService;

    private static StringBuilder scriptSb = new StringBuilder();
    private static ScriptEngine engine;
    private static Invocable jsInv;

    @PostConstruct
    private void init() throws IOException, ScriptException {

        String jsPath = ResourceUtils.getURL("classpath:aes.min.js").getPath();
        File file = new File(jsPath);
        FileReader filereader = new FileReader(file);
        BufferedReader bufferreader = new BufferedReader(filereader);
        String tempString = null;
        while ((tempString = bufferreader.readLine()) != null) {
            scriptSb.append(tempString).append("\n");
        }
        bufferreader.close();
        filereader.close();

        ScriptEngineManager mgr = new ScriptEngineManager();
        engine = mgr.getEngineByName("javascript");
        engine.eval(scriptSb.toString());
        jsInv = (Invocable) engine;

    }

    /**
     * 获取题材
     * @param stockName
     * @return
     */
    public String queryStockTheme(String stockName){
        try{
            JSONObject paramJson = getWencaiJson(stockName);
            JSONObject resultJson = apiService.post(wencaiUrl,paramJson,getCookieHeaders());

            JSONObject dataJson = resultJson.getJSONObject("data");
            JSONArray answerArray = dataJson.getJSONArray("answer");
            JSONObject answerJson = answerArray.getJSONObject(0);
            JSONArray txtArray = answerJson.getJSONArray("txt");
            JSONObject txtJson = txtArray.getJSONObject(0);
            JSONObject contentJson = txtJson.getJSONObject("content");
            JSONArray componentsArray = contentJson.getJSONArray("components");
            JSONObject componentJson = componentsArray.getJSONObject(0);
            JSONArray component_data_array = componentJson.getJSONArray("data");
            JSONObject component_data_json = component_data_array.getJSONObject(0);
            return component_data_json.getString("所属概念");
        }catch (Exception e){
            logger.warn("{} 提取题材出现异常，已忽略",stockName);
            return null;
        }

    }

    /**
     * 问财股票接口参数格式
     * @param question
     * @return
     */
    private JSONObject getWencaiJson(String question){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question",question);
        jsonObject.put("perpage",200);
        jsonObject.put("page",1);
        jsonObject.put("secondary_intent","stock");
        jsonObject.put("log_info","{\"input_type\":\"typewrite\"}");
        jsonObject.put("source","Ths_iwencai_Xuangu");
        jsonObject.put("version","2.0");
        jsonObject.put("query_area","");
        jsonObject.put("block_lisft","");
        jsonObject.put("add_info","{\"urp\":{\"scene\":1,\"company\":1,\"business\":1},\"contentType\":\"json\",\"searchInfo\":true}");
        return jsonObject;
    }

    /**
     * 问财指数接口参数格式
     * @param question
     * @return
     */
    private JSONObject getWencaiZhiShuJson(String question){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question",question);
        jsonObject.put("perpage",500);
        jsonObject.put("page",1);
        jsonObject.put("secondary_intent","zhishu");
        jsonObject.put("log_info","{\"input_type\":\"typewrite\"}");
        jsonObject.put("source","Ths_iwencai_Xuangu");
        jsonObject.put("version","2.0");
        jsonObject.put("query_area","");
        jsonObject.put("block_lisft","");
        jsonObject.put("add_info","{\"urp\":{\"scene\":1,\"company\":1,\"business\":1},\"contentType\":\"json\",\"searchInfo\":true}");
        return jsonObject;
    }

    /**
     * 统计行业指数
     * @param emotionalCycle
     * @param date
     */
    public void queryHotBusiness(EmotionalCycle emotionalCycle, String date)  {

        //安涨停个数统计
        String byUpLimitStr = String.format(QUERY_FORMAT_BUSINESS_UPLIMIT,date,date);
        JSONObject upLimitJson = getWencaiZhiShuJson(byUpLimitStr);
        JSONObject upLimitResultJson = apiService.post(wencaiUrl,upLimitJson,getCookieHeaders());

        String dateStr = DateUtils.coverToUnSymbol(date);

        String field_business_name = FIELD_BUSINESS_NAME;
        String field_business_rose = String.format(FIELD_BUSINESS_ROSE_FORMAT,dateStr);
        String field_business_limit = String.format(FIELD_BUSINESS_LIMIT_FORMAT,dateStr);

        JSONObject dataJson = upLimitResultJson.getJSONObject("data");
        JSONArray c_data_datas_array = parseToDatas(dataJson);

        int count = 0;
        StringBuilder resultSb = new StringBuilder();
        //TODO 暂时取前5,具体逻辑没想好
        for (int i=0;i<c_data_datas_array.size();i++){
            if (count<5){
                JSONObject resultJson = c_data_datas_array.getJSONObject(i);
                String name = resultJson.getString(field_business_name);
//                float rose = DecimalUtils.roundValue(resultJson.getFloat(field_business_rose));
                String limits = resultJson.getString(field_business_limit);
                resultSb.append(name).append(":").append(limits).append(",");
                count++;
            }else{
                break;
            }
        }
        if (resultSb.indexOf(",")!=-1){
            resultSb.delete(resultSb.lastIndexOf(","),resultSb.length());
        }
        emotionalCycle.setHotBusinessOrderLimit(resultSb.toString());

    }

    private HttpHeaders getCookieHeaders()  {
        HttpHeaders headers = new HttpHeaders();

        try{
            Object res = (Object) jsInv.invokeFunction("v");
            headers.add("Cookie", "v="+res);
            headers.add("Accept","application/json, text/plain, */*");
            headers.add("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.80 Safari/537.36");
            headers.setContentType(MediaType.APPLICATION_JSON);
        }catch (Exception e){
            logger.error("js 脚本执行出错",e);
        }
        return headers;

    }

    private JSONArray parseToDatas(JSONObject dataJson ){
        JSONArray answerArray = dataJson.getJSONArray("answer");
        JSONObject answerJson = answerArray.getJSONObject(0);
        JSONArray txtArray = answerJson.getJSONArray("txt");
        JSONObject txtJson = txtArray.getJSONObject(0);
        JSONObject contentJson = txtJson.getJSONObject("content");
        JSONArray componentsArray = contentJson.getJSONArray("components");
        JSONObject componentJson = componentsArray.getJSONObject(0);
        JSONObject c_dataJson = componentJson.getJSONObject("data");
        JSONArray c_data_datas_array = c_dataJson.getJSONArray("datas");
        return c_data_datas_array;
    }

    //补历史数据
    public List<UpLimit> queryUpLimit(EmotionalCycle emotionalCycle,String date,boolean isHistory)  {

        //2022-02-07 -> 20220207
//        String dateStr = "2022-02-07";
        String dateStr = DateUtils.coverToUnSymbol(date);
        String zhangtingStr = "";
        if (isHistory){
            zhangtingStr = String.format(QUERY_FORMAT_ZHANGTING_HISTORY,dateStr,dateStr);
        }else {
            zhangtingStr = String.format(QUERY_FORMAT_ZHANGTING,dateStr,dateStr);
        }

        JSONObject zhangtingJson = getWencaiJson(zhangtingStr);
        JSONObject zhangtingResult = apiService.post(wencaiUrl,zhangtingJson,getCookieHeaders());


        String field_limit_type = String.format(FIELD_LIMIT_TYPE_FORMAT,dateStr);
        String field_limit_reason = String.format(FIELD_LIMIT_REASON_FORMAT,dateStr);
        String field_code = FIELD_CODE;
        String field_sequence_type = String.format(FIELD_SEQUENCE_TYPE_FORMAT,dateStr);
        String field_sequence_count = String.format(FIELD_SEQUENCE_COUNT_FORMAT,dateStr);
        String field_name = FIELD_NAME;
        String field_last_time = String.format(FIELD_LAST_TIME_FORMAT,dateStr);
        String field_open_count = String.format(FIELD_OPEN_COUNT_FORMAT,dateStr);
        String field_first_time = String.format(FIELD_FIRST_TIME_FORMAT,dateStr);

        List<UpLimit> lists = Lists.newArrayList();
        //处理reason 统计题材
        Map<String,Integer> themeMap = Maps.newHashMap();

        JSONObject dataJson = zhangtingResult.getJSONObject("data");
        JSONArray answerArray = dataJson.getJSONArray("answer");
        JSONObject answerJson = answerArray.getJSONObject(0);
        JSONArray txtArray = answerJson.getJSONArray("txt");
        JSONObject txtJson = txtArray.getJSONObject(0);
        JSONObject contentJson = txtJson.getJSONObject("content");
        JSONArray componentsArray = contentJson.getJSONArray("components");
        JSONObject componentJson = componentsArray.getJSONObject(0);
        JSONObject c_dataJson = componentJson.getJSONObject("data");
        JSONArray c_data_datas_array = c_dataJson.getJSONArray("datas");

        for (int i=0;i<c_data_datas_array.size();i++){
            JSONObject js = c_data_datas_array.getJSONObject(i);
            UpLimit upLimit = new UpLimit();
            upLimit.setDate(dateStr);
            upLimit.setLimitType(js.getString(field_limit_type));
            upLimit.setReason(StringUtils.isBlank(js.getString(field_limit_reason))?"-":js.getString(field_limit_reason));
            if (!StringUtils.isBlank(js.getString(field_limit_reason))){
                String[] themes = js.getString(field_limit_reason).split("\\+");
                for (String theme :themes){
                    if (StringUtils.containsIgnoreCase(theme,"ST")){
                        continue;
                    }
                    if (!themeMap.containsKey(theme)){
                        themeMap.put(theme,new Integer(0));
                    }
                    themeMap.put(theme,themeMap.get(theme)+1);
                }
            }

            String code = js.getString(field_code);
            if (isHistory && isST(code,js.getString(field_name),dateStr)){
                continue;
            }
            if (StringUtils.isBlank(code)){
                continue;
            }
            code = code.substring(0,6);
            upLimit.setCode(code);
            upLimit.setSequenceType(js.getString(field_sequence_type));
            upLimit.setSequenceCount(js.getInteger(field_sequence_count));
            upLimit.setName(js.getString(field_name));
            upLimit.setFirstTime(js.getString(field_first_time));
            upLimit.setLastTime(js.getString(field_last_time));
            upLimit.setOpenCount(js.getInteger(field_open_count));
            lists.add(upLimit);
        }

        Map<String,Integer> sortedThemeMap = CollectionUtils.sortByValueDesc(themeMap);
        StringBuilder sb = new StringBuilder();
        int count = 0;
        //题材取前5个
        for (Map.Entry<String,Integer> entry : sortedThemeMap.entrySet()){
            count++;
            if (count<=5){
                sb.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
            }else{
                break;
            }
        }
        if (sb.indexOf(",")!=-1){
            sb.delete(sb.lastIndexOf(","),sb.length());
        }
        emotionalCycle.setHotThemeOrderLimit(sb.toString());

        int totalCount = 0;
        int firstCount = 0;
        int secondCount = 0;
        int thirdCount = 0;
        int moreCount = 0;
        StringBuilder thirdSb = new StringBuilder();
        StringBuilder moreSb = new StringBuilder();

        Map<String,Integer> moreMap = Maps.newHashMap();
        //统计涨停梯队个数
        for (UpLimit upLimit : lists){
            int upCount = upLimit.getSequenceCount();
            totalCount++;
            if (upCount==1){
                firstCount++;
            }else if (upCount==2){
                secondCount++;
            }else if(upCount==3){
                thirdCount++;
                thirdSb.append(upLimit.getName()).append("、");
            }else{
                moreCount++;
                moreMap.put(upLimit.getName(),upLimit.getSequenceCount());
            }
        }

        //降序比较器
        Comparator<Map.Entry<String, Integer>> valueComparator = new Comparator<Map.Entry<String,Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return o2.getValue()-o1.getValue();
            }
        };

        // map转换成list进行排序
        List<Map.Entry<String, Integer>> sortList = new ArrayList<Map.Entry<String,Integer>>(moreMap.entrySet());
        // 排序
        Collections.sort(sortList,valueComparator);

        for (Map.Entry<String, Integer> entry : sortList) {
            moreSb.append(entry.getKey()).append(entry.getValue()).append("、");
        }

        if (thirdSb.indexOf("、")!=-1){
            thirdSb.delete(thirdSb.lastIndexOf("、"),thirdSb.length());
        }
        if (moreSb.indexOf("、")!=-1){
            moreSb.delete(moreSb.lastIndexOf("、"),moreSb.length());
        }

        emotionalCycle.setUpLimit(totalCount);
        emotionalCycle.setFirstLimit(firstCount);
        emotionalCycle.setSecondLimit(secondCount);
        emotionalCycle.setThirdLimit(thirdCount);
        emotionalCycle.setMoreLimit(moreCount);
        emotionalCycle.setThirdLimitStr(thirdSb.toString());
        emotionalCycle.setMoreLimitStr(moreSb.toString());

        return lists;
    }

    public boolean isST(String code,String name,String dateStr){
        StockDetail stockDetail = stockDetailService.getDetailByStockCodeAndDate(code,name,dateStr);
        if (stockDetail==null){
            return true;
        }
        float maxRate = stockDetail.getMaxPriceRate().floatValue();
        float minRate = stockDetail.getMinPriceRate().floatValue();
        if (maxRate>9){
            return false;
        }
        if (minRate<-9){
            return false;
        }

        return true;
    }




    /**
     * 解析涨停数据
     * @param date 2022-02-10
     * @param emotionalCycle
     */
    public List<UpLimit> queryUpLimit(EmotionalCycle emotionalCycle,String date) throws IOException {
        return queryUpLimit(emotionalCycle,date,false);
    }

    /**
     * 解析 上涨下跌数据
     */
    public void queryUpDrop(EmotionalCycle emotionalCycle,String dateStr)  {

        String upDropStr = String.format(QUERY_FORMAT_UP_DROP,dateStr,dateStr);
        JSONObject upDropJson = getWencaiJson(upDropStr);
        JSONObject upDropResult = apiService.post(wencaiUrl,upDropJson,getCookieHeaders());

        JSONObject dataJson = upDropResult.getJSONObject("data");
        JSONArray answerArray = dataJson.getJSONArray("answer");
        JSONObject answerJson = answerArray.getJSONObject(0);
        JSONArray txtArray = answerJson.getJSONArray("txt");
        JSONObject txtJson = txtArray.getJSONObject(0);
        JSONObject contentJson = txtJson.getJSONObject("content");
        JSONArray componentsArray = contentJson.getJSONArray("components");
        JSONObject componentJson = componentsArray.getJSONObject(0);
        JSONObject c_dataJson = componentJson.getJSONObject("data");
        JSONObject c_meteJson = c_dataJson.getJSONObject("meta");
        JSONObject extraJson = c_meteJson.getJSONObject("extra");
        String chunks_info_str = extraJson.getString("chunks_info");

        Matcher matcher = upDropCountPattern.matcher(chunks_info_str);
        if (matcher.find()){
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
            emotionalCycle.setUpCount(Integer.valueOf(matcher.group(1)));
            emotionalCycle.setDropCount(Integer.valueOf(matcher.group(2)));
        }
    }


    /**
     * 不统计主题和炸板收益 用于盘中监控炸板
     * @param emotionalCycle
     * @param date
     * @return
     * @throws ParseException
     */
    public List<BrokenLimit> queryBrokenLimitWithoutParse(EmotionalCycle emotionalCycle,String date) throws ParseException {
        return queryBrokenLimit(emotionalCycle,date,false,false,false);
    }

    public List<BrokenLimit> queryBrokenLimit(EmotionalCycle emotionalCycle,String date,boolean isHistory) throws ParseException {
        if (isHistory){
            //历史数据没必要差题材
            return queryBrokenLimit(emotionalCycle,date,false,true,isHistory);
        }
        return queryBrokenLimit(emotionalCycle,date,true,true,isHistory);
    }


        /**
         * 解析炸板数据
         * @param emotionalCycle
         * @param date  2022-02-10
         * @param doesParseTheme 是否解析题材 默认true 解析
         * @param doesParseLoseRate 是否解析亏损率
         * 非当庭数据不准确
         */
    private List<BrokenLimit> queryBrokenLimit(EmotionalCycle emotionalCycle,String date,boolean doesParseTheme,boolean doesParseLoseRate,boolean isHistory) throws ParseException {

        String brokenStr = "";
        if (isHistory){
            brokenStr = String.format(QUERY_FORMAT_BROKEN_HISTORY,date);
        }else{
            brokenStr = String.format(QUERY_FORMAT_BROKEN,date);
        }
        JSONObject brokenJson = getWencaiJson(brokenStr);
        JSONObject brokenResult = apiService.post(wencaiUrl,brokenJson,getCookieHeaders());

        String todayStr = DateUtils.getTodayStr();
        boolean isToday = StringUtils.equals(todayStr,date);
        String dateStr = DateUtils.coverToUnSymbol(date);

        List<UpLimit> lastUpLimitList = Lists.newArrayList();
        Map<String,UpLimit> lastUpLimitNameMap = Maps.newHashMap();

        String lastDay = tradingDayService.getLastTradingDay(date);
        if (!StringUtils.isBlank(lastDay)){
            lastUpLimitList = upLimitMapper.selectByDateStr(DateUtils.coverToUnSymbol(lastDay));
            for (UpLimit upLimit : lastUpLimitList){
                lastUpLimitNameMap.put(upLimit.getName(),upLimit);
            }
        }

        String field_broken_code = FIELD_BROKEN_CODE;
        String field_broken_close_time_min = String.format(FIELD_BROKEN_CLOSE_TIME_MIN_FORMAT,dateStr);
        String field_broken_open_count = String.format(FIELD_BROKEN_OPEN_COUNT_FORMAT,dateStr);
        String field_broken_loss_rate = FIELD_BROKEN_LOSS_RATE;
        String field_broken_name = FIELD_BROKEN_NAME;
        String field_broken_first_time = String.format(FIELD_BROKEN_FIRST_TIME_FORMAT,dateStr);
        String field_broken_last_time = String.format(FIELD_BROKEN_LAST_TIME_FORMAT,dateStr);
        String field_broken_close_price = FIELD_BROKEN_CLOSE_PRICE;

        JSONObject dataJson = brokenResult.getJSONObject("data");
        JSONArray answerArray = dataJson.getJSONArray("answer");
        JSONObject answerJson = answerArray.getJSONObject(0);
        JSONArray txtArray = answerJson.getJSONArray("txt");
        JSONObject txtJson = txtArray.getJSONObject(0);
        JSONObject contentJson = txtJson.getJSONObject("content");
        JSONArray componentsArray = contentJson.getJSONArray("components");
        JSONObject componentJson = componentsArray.getJSONObject(0);
        JSONObject c_dataJson = componentJson.getJSONObject("data");
        JSONArray c_data_datas_array = c_dataJson.getJSONArray("datas");


        List<BrokenLimit> lists = Lists.newArrayList();

        if (c_data_datas_array.size()>0){
            //破板统计
            for (int i=0;i<c_data_datas_array.size();i++){
                JSONObject resultJson = c_data_datas_array.getJSONObject(i);
                BrokenLimit limit = new BrokenLimit();
                limit.setCode(resultJson.getString(field_broken_code));
                limit.setCloseTimeMin(resultJson.getString(field_broken_close_time_min));
                limit.setOpenCount(resultJson.getInteger(field_broken_open_count));
                //亏损率 最新涨跌幅-10
                if (doesParseLoseRate){
                    if (isToday){
                        limit.setLossRate(resultJson.getFloat(field_broken_loss_rate)-10);
                        limit.setClosePrice(resultJson.getFloat(field_broken_close_price));
                    }else{
                        StockDetail stockDetail = stockDetailService.getDetailByStockCodeAndDate(resultJson.getString(field_broken_code),resultJson.getString(field_broken_name),dateStr);
                        if (stockDetail!=null){
                            limit.setLossRate(stockDetail.getClosePriceRate().floatValue()-10);
                            limit.setClosePrice(stockDetail.getClosePrice().floatValue());
                        }
                    }
                }
                if (isHistory && isST(resultJson.getString(field_broken_code),resultJson.getString(field_broken_name),dateStr)){
                    continue;
                }
                String timeDetail = resultJson.getString(field_broken_last_time);
                String[] tdss = timeDetail.split("\\|\\|");
                limit.setLastTime(tdss[tdss.length-1].trim());
                limit.setFirstTime(resultJson.getString(field_broken_first_time).trim());
                limit.setName(resultJson.getString(field_broken_name));
                if (doesParseTheme){
                    limit.setTheme(queryStockTheme(resultJson.getString(field_broken_name)));
                }
                limit.setDate(dateStr);

                //统计上个交易日涨停类型
                if (lastUpLimitList.size()>0){
                    //昨日统计过涨停内容
                    if (lastUpLimitNameMap.containsKey(limit.getName())){
                        limit.setBrokenLimitType(lastUpLimitNameMap.get(limit.getName()).getSequenceCount()+1+"板");
                    }else {
                        limit.setBrokenLimitType("首板");
                    }
                }

                lists.add(limit);
            }
        }
        emotionalCycle.setBrokenLimit(lists.size());
        return lists;
    }

    public List<DropLimit> queryDropLimit(EmotionalCycle emotionalCycle,String date){
        return queryDropLimit(emotionalCycle,date,false);
    }

    public List<DropLimit> queryDropLimit(EmotionalCycle emotionalCycle,String date,boolean isHistory) {

        String dateStr = DateUtils.coverToUnSymbol(date);
        String dietingStr = "";
        if (isHistory){
            dietingStr = String.format(QUERY_FORMAT_DIETING_HISTORY,dateStr);
        }else {
            dietingStr = String.format(QUERY_FORMAT_DIETING,dateStr);
        }

        JSONObject dietingJson = getWencaiJson(dietingStr);
        JSONObject dietingResult = apiService.post(wencaiUrl,dietingJson,getCookieHeaders());

        String field_drop_type = String.format(FIELD_DROP_TYPE_FORMAT,dateStr);
        String field_drop_season = String.format(FIELD_DROP_SEASON_FORMAT,dateStr);
        String field_drop_code = FIELD_DROP_CODE;
        String field_drop_name = FIELD_DROP_NAME;
        String field_drop_sequence_count = String.format(FIELD_DROP_SEQUENCE_COUNT_FORMAT,dateStr);

        JSONObject dataJson = dietingResult.getJSONObject("data");

        JSONArray answerArray = dataJson.getJSONArray("answer");
        JSONObject answerJson = answerArray.getJSONObject(0);
        JSONArray txtArray = answerJson.getJSONArray("txt");
        JSONObject txtJson = txtArray.getJSONObject(0);
        JSONObject contentJson = txtJson.getJSONObject("content");
        JSONArray componentsArray = contentJson.getJSONArray("components");
        JSONObject componentJson = componentsArray.getJSONObject(0);
        JSONObject c_dataJson = componentJson.getJSONObject("data");
        JSONArray c_data_datas_array = c_dataJson.getJSONArray("datas");


        List<DropLimit> lists = Lists.newArrayList();

        if (c_data_datas_array.size()>0){
            //有跌停

            for (int i=0;i<c_data_datas_array.size();i++){
                JSONObject resultJson = c_data_datas_array.getJSONObject(i);
                DropLimit dropLimit = new DropLimit();
                dropLimit.setLimitType(resultJson.getString(field_drop_type));
                dropLimit.setReason(resultJson.getString(field_drop_season));
                String code = resultJson.getString(field_drop_code);
                if (StringUtils.isBlank(code)){
                    continue;
                }
                code = code.substring(0,6);
                //历史st股排除统计
                if (isHistory && isST(code,resultJson.getString(field_drop_name),dateStr)){
                    continue;
                }
                dropLimit.setCode(code);
                dropLimit.setName(resultJson.getString(field_drop_name));
                dropLimit.setSequenceCount(resultJson.getInteger(field_drop_sequence_count));
                dropLimit.setDate(dateStr);
                lists.add(dropLimit);
            }
        }
        emotionalCycle.setDropLimit(lists.size());
        return lists;
    }


    public static void main(String[] args) throws IOException {

        String todayStr = DateUtils.getTodayStr();
        String dateStr = "2022-02-16";

        dateStr = DateUtils.coverToUnSymbol(dateStr);

        String field_business_name = FIELD_BUSINESS_NAME;
        String field_business_rose = String.format(FIELD_BUSINESS_ROSE_FORMAT,dateStr);
        String field_business_limit = String.format(FIELD_BUSINESS_LIMIT_FORMAT,dateStr);


        File file = new File("D:\\ee.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));

        StringBuilder sb = new StringBuilder();
        sb.append(br.readLine());

        JSONObject jsonObject = JSONObject.parseObject(sb.toString());
        JSONObject dataJson = jsonObject.getJSONObject("data");

        JSONArray answerArray = dataJson.getJSONArray("answer");
        JSONObject answerJson = answerArray.getJSONObject(0);
        JSONArray txtArray = answerJson.getJSONArray("txt");
        JSONObject txtJson = txtArray.getJSONObject(0);
        JSONObject contentJson = txtJson.getJSONObject("content");
        JSONArray componentsArray = contentJson.getJSONArray("components");
        JSONObject componentJson = componentsArray.getJSONObject(0);
        JSONObject c_dataJson = componentJson.getJSONObject("data");
        JSONArray c_data_datas_array = c_dataJson.getJSONArray("datas");

        int count = 0;
        StringBuilder resultSb = new StringBuilder();
        //TODO 暂时取前5,具体逻辑没想好
        for (int i=0;i<c_data_datas_array.size();i++){
            if (count<5){
                JSONObject resultJson = c_data_datas_array.getJSONObject(i);
                String name = resultJson.getString(field_business_name);
                float rose = DecimalUtils.roundValue(resultJson.getFloat(field_business_rose));
                String limits = resultJson.getString(field_business_limit);
                resultSb.append(name).append("[").append(limits).append(",").append(rose).append("%]").append("、");
                count++;
            }else{
                break;
            }
        }

    }

}
