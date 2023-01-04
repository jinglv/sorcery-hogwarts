package com.sorcery.api.dao;

import com.sorcery.api.common.MySqlExtensionMapper;
import com.sorcery.api.dto.cases.QueryCaseListDTO;
import com.sorcery.api.entity.Cases;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jinglv
 * @date 2021/01/19
 */
@Repository
public interface CaseDAO extends MySqlExtensionMapper<Cases> {

    /**
     * 统计总数
     *
     * @param params 查询测试用例信息
     * @return int
     */
    Integer count(@Param("params") QueryCaseListDTO params);

    /**
     * 列表分页查询
     *
     * @param params   查询测试用例列表信息
     * @param pageNum  分页的每页数量
     * @param pageSize 分页页数
     * @return 测试用例信息
     */
    List<Cases> list(@Param("params") QueryCaseListDTO params, @Param("pageNum") Integer pageNum,
                     @Param("pageSize") Integer pageSize);
}