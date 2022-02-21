package wei.tools.dao;

import java.util.List;
import wei.tools.model.LimitEarnings;

/**
 *
 */
public interface LimitEarningsMapper {
    int insert(LimitEarnings record);

    List<LimitEarnings> findAll();
}