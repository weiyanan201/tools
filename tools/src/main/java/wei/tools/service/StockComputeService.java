package wei.tools.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wei.tools.dao.BrokenRateMapper;
import wei.tools.dao.LimitEarningsMapper;
import wei.tools.dao.TimeSlotHoldRateMapper;
import wei.tools.model.BrokenRate;
import wei.tools.model.LimitEarnings;
import wei.tools.model.TimeSlotHoldRate;
import wei.tools.util.DateUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 股票统计
 * @Author: weiyanan
 * @Date: 2022/2/18 16:32
 */
@Service
public class StockComputeService {

    @Autowired
    private LimitEarningsMapper limitEarningsMapper;
    @Autowired
    private BrokenRateMapper brokenRateMapper;
    @Autowired
    private ExcelService excelService;
    @Autowired
    private TradingDayService tradingDayService;
    @Autowired
    private TimeSlotHoldRateMapper timeSlotHoldRateMapper;


    /**
     * 统计涨停（炸板）次日收益
     * @param lastTradingDay
     * @param tradingDay
     */
    public void computeLimitEarnings(String lastTradingDay,String tradingDay) throws IOException {
        String lastTradingDateUnSymbolStr = DateUtils.coverToUnSymbol(lastTradingDay);
        String dateUnSymbolStr = DateUtils.coverToUnSymbol(tradingDay);
        List<LimitEarnings> limits = Lists.newArrayList();
        List<LimitEarnings> brokens = Lists.newArrayList();
        limits = limitEarningsMapper.countLimitEarnings(lastTradingDateUnSymbolStr,dateUnSymbolStr);
        brokens = limitEarningsMapper.countBrokenEarnings(lastTradingDateUnSymbolStr,dateUnSymbolStr);

        if (limits.size()>0){
            limitEarningsMapper.batchInsertOrUpdate(limits);
        }
        if (brokens.size()>0){
            limitEarningsMapper.batchInsertOrUpdate(brokens);
        }
        excelService.writeUpLimitEarningResult(limits,brokens,tradingDay);
    }

    /**
     * 统计炸板率
     * @param tradingDay
     */
    public void computeBrokenRate(String tradingDay) throws IOException {
        String dateUnSymbolStr = DateUtils.coverToUnSymbol(tradingDay);
        BrokenRate brokenRate = brokenRateMapper.queryByDate(dateUnSymbolStr);
        brokenRateMapper.insert(brokenRate);
        excelService.writeBrokenRateResult(brokenRate,tradingDay);
    }

    /**
     * 统计上一个交易日首板各时间段封板率及收益
     * @param tradingDay  yyyy-MM-dd
     */
    public void computeTimeSlotHoldRateForFirst(String tradingDay) throws ParseException {
        String lastTradingDay = tradingDayService.getLastTradingDay(tradingDay);
        String tradingDateUnSym = DateUtils.coverToUnSymbol(tradingDay);
        String lastTradingDayUnSym = DateUtils.coverToUnSymbol(lastTradingDay);
        List<TimeSlotHoldRate> records = timeSlotHoldRateMapper.queryByDate(lastTradingDayUnSym,tradingDateUnSym);
        Map<Integer,Integer> totalMaps = Maps.newHashMap();
        for (TimeSlotHoldRate record : records){
            if (!totalMaps.containsKey(record.getTimeSlot())){
                totalMaps.put(record.getTimeSlot(),new Integer(0));
            }
            totalMaps.put(record.getTimeSlot(),totalMaps.get(record.getTimeSlot())+record.getfCount());
        }
        Map<Integer, BigDecimal> holdRateMap = Maps.newHashMap();
        //计算时间段封板率
        for (TimeSlotHoldRate record : records){
            if (record.getIsBroken()==0){
                holdRateMap.put(record.getTimeSlot(),new BigDecimal(1.0*record.getfCount()/totalMaps.get(record.getTimeSlot())));
            }
        }
        for (TimeSlotHoldRate record : records){
            record.setDate(lastTradingDayUnSym);
            record.setIsFirst(1);
            if (holdRateMap.containsKey(record.getTimeSlot())){
                record.setHoldRate(holdRateMap.get(record.getTimeSlot()));
            }else{
                record.setHoldRate(new BigDecimal(0));
            }

        }
        //入库
        timeSlotHoldRateMapper.batchInsertOrUpdate(records);
    }

    public static void main(String[] args) {
        int i = 10;
        int b = 28;
        BigDecimal bigDecimal = new BigDecimal(1.0*i/b);
        System.out.println(bigDecimal);
    }
}
