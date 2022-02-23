package wei.tools.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import wei.tools.model.BrokenRate;

public interface BrokenRateMapper {
    int insert(BrokenRate record);

    List<BrokenRate> findAll();

    BrokenRate queryByDate(@Param("dateStr") String dateStr);

}