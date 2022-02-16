package wei.tools.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wei.tools.dao.*;
import wei.tools.exception.ToolsException;
import wei.tools.model.*;
import wei.tools.util.DateUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 股票复盘
 * @Author: weiyanan
 * @Date: 2022/2/16 17:31
 */
@Service
public class StockReviewService {

    @Autowired
    private WenCaiService wenCaiService;
    @Autowired
    private StockDetailService stockDetailService;
    @Autowired
    private TradingDayService tradingDayService;
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

    /**
     * 每日复盘
     * 爬数据、计算、预测、推荐
     * @param dateStr 2022-02-10
     */
    public void reviewByDate(String dateStr) throws IOException, ParseException {
        DateUtils.checkFormat(dateStr);
        String dateUnSymbolStr = DateUtils.coverToUnSymbol(dateStr);

        if (!tradingDayService.isTradingDay(dateStr)){
            throw new ToolsException("查询日期为非交易日!");
        }
        //step1 情绪周期表统计
        EmotionalCycle emotionalCycle = new EmotionalCycle(dateStr);
        //统计涨停信息
        List<UpLimit> upLimits = wenCaiService.queryUpLimit(emotionalCycle,dateStr);
        //统计上涨下跌
        wenCaiService.queryUpDrop(emotionalCycle,dateStr);
        //跌停
        List<DropLimit> dropLimits = wenCaiService.queryDropLimit(emotionalCycle,dateStr);
        //炸板统计
        List<BrokenLimit> brokenLimits = wenCaiService.queryBrokenLimit(emotionalCycle,dateStr,true,true);
        //查询板块
        wenCaiService.queryHotBusiness(emotionalCycle,dateStr);
        //结果写入excel
        wenCaiService.writeResult(emotionalCycle,dateStr);
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
        String lastTradingUnSymbol = DateUtils.coverToUnSymbol(lastTradingDay);

        Map<String,String> codeNameMap = Maps.newHashMap();
        List<String> lastDayCodes = Lists.newArrayList();

        List<UpLimit> lastUpLimits = upLimitMapper.selectByDateStr(lastTradingUnSymbol);
        for (UpLimit upLimit : lastUpLimits){
            lastDayCodes.add(upLimit.getCode());
            codeNameMap.put(upLimit.getCode(),upLimit.getName());
        }
        List<BrokenLimit> lastBrokenLimits = brokenLimitMapper.selectByDateStr(lastTradingUnSymbol);
        for (BrokenLimit brokenLimit : lastBrokenLimits){
            lastDayCodes.add(brokenLimit.getCode());
            codeNameMap.put(brokenLimit.getCode(),brokenLimit.getName());
        }
        List<StockDetail> stockDetails = stockDetailService.getDetailsByDate(lastDayCodes,codeNameMap,dateUnSymbolStr);
        stockDetailMapper.batchInsertOrUpdate(stockDetails);

    }

    public void openingQuery(String dateStr) throws IOException, ParseException {
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
        wenCaiService.queryBrokenLimit(emotionalCycle,dateStr,false,false);
        //热门板块
        wenCaiService.queryHotBusiness(emotionalCycle,dateStr);

        System.out.println(emotionalCycle.openQueryToString());
    }

    public static void main(String[] args) {
        String code = "002068.SZ";
        System.out.println(code.substring(0,6));
    }
}
