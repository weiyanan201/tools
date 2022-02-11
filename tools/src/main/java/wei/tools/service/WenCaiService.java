package wei.tools.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wei.tools.dao.BrokenLimitMapper;
import wei.tools.dao.DropLimitMapper;
import wei.tools.dao.UpLimitMapper;
import wei.tools.entity.WenCaiQueryEntity;
import wei.tools.exception.ToolsException;
import wei.tools.model.BrokenLimit;
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
    public static String QUERY_FORMAT_BROKEN = "%s炸板；非st";

    //涨停字段
    private static String FIELD_LIMIT_TYPE_FORMAT = "涨停类型[%s]";
    private static String FIELD_LIMIT_REASON_FORMAT = "涨停原因类别[%s]";
    private static String FIELD_CODE = "股票代码";
    private static String FIELD_SEQUENCE_TYPE_FORMAT = "几天几板[%s]";
    private static String FIELD_SEQUENCE_COUNT_FORMAT = "连续涨停天数[%s]";
    private static String FIELD_NAME = "股票简称";
    private static String FIELD_LAST_TIME_FORMAT = "最终涨停时间[%s]";
    private static String FIELD_OPEN_COUNT_FORMAT = "涨停开板次数[%s]";

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

    //上涨下跌提取
    private static Pattern upDropCountPattern = Pattern.compile("\\((\\d+)+\\).*\\((\\d+)+\\)");

    @Value("${review.filePath}")
    private String excelFilePath ;

    @Autowired
    private ApiService apiService;
    @Autowired
    private StockService stockService;
    @Autowired
    private UpLimitMapper upLimitMapper;
    @Autowired
    private DropLimitMapper dropLimitMapper;
    @Autowired
    private BrokenLimitMapper brokenLimitMapper;
    @Autowired
    private TradingDayService tradingDayService;

    public void reviewByDate(String dateStr) throws IOException {

        DateUtils.checkFormat(dateStr);
        if (!tradingDayService.isTradingDay(dateStr)){
            throw new ToolsException("查询日期为非交易日!");
        }

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
        String brokenStr = String.format(QUERY_FORMAT_BROKEN,dateStr);
        JSONObject brokenJson = getWencaiJson(brokenStr);
        JSONObject brokenResult = apiService.post(wencaiUrl,brokenJson);
        parseBreakResult(brokenResult,entity,dateStr);

        //结果写入excel
        writeResult(entity,dateStr);

        System.out.println(entity);
    }


    /**
     * 获取题材
     * @param stockName
     * @return
     */
    public String queryStockTheme(String stockName){
        JSONObject paramJson = getWencaiJson(stockName);
        JSONObject resultJson = apiService.post(wencaiUrl,paramJson);

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
    }

    /**
     * 问财接口参数格式
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
    private void parseBreakResult(JSONObject breakResult,WenCaiQueryEntity entity,String dateStr){
        String todayStr = DateUtils.getTodayStr();
        boolean isToday = StringUtils.equals(todayStr,dateStr);

        dateStr = DateUtils.coverToUnSymbol(dateStr);

        String field_broken_code = FIELD_BROKEN_CODE;
        String field_broken_close_time_min = String.format(FIELD_BROKEN_CLOSE_TIME_MIN_FORMAT,dateStr);
        String field_broken_open_count = String.format(FIELD_BROKEN_OPEN_COUNT_FORMAT,dateStr);
        String field_broken_loss_rate = FIELD_BROKEN_LOSS_RATE;
        String field_broken_name = FIELD_BROKEN_NAME;
        String field_broken_first_time = String.format(FIELD_BROKEN_FIRST_TIME_FORMAT,dateStr);
        String field_broken_last_time = String.format(FIELD_BROKEN_LAST_TIME_FORMAT,dateStr);

        JSONObject dataJson = breakResult.getJSONObject("data");
        JSONArray answerArray = dataJson.getJSONArray("answer");
        JSONObject answerJson = answerArray.getJSONObject(0);
        JSONArray txtArray = answerJson.getJSONArray("txt");
        JSONObject txtJson = txtArray.getJSONObject(0);
        JSONObject contentJson = txtJson.getJSONObject("content");
        JSONArray componentsArray = contentJson.getJSONArray("components");
        JSONObject componentJson = componentsArray.getJSONObject(0);
        JSONObject c_dataJson = componentJson.getJSONObject("data");
        JSONArray c_data_datas_array = c_dataJson.getJSONArray("datas");
        entity.setBreakCount(c_data_datas_array.size());

        if (c_data_datas_array.size()>0){
            //破板统计
            List<BrokenLimit> lists = Lists.newArrayList();
            for (int i=0;i<c_data_datas_array.size();i++){
                JSONObject resultJson = c_data_datas_array.getJSONObject(i);
                BrokenLimit limit = new BrokenLimit();
                limit.setCode(resultJson.getString(field_broken_code));
                limit.setCloseTimeMin(resultJson.getString(field_broken_close_time_min));
                limit.setOpenCount(resultJson.getInteger(field_broken_open_count));
                //亏损率 最新涨跌幅-10
                if (isToday){
                    limit.setLossRate(resultJson.getFloat(field_broken_loss_rate)-10);
                }else{
                    limit.setLossRate(stockService.getPriceRateByStockCodeAndDate(resultJson.getString(field_broken_code),dateStr)-10);
                }

                String timeDetail = resultJson.getString(field_broken_last_time);
                String[] tdss = timeDetail.split("\\|\\|");
                limit.setLastTime(tdss[tdss.length-1].trim());
                limit.setFirstTime(resultJson.getString(field_broken_first_time).trim());
                limit.setName(resultJson.getString(field_broken_name));
                limit.setTheme(queryStockTheme(resultJson.getString(field_broken_name)));
                limit.setDate(dateStr);
                lists.add(limit);
            }

            brokenLimitMapper.batchInsertOrUpdate(lists);

        }else{

        }

        System.out.println("xxx");

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

    /**
     * 结果写入excel
     */
    public void writeResult(WenCaiQueryEntity entity,String dateStr) throws IOException {

        String[] dss = dateStr.split("-");
        String dateExStr = dss[1]+"."+dss[2];

        InputStream in  = new FileInputStream(new File(excelFilePath));
        XSSFWorkbook wb = new XSSFWorkbook(in);
        FormulaEvaluator formulaEvaluator = new XSSFFormulaEvaluator(wb);

        Sheet sheet = wb.getSheet("情绪周期表");

        //先找要插入的位置
        boolean isNewRow = true;
        int insertIndex = 0;
        int insertNum = 0;
        for (int i=2;i<sheet.getLastRowNum();i++){
            //取第一列 比较日期
            Cell cell = sheet.getRow(i).getCell(0);
            if(cell==null || StringUtils.isBlank(cell.getStringCellValue())){
                continue;
            }
            if (StringUtils.compare(dateExStr,cell.getStringCellValue())<=0){
                if (StringUtils.compare(dateExStr,cell.getStringCellValue())==0){
                    //该日期已有数据，覆盖
                    isNewRow = false;
                }
                insertIndex = i;
                break;
            }
        }

        Row insertRow ;

        if (isNewRow){
            if (insertIndex==0){
                //不需要插入，追加数据
                insertNum = sheet.getLastRowNum()+2;
                insertRow = sheet.createRow(sheet.getLastRowNum()+1);
            }else{
                insertNum=insertIndex+1;
                //插入，后移原数据再插入
                sheet.shiftRows(insertIndex, sheet.getLastRowNum(), 1);
                //poi shiftRows bug
                if (sheet instanceof XSSFSheet) {
                    XSSFSheet xSSFSheet = (XSSFSheet) sheet;
                    for (int r = xSSFSheet.getFirstRowNum(); r < sheet.getLastRowNum() + 1; r++) {
                        XSSFRow row = xSSFSheet.getRow(r);
                        if (row != null) {
                            long rRef = row.getCTRow().getR();
                            for (Cell cell : row) {
                                String cRef = ((XSSFCell) cell).getCTCell().getR();
                                ((XSSFCell) cell).getCTCell().setR(cRef.replaceAll("[0-9]", "") + rRef);
                            }
                        }
                    }
                }
                insertRow = sheet.createRow(insertIndex);
            }
        }else{
            insertRow = sheet.getRow(insertIndex);
            insertNum = insertIndex+1;
        }

        //写入数据
        short height = 650;
        insertRow.setHeight(height);
        //日期
        Cell cell1 = insertRow.createCell(0);
        cell1.setCellStyle(sheet.getColumnStyle(0));
        cell1.setCellValue(dateExStr);
        //红盘
        Cell cell2 = insertRow.createCell(1);
        cell2.setCellStyle(sheet.getColumnStyle(1));
        cell2.setCellValue(entity.getUpCount());
        //绿盘
        Cell cell3 = insertRow.createCell(2);
        cell3.setCellStyle(sheet.getColumnStyle(2));
        cell3.setCellValue(entity.getDropCount());
        //涨停
        Cell cell4 = insertRow.createCell(3);
        cell4.setCellStyle(sheet.getColumnStyle(1));
        cell4.setCellValue(entity.getUpLimitCount());
        //跌停
        Cell cell5 = insertRow.createCell(4);
        cell5.setCellStyle(sheet.getColumnStyle(4));
        cell5.setCellValue(entity.getDropLimitCount());
        //炸板
        Cell cell6 = insertRow.createCell(5);
        cell6.setCellStyle(sheet.getColumnStyle(5));
        cell6.setCellValue(entity.getBreakCount());
        //cell7 炸板率excel自己计算
        Cell cell7 = insertRow.createCell(6);
        cell7.setCellStyle(sheet.getColumnStyle(6));
        String cell7Format = "F%d/(D%d+F%d)";
        cell7.setCellFormula(String.format(cell7Format,insertNum,insertNum,insertNum));
        //cell8 首板excel自己计算
        Cell cell8 = insertRow.createCell(7);
        cell8.setCellStyle(sheet.getColumnStyle(7));
        String cell8Format = "D%d-I%d-J%d-L%d";
        cell8.setCellFormula(String.format(cell8Format,insertNum,insertNum,insertNum,insertNum));
        //2板
        Cell cell9 = insertRow.createCell(8);
        cell9.setCellStyle(sheet.getColumnStyle(8));
        cell9.setCellValue(entity.getSecondCount());
        //3板个数
        Cell cell10 = insertRow.createCell(9);
        cell10.setCellStyle(sheet.getColumnStyle(9));
        cell10.setCellValue(entity.getThirdCount());
        //3板详情
        Cell cell11 = insertRow.createCell(10);
        cell11.setCellStyle(sheet.getColumnStyle(10));
        cell11.setCellValue(entity.getThirdStr());
        //3板以上
        Cell cell12 = insertRow.createCell(11);
        cell12.setCellStyle(sheet.getColumnStyle(11));
        cell12.setCellValue(entity.getMoreCount());
        //3板以上详情
        Cell cell13 = insertRow.createCell(12);
        cell13.setCellStyle(sheet.getColumnStyle(12));
        cell13.setCellValue(entity.getMoreStr());

        formulaEvaluator.evaluateAll();

        FileOutputStream out = new FileOutputStream(excelFilePath);
        wb.write(out);

    }

    public static void main(String[] args) throws IOException {

        String todayStr = DateUtils.getTodayStr();
        String dateStr = "2022-02-10";
        boolean isToday = StringUtils.equals(todayStr,dateStr);

        dateStr = DateUtils.coverToUnSymbol(dateStr);

        String field_broken_code = FIELD_BROKEN_CODE;
        String field_broken_close_time_min = String.format(FIELD_BROKEN_CLOSE_TIME_MIN_FORMAT,dateStr);
        String field_broken_open_count = String.format(FIELD_BROKEN_OPEN_COUNT_FORMAT,dateStr);
        String field_broken_loss_rate = FIELD_BROKEN_LOSS_RATE;
        String field_broken_name = FIELD_BROKEN_NAME;
        String field_broken_first_time = String.format(FIELD_BROKEN_FIRST_TIME_FORMAT,dateStr);
        String field_broken_last_time = String.format(FIELD_BROKEN_LAST_TIME_FORMAT,dateStr);

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
            //破板统计
            List<BrokenLimit> lists = Lists.newArrayList();
            for (int i=0;i<c_data_datas_array.size();i++){
                JSONObject resultJson = c_data_datas_array.getJSONObject(i);
                BrokenLimit limit = new BrokenLimit();
                limit.setCode(resultJson.getString(field_broken_code));
                limit.setCloseTimeMin(resultJson.getString(field_broken_close_time_min));
                limit.setOpenCount(resultJson.getInteger(field_broken_open_count));
                //亏损率 最新涨跌幅-10
                limit.setLossRate(resultJson.getFloat(field_broken_loss_rate)-10);
                String timeDetail = resultJson.getString(field_broken_last_time);
                String[] tdss = timeDetail.split("\\|\\|");
                limit.setLastTime(tdss[tdss.length-1].trim());
                limit.setFirstTime(resultJson.getString(field_broken_first_time).trim());
                limit.setName(resultJson.getString(field_broken_name));
                //TODO 主题抓取
                limit.setTheme("");
                limit.setDate(dateStr);

                lists.add(limit);
            }

            for (BrokenLimit limit : lists){
                System.out.println(limit);
            }

        }else{
            //无跌停
        }

        System.out.println("xxx");
    }

}
