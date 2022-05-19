package wei.tools.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wei.tools.dao.*;
import wei.tools.exception.ToolsException;
import wei.tools.model.*;
import wei.tools.util.DateUtils;
import wei.tools.util.DecimalUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 股票复盘
 * @Author: weiyanan
 * @Date: 2022/2/16 17:31
 */
@Service
public class StockReviewService {

    private static Logger logger = LoggerFactory.getLogger(StockReviewService.class);

    @Value("${review.tradingLogPath}")
    private String tradingLogPath ;
    @Value("${review.openingLoopIntervalSec}")
    private int openingLoopIntervalSec;

    @Autowired
    private WenCaiService wenCaiService;
    @Autowired
    private StockDetailService stockDetailService;
    @Autowired
    private TradingDayService tradingDayService;
    @Autowired
    private ExcelService excelService;
    @Autowired
    private UpLimitMapper upLimitMapper;
    @Autowired
    private DropLimitMapper dropLimitMapper;
    @Autowired
    private BrokenLimitMapper brokenLimitMapper;
    @Autowired
    private EmotionalCycleMapper emotionalCycleMapper;
    @Autowired
    private StockDetailMapper stockDetailMapper;

    public void reviewByDate() throws IOException, ParseException {
        String dateStr = DateUtils.getTodayStr();
        reviewByDate(dateStr,false);
    }

    public void reviewByDate(String dateStr) throws IOException, ParseException {
        reviewByDate(dateStr,false);
    }
    /**
     * 每日复盘
     * 爬数据、计算、预测、推荐
     * @param dateStr 2022-02-10
     */
    public void reviewByDate(String dateStr,boolean isHistory) throws IOException, ParseException {
        DateUtils.checkFormat(dateStr);
        String dateUnSymbolStr = DateUtils.coverToUnSymbol(dateStr);

        if (!tradingDayService.isTradingDay(dateStr)){
            throw new ToolsException("查询日期为非交易日!");
        }
        //step1 情绪周期表统计
        EmotionalCycle emotionalCycle = new EmotionalCycle(dateStr);
        //统计涨停信息
        List<UpLimit> upLimits = wenCaiService.queryUpLimit(emotionalCycle,dateStr,isHistory);
        //统计上涨下跌
        wenCaiService.queryUpDrop(emotionalCycle,dateStr);
        //跌停
        List<DropLimit> dropLimits = wenCaiService.queryDropLimit(emotionalCycle,dateStr,isHistory);
        //炸板统计
        List<BrokenLimit> brokenLimits = wenCaiService.queryBrokenLimit(emotionalCycle,dateStr,isHistory);
        //查询板块
        wenCaiService.queryHotBusiness(emotionalCycle,dateStr);

        //昨日涨停等指标统计
        wenCaiService.queryEarningRate(emotionalCycle,dateStr);

        //结果写入excel
        excelService.writeEmotionCycleResult(emotionalCycle,dateStr);

        //写入mysql
        if (upLimits.size()>0){
            upLimitMapper.batchInsertOrUpdate(upLimits);
        }
        if (dropLimits.size()>0){
            dropLimitMapper.batchInsertOrUpdate(dropLimits);
        }
        if (brokenLimits.size()>0){
            brokenLimitMapper.batchInsertOrUpdate(brokenLimits);
        }
        emotionalCycleMapper.insertOrUpdate(emotionalCycle);

        //step2 爬上个交易日涨停、炸板个股当日的股价详情

        String lastTradingDay = tradingDayService.getLastTradingDay(dateStr);
        //防止没有历史数据而报错
        if (!StringUtils.isBlank(lastTradingDay)){
            List<StockDetail> stockDetails = crawlerStockDetails(dateStr,lastTradingDay);
            if (stockDetails.size()>0){
                stockDetailMapper.batchInsertOrUpdate(stockDetails);
            }
        }

        logger.info("-----------------reviewByDate : {} 已完成--------------------------",dateStr);


    }

    /**
     * 涨停、炸板的股价详情
     * @param lastDateStr 个股范围挑选日
     * @param dateStr 该日这些个股的股价
     * @return
     */
    private List<StockDetail> crawlerStockDetails(String dateStr,String lastDateStr){
        //
        String lastDateStrUnSymbol = DateUtils.coverToUnSymbol(lastDateStr);
        String dateStrUnSymbol = DateUtils.coverToUnSymbol(dateStr);

        List<String> lastDayCodes = Lists.newArrayList();
        Map<String,String> codeNameMap = Maps.newHashMap();

        List<UpLimit> lastUpLimits = upLimitMapper.selectByDateStr(lastDateStrUnSymbol);
        for (UpLimit upLimit : lastUpLimits){
            lastDayCodes.add(upLimit.getCode());
            codeNameMap.put(upLimit.getCode(),upLimit.getName());
        }
        List<BrokenLimit> lastBrokenLimits = brokenLimitMapper.selectByDateStr(lastDateStrUnSymbol);
        for (BrokenLimit brokenLimit : lastBrokenLimits){
            lastDayCodes.add(brokenLimit.getCode());
            codeNameMap.put(brokenLimit.getCode(),brokenLimit.getName());
        }
        return stockDetailService.getDetailsByDate(lastDayCodes,codeNameMap,dateStrUnSymbol);
    }

