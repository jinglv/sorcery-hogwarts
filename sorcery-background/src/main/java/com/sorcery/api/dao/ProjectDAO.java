package com.sorcery.api.dao;

import com.sorcery.api.common.MySqlExtensionMapper;
import com.sorcery.api.dto.project.QueryProjectListDTO;
import com.sorcery.api.entity.Project;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jinglv
 * @date 2022/9/19 14:53
 */
@Repository
public interface ProjectDAO extends MySqlExtensionMapper<Project> {
    /**
     * 查询项目统计总数
     *
     * @param params 查询项目信息
     * @return int
     */
    Integer count(@Param("params") QueryProjectListDTO params);

    /**
     * 列表分页查询
     *
     * @param params   查询项目信息
     * @param pageNum  分页的每页数量
     * @param pageSize 分页页数
     * @return 项目列表信息
     */
    List<Project> list(@Param("params") QueryProjectListDTO params, @Param("pageNum") Integer pageNum,
                       @Param("pageSize") Integer pageSize);
}
