package wei.tools.dao;

import java.util.List;
import wei.tools.model.StockDetail;

public interface StockDetailMapper {
    int insert(StockDetail record);

    List<StockDetail> findAll();
}