    public void fixupCrawlerStockDetails(String dateStr,String lastDateStr){
        List<StockDetail> stockDetails = crawlerStockDetails(dateStr,lastDateStr);
        if (stockDetails.size()>0){
            stockDetailMapper.batchInsertOrUpdate(stockDetails);
        }
    }

    /**
     * 盘中情绪监控
     * @param dateStr
     * @throws IOException
     * @throws ParseException
     */
    public EmotionalCycle openingQuery(String dateStr) throws IOException, ParseException {
        DateUtils.checkFormat(dateStr);
        if (!tradingDayService.isTradingDay(dateStr)){
            throw new ToolsException("查询日期为非交易日!");
        }

        EmotionalCycle emotionalCycle = new EmotionalCycle(dateStr);
        //统计涨停信息
        wenCaiService.queryUpLimit(emotionalCycle,dateStr);
        //统计上涨下跌
        wenCaiService.queryUpDrop(emotionalCycle,dateStr);
        //跌停
        wenCaiService.queryDropLimit(emotionalCycle,dateStr);
        //炸板统计
        wenCaiService.queryBrokenLimitWithoutParse(emotionalCycle,dateStr);
        //热门板块
        wenCaiService.queryHotBusiness(emotionalCycle,dateStr);

        System.out.println(emotionalCycle.openQueryToString());
        return emotionalCycle;
    }

    public void openLoopQuery() throws IOException, ParseException, InterruptedException {
        String dateStr = DateUtils.getTodayStr();
        openLoopQuery(dateStr);
    }
    /**
     * 盘中循环监控情绪
     */
    public void openLoopQuery(String dateStr) throws IOException, ParseException, InterruptedException {

        DateUtils.checkFormat(dateStr);
        if (!tradingDayService.isTradingDay(dateStr)){
            throw new ToolsException("查询日期为非交易日!");
        }
        File file = new File(tradingLogPath);
        if (!file.exists()){
            file.createNewFile();
        }

        while(true){
            if (isOpening()){
                try{
                    FileWriter fileWriter = new FileWriter(file,true);
                    //TODO
//                dateStr = "2022-02-23";
                    EmotionalCycle emotionalCycle = openingQuery(dateStr);
                    StringBuilder sb = new StringBuilder();

                    sb.append(DateUtils.getNowDateTime())
                            .append(" ")
                            .append(emotionalCycle.openQueryToString())
                            .append("\n");
                    fileWriter.write(sb.toString());
                    fileWriter.flush();
                }catch (Exception exception){
                    logger.error("openLoopQuery is error : ",exception);
                }

            }
            Thread.sleep(openingLoopIntervalSec*1000L);
        }

    }

    public static boolean isOpening() throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String todayStr = format.format(now);
        Date todayDate = format.parse(todayStr);


        Calendar amOpenCal = Calendar.getInstance();
        amOpenCal.setTime(todayDate);
        amOpenCal.set(Calendar.HOUR,9);
        amOpenCal.set(Calendar.MINUTE,30);

        Calendar amCloseCal = Calendar.getInstance();
        amCloseCal.setTime(todayDate);
        amCloseCal.set(Calendar.HOUR,11);
        amCloseCal.set(Calendar.MINUTE,30);

        Calendar pmOpenCal = Calendar.getInstance();
        pmOpenCal.setTime(todayDate);
        pmOpenCal.set(Calendar.HOUR,13);
        pmOpenCal.set(Calendar.MINUTE,00);

        Calendar pmCloseCal = Calendar.getInstance();
        pmCloseCal.setTime(todayDate);
        pmCloseCal.set(Calendar.HOUR,15);
        pmCloseCal.set(Calendar.MINUTE,00);

        if ((now.getTime()>=amOpenCal.getTime().getTime() && now.getTime()<=amCloseCal.getTime().getTime())
                || (now.getTime()>=pmOpenCal.getTime().getTime() && now.getTime()<pmCloseCal.getTime().getTime())){
            return true;
        }

        return false;
    }

    public static void main(String[] args) throws IOException, ParseException {
        System.out.println(DateUtils.getNowDateTime());

    }
}
