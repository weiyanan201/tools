package wei.tools.service;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wei.tools.dao.TradingDayMapper;
import wei.tools.model.TradingDay;
import wei.tools.util.DateUtils;

import java.text.ParseException;
import java.util.List;

/**
 * @Author: weiyanan
 * @Date: 2022/2/11 16:38
 */
@Service
public class TradingDayService {

    @Autowired
    private TradingDayMapper tradingDayMapper;

    public boolean isTradingDay(String dateStr){
        DateUtils.checkFormat(dateStr);
        TradingDay day = tradingDayMapper.selectByDayStr(dateStr);
        return day.getIsTrading()==TradingDay.TRADING_TRUE;
    }

    /**
     * 获取上个交易日日期
     * @param todayStr
     * @return
     * @throws ParseException
     */
    public String getLastTradingDay(String todayStr) throws ParseException {
        DateUtils.checkFormat(todayStr);
        String lastDay = tradingDayMapper.getLastTradingDayStr(todayStr);
        return lastDay;
    }

    /**
     * 获取下个交易日日期
     * @param todayStr
     * @return
     * @throws ParseException
     */
    public String getNextTradingDay(String todayStr) throws ParseException {
        DateUtils.checkFormat(todayStr);
        String lastDay = tradingDayMapper.getNextTradingDayStr(todayStr);
        return lastDay;
    }

    /**
     * 获取一段时间内的所有交易日
     * @param from
     * @param to
     * @return
     * @throws ParseException
     */
    public List<String> getPeriodTradingDays(String from ,String to) throws ParseException {

        DateUtils.checkFormat(from);
        DateUtils.checkFormat(to);

        List<String> results = Lists.newArrayList();
        String tmpDay = from;
        while(StringUtils.compare(tmpDay,to)<=0){
            if (isTradingDay(tmpDay)){
                results.add(tmpDay);
            }
            tmpDay = DateUtils.getNextDay(tmpDay);
        }

        return results;
    }

}
