package wei.tools.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import wei.tools.model.DropLimit;
import wei.tools.model.UpLimit;

public interface DropLimitMapper {
    int insert(DropLimit record);

    DropLimit selectByPrimaryKey(Integer id);

    List<DropLimit> findAll();

    int updateByPrimaryKey(DropLimit record);

    void batchInsertOrUpdate(@Param("records") List<DropLimit> records);
}