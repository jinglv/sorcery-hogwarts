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
     * 统计总数
     *
     * @param params
     * @return
     */
    Integer count(@Param("params") QueryProjectListDTO params);

    /**
     * 列表分页查询
     *
     * @param params
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<Project> list(@Param("params") QueryProjectListDTO params, @Param("pageNum") Integer pageNum,
                       @Param("pageSize") Integer pageSize);
}
