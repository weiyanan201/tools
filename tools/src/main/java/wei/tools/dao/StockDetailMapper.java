package wei.tools.dao;

import org.apache.ibatis.annotations.Param;
import wei.tools.model.StockDetail;

import java.util.List;

public interface StockDetailMapper {
    int insert(StockDetail record);

    List<StockDetail> findAll();

    void batchInsertOrUpdate(@Param("records") List<StockDetail> records);
}