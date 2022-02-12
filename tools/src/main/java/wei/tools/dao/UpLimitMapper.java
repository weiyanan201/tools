package wei.tools.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import wei.tools.model.UpLimit;

public interface UpLimitMapper {
    int insert(UpLimit record);

    UpLimit selectByPrimaryKey(Integer id);

    List<UpLimit> findAll();

    int updateByPrimaryKey(UpLimit record);

    void batchInsertOrUpdate(@Param("records") List<UpLimit> records);

    List<UpLimit> selectByDateStr(@Param("dateStr")String dateStr);
}