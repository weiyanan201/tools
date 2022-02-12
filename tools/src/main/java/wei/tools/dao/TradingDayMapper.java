package wei.tools.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import wei.tools.model.TradingDay;

public interface TradingDayMapper {
    int insert(TradingDay record);

    TradingDay selectByDayStr(@Param("dayStr") String dayStr);

    List<TradingDay> findAll();

    void batchInsertOrUpdate(@Param("calendars") List<TradingDay> calendars);

    //获取上一个交易日的日期
    String getLastTradingDayStr(@Param("dayStr") String dayStr);
}