package wei.tools.dao;

import java.util.List;

import wei.tools.aop.WithPage;
import wei.tools.entity.PageRequest;
import wei.tools.entity.PageResult;
import wei.tools.model.Test;

public interface TestMapper {
    int insert(Test record);

    Test selectByPrimaryKey(Integer id);

    @WithPage
    List<Test> findAll(PageRequest pageRequest, PageResult pageResult);

    int updateByPrimaryKey(Test record);

}