package wei.tools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wei.tools.dao.TradingDayMapper;
import wei.tools.model.TradingDay;
import wei.tools.util.DateUtils;

import java.text.ParseException;

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
        String lastDay = tradingDayMapper.getLastTradingDayStr(todayStr);
        return lastDay;
    }

}
