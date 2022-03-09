package wei.tools.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import wei.tools.model.TimeSlotHoldRate;

public interface TimeSlotHoldRateMapper {
    int insert(TimeSlotHoldRate record);

    List<TimeSlotHoldRate> findAll();

    List<TimeSlotHoldRate> queryByDate(@Param("lastDateStr") String lastDateStr,@Param("dateStr") String dateStr);

    void batchInsertOrUpdate(@Param("records") List<TimeSlotHoldRate> records);
}