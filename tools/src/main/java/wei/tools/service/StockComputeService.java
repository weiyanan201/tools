package wei.tools.service;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wei.tools.dao.BrokenRateMapper;
import wei.tools.dao.LimitEarningsMapper;
import wei.tools.model.BrokenRate;
import wei.tools.model.LimitEarnings;
import wei.tools.util.DateUtils;

import java.io.IOException;
import java.util.List;

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
}
