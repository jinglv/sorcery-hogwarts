package com.sorcery.api.dao;

import com.sorcery.api.common.MySqlExtensionMapper;
import com.sorcery.api.dto.cases.TaskCaseRelDetailDTO;
import com.sorcery.api.dto.cases.TaskCaseRelListDTO;
import com.sorcery.api.entity.TaskCaseRel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jinglv
 * @date 2021/01/19
 */
@Repository
public interface TaskCaseRelDAO extends MySqlExtensionMapper<TaskCaseRel> {
    /**
     * 查询测试任务与测试用例关联信息
     *
     * @param params   查询测试任务与测试用例关联信息
     * @param pageNum  分页的每页数量
     * @param pageSize 分页页数
     * @return 测试任务与测试用例关联列表信息
     */
    List<TaskCaseRelDetailDTO> listDetail(@Param("params") TaskCaseRelListDTO params, @Param("pageNum") Integer pageNum,
                                          @Param("pageSize") Integer pageSize);
}