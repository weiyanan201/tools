package wei.tools.dao;

import org.apache.ibatis.annotations.Param;
import wei.tools.model.LimitEarnings;

import java.util.List;

/**
 *
 */
public interface LimitEarningsMapper {
    int insert(LimitEarnings record);

    List<LimitEarnings> findAll();

    /**
     * 统计次日涨停收益
     * @param lastTradingDateStr
     * @param dateStr
     * @return
     */
    List<LimitEarnings> countLimitEarnings(@Param("lastTradingDateStr")String lastTradingDateStr,@Param("dateStr")String dateStr);

    /**
     * 统计次日炸板收益
     * @param lastTradingDateStr
     * @param dateStr
     * @return
     */
    List<LimitEarnings> countBrokenEarnings(@Param("lastTradingDateStr")String lastTradingDateStr,@Param("dateStr")String dateStr);

    void batchInsertOrUpdate(@Param("records") List<LimitEarnings> records);
}