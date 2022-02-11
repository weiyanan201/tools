package wei.tools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wei.tools.dao.TradingDayMapper;
import wei.tools.model.TradingDay;
import wei.tools.util.DateUtils;

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

}
