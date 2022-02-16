package wei.tools.dao;

import java.util.List;
import wei.tools.model.EmotionalCycle;

public interface EmotionalCycleMapper {
    void insertOrUpdate(EmotionalCycle record);

    List<EmotionalCycle> findAll();
}