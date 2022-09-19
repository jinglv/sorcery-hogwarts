package com.sorcery.api.service;

import com.sorcery.api.dto.ResultDTO;
import com.sorcery.api.dto.page.PageTableRequest;
import com.sorcery.api.dto.page.PageTableResponse;
import com.sorcery.api.dto.project.QueryProjectListDTO;
import com.sorcery.api.entity.Project;

/**
 * 项目管理操作服务层
 *
 * @author jinglv
 * @date 2022/9/19 14:55
 */
public interface ProjectService {

    /**
     * 新增项目）
     *
     * @param project 项目信息
     * @return 返回接口项目保存结果
     */
    ResultDTO<Project> save(Project project);

    /**
     * 根据id项目信息
     *
     * @param projectId    测试用例主键id
     * @param createUserId 创建人用户id
     * @return 返回接口测试用例查询结果
     */
    ResultDTO<Project> getById(Integer projectId, Integer createUserId);

    /**
     * 查询测试用例信息列表
     *
     * @param pageTableRequest 分页查询
     * @return 返回接口测试用例分页查询结果
     */
    ResultDTO<PageTableResponse<Project>> list(PageTableRequest<QueryProjectListDTO> pageTableRequest);

    /**
     * 删除项目信息
     *
     * @param projectId    项目主键id
     * @param createUserId 创建人用户id
     * @return 返回接口测试用例删除结果
     */
    ResultDTO<Project> delete(Integer projectId, Integer createUserId);

    /**
     * 修改项目信息
     *
     * @param project 测试用例更新信息
     * @return 返回接口测试用例更新结果
     */
    ResultDTO<Project> update(Project project);

}
