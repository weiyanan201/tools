package wei.tools.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import wei.tools.model.Calendar;

public interface CalendarMapper {
    int insert(Calendar record);

    Calendar selectByDayStr(@Param("dayStr") String dayStr);

    List<Calendar> findAll();

    void batchInsertOrUpdate(@Param("calendars") List<Calendar> calendars);
}