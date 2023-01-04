package com.sorcery.api.dao;

import com.sorcery.api.common.MySqlExtensionMapper;
import com.sorcery.api.dto.jenkins.QueryJenkinsListDTO;
import com.sorcery.api.entity.Jenkins;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jinglv
 * @date 2021/01/19
 */
@Repository
public interface JenkinsDAO extends MySqlExtensionMapper<Jenkins> {

    /**
     * 统计总数
     *
     * @param params 查询Jenkins信息
     * @return int
     */
    Integer count(@Param("params") QueryJenkinsListDTO params);

    /**
     * 列表分页查询
     *
     * @param params   查询Jenkins信息
     * @param pageNum  分页的每页数量
     * @param pageSize 分页页数
     * @return Jenkins信息
     */
    List<Jenkins> list(@Param("params") QueryJenkinsListDTO params,
                       @Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);
}