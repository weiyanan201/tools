package wei.tools.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import wei.tools.model.EmotionalCycle;

public interface EmotionalCycleMapper {

    void insertOrUpdate(EmotionalCycle record);

    List<EmotionalCycle> findAll();

    EmotionalCycle selectByDate(@Param(value = "dateStr") String dateStr);

    @Deprecated
    void fixUpdateEarningRate(EmotionalCycle record);
}