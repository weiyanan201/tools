package wei.tools.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wei.tools.dao.DropLimitMapper;
import wei.tools.dao.UpLimitMapper;
import wei.tools.entity.WenCaiQueryEntity;
import wei.tools.model.DropLimit;
import wei.tools.model.UpLimit;
import wei.tools.util.DateUtils;

import java.io.*;
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



    public static String QUERY_FORMAT_ZHANGTING_FENXI= "%s涨停;非st;非新股；%s连续涨停天数>=1；上市时间超过50天";
    public static String QUERY_FORMAT_UP_DROP = "%s上涨个股；%s下跌个股；";
    public static String QUERY_FORMAT_DIETING = "%s 跌停;非st；";

    private static String FIELD_LIMIT_TYPE_FORMAT = "涨停类型[%s]";
    private static String FIELD_LIMIT_REASON_FORMAT = "涨停原因类别[%s]";
    private static String FIELD_CODE = "股票代码";
    private static String FIELD_SEQUENCE_TYPE_FORMAT = "几天几板[%s]";
    private static String FIELD_SEQUENCE_COUNT_FORMAT = "连续涨停天数[%s]";
    private static String FIELD_NAME = "股票简称";
    private static String FIELD_LAST_TIME_FORMAT = "最终涨停时间[%s]";
    private static String FIELD_OPEN_COUNT_FORMAT = "涨停开板次数[%s]";

    private static String FIELD_DROP_TYPE_FORMAT = "跌停类型[%s]";
    private static String FIELD_DROP_SEASON_FORMAT = "跌停原因类型[%s]";
    private static String FIELD_DROP_CODE = "股票代码";
    private static String FIELD_DROP_NAME = "股票简称";
    private static String FIELD_DROP_SEQUENCE_COUNT_FORMAT = "连续跌停天数[%s]";

    private static Pattern upDropCountPattern = Pattern.compile("\\((\\d+)+\\).*\\((\\d+)+\\)");

    @Autowired
    private ApiService apiService;
    @Autowired
    private UpLimitMapper upLimitMapper;
    @Autowired
    private DropLimitMapper dropLimitMapper;

    public void queryByDate(String dateStr) throws IOException {

        WenCaiQueryEntity entity = new WenCaiQueryEntity();
//        //统计涨停信息
        String zhangtingStr = String.format(QUERY_FORMAT_ZHANGTING_FENXI,dateStr,dateStr);
        JSONObject zhangtingJson = getWencaiJson(zhangtingStr);
        JSONObject zhangtingResult = apiService.post(wencaiUrl,zhangtingJson);
        parseZhangtingResult(zhangtingResult,entity,dateStr);

//        //统计上涨下跌
        String upDropStr = String.format(QUERY_FORMAT_UP_DROP,dateStr,dateStr);
        JSONObject upDropJson = getWencaiJson(upDropStr);
        JSONObject upDropResult = apiService.post(wencaiUrl,upDropJson);
        pareUpDropResult(upDropResult,entity);

        //跌停跌停
        String dietingStr = String.format(QUERY_FORMAT_DIETING,dateStr);
        JSONObject dietingJson = getWencaiJson(dietingStr);
        JSONObject dietingResult = apiService.post(wencaiUrl,dietingJson);
        pareDieTingResult(dietingResult,entity,dateStr);

        //炸板统计


        System.out.println(entity);
    }

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
     * 解析涨停数据
     * @param zhangtingResult
     * @param entity
     */
    private void parseZhangtingResult(JSONObject zhangtingResult, WenCaiQueryEntity entity,String dateStr) throws IOException {
        //2022-02-07 -> 20220207
//        String dateStr = "2022-02-07";
        dateStr = DateUtils.coverToUnSymbol(dateStr);

        String field_limit_type = String.format(FIELD_LIMIT_TYPE_FORMAT,dateStr);
        String field_limit_reason = String.format(FIELD_LIMIT_REASON_FORMAT,dateStr);
        String field_code = FIELD_CODE;
        String field_sequence_type = String.format(FIELD_SEQUENCE_TYPE_FORMAT,dateStr);
        String field_sequence_count = String.format(FIELD_SEQUENCE_COUNT_FORMAT,dateStr);
        String field_name = FIELD_NAME;
        String field_last_time = String.format(FIELD_LAST_TIME_FORMAT,dateStr);
        String field_open_count = String.format(FIELD_OPEN_COUNT_FORMAT,dateStr);

        List<UpLimit> lists = Lists.newArrayList();

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
            upLimit.setCode(js.getString(field_code));
            upLimit.setSequenceType(js.getString(field_sequence_type));
            upLimit.setSequenceCount(js.getInteger(field_sequence_count));
            upLimit.setName(js.getString(field_name));
            upLimit.setLastTime(js.getString(field_last_time));
            upLimit.setOpenCount(js.getInteger(field_open_count));
            lists.add(upLimit);
        }
        if (!lists.isEmpty()){
            upLimitMapper.batchInsertOrUpdate(lists);
        }

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

        entity.setUpLimitCount(totalCount);
        entity.setFirstCount(firstCount);
        entity.setSecondCount(secondCount);
        entity.setThirdCount(thirdCount);
        entity.setMoreCount(moreCount);
        entity.setThirdStr(thirdSb.toString());
        entity.setMoreStr(moreSb.toString());

    }

    /**
     * 解析 上涨下跌数据
     */
    private void pareUpDropResult(JSONObject upDropResult,WenCaiQueryEntity entity){

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
            entity.setUpCount(Integer.valueOf(matcher.group(1)));
            entity.setDropCount(Integer.valueOf(matcher.group(2)));
        }
    }

    /**
     * 解析炸板数据
     * @param breakResult
     * @param entity
     * 非当庭数据不准确
     */
    private void pareBreakResult(JSONObject breakResult,WenCaiQueryEntity entity){


    }

    /**
     * 解析跌停数据
     * @param dietingResult
     * @param entity
     * @param dateStr
     */
    private void pareDieTingResult(JSONObject dietingResult, WenCaiQueryEntity entity,String dateStr){

        dateStr = DateUtils.coverToUnSymbol(dateStr);

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

        entity.setDropLimitCount(c_data_datas_array.size());

        if (c_data_datas_array.size()>0){
            //有跌停
            List<DropLimit> lists = Lists.newArrayList();
            for (int i=0;i<c_data_datas_array.size();i++){
                JSONObject resultJson = c_data_datas_array.getJSONObject(i);
                DropLimit dropLimit = new DropLimit();
                dropLimit.setLimitType(resultJson.getString(field_drop_type));
                dropLimit.setReason(resultJson.getString(field_drop_season));
                dropLimit.setCode(resultJson.getString(field_drop_code));
                dropLimit.setName(resultJson.getString(field_drop_name));
                dropLimit.setSequenceCount(resultJson.getInteger(field_drop_sequence_count));
                dropLimit.setDate(dateStr);
                lists.add(dropLimit);
            }
            dropLimitMapper.batchInsertOrUpdate(lists);
        }
    }

    public static void main(String[] args) throws IOException {

        String todayStr = DateUtils.getTodayStr();
        String dateStr = "2022-01-04";
        boolean isToday = StringUtils.equals(todayStr,dateStr);

        dateStr = DateUtils.coverToUnSymbol(dateStr);

        String field_drop_type = String.format(FIELD_DROP_TYPE_FORMAT,dateStr);
        String field_drop_season = String.format(FIELD_DROP_SEASON_FORMAT,dateStr);
        String field_drop_code = FIELD_DROP_CODE;
        String field_drop_name = FIELD_DROP_NAME;
        String field_drop_sequence_count = String.format(FIELD_DROP_SEQUENCE_COUNT_FORMAT,dateStr);

        File file = new File("D:\\dd.txt");
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

        if (c_data_datas_array.size()>0){
            //有跌停
            List<DropLimit> lists = Lists.newArrayList();
            for (int i=0;i<c_data_datas_array.size();i++){
                JSONObject resultJson = c_data_datas_array.getJSONObject(i);
                DropLimit dropLimit = new DropLimit();
                dropLimit.setLimitType(resultJson.getString(field_drop_type));
                dropLimit.setReason(resultJson.getString(field_drop_season));
                dropLimit.setCode(resultJson.getString(field_drop_code));
                dropLimit.setName(resultJson.getString(field_drop_name));
                dropLimit.setSequenceCount(resultJson.getInteger(field_drop_sequence_count));
                dropLimit.setDate(dateStr);
                lists.add(dropLimit);
            }
            for (DropLimit limit : lists){
                System.out.println(limit);
            }
        }else{
            //无跌停
        }

        System.out.println("xxx");
    }

}
