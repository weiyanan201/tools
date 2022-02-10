package wei.tools.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import wei.tools.model.BrokenLimit;
import wei.tools.model.UpLimit;

public interface BrokenLimitMapper {
    int insert(BrokenLimit record);

    BrokenLimit selectByPrimaryKey(Integer id);

    List<BrokenLimit> findAll();

    int updateByPrimaryKey(BrokenLimit record);

    void batchInsertOrUpdate(@Param("records") List<BrokenLimit> records);
}