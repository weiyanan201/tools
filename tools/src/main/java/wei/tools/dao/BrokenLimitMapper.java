package wei.tools.dao;

import org.apache.ibatis.annotations.Param;
import wei.tools.model.BrokenLimit;

import java.util.List;

public interface BrokenLimitMapper {
    int insert(BrokenLimit record);

    BrokenLimit selectByPrimaryKey(Integer id);

    List<BrokenLimit> findAll();

    int updateByPrimaryKey(BrokenLimit record);

    void batchInsertOrUpdate(@Param("records") List<BrokenLimit> records);

    List<BrokenLimit> selectByDateStr(@Param("dateStr")String dateStr);
}