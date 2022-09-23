package com.sorcery.api.service.impl;

import cn.hutool.core.lang.Assert;
import com.sorcery.api.constants.Constants;
import com.sorcery.api.dao.ProjectDAO;
import com.sorcery.api.dto.ResultDTO;
import com.sorcery.api.dto.page.PageTableRequest;
import com.sorcery.api.dto.page.PageTableResponse;
import com.sorcery.api.dto.project.QueryProjectListDTO;
import com.sorcery.api.entity.Project;
import com.sorcery.api.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author jinglv
 * @date 2022/9/19 14:57
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectDAO projectDAO;

    /**
     * 新增项目）
     *
     * @param project 项目信息
     * @return 返回接口项目保存结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Project> save(Project project) {
        // 获取项目名称
        String projectName = project.getProjectName();
        // 查询条件
        Project queryProject = new Project();
        queryProject.setProjectName(projectName);
        // 根据用户名在数据库中查询数据
        Project queryResult = projectDAO.selectOne(queryProject);
        if (Objects.nonNull(queryResult)) {
            return ResultDTO.fail("项目名已存在");
        }
        // 如果项目图片为空，设置默认图片
        if (project.getImage() == null || Objects.equals(project.getImage(), "")) {
            project.setImage("/images/62817b622b554e21b094934e59d5539a.jpeg");
        }
        // 设置项目未逻辑删除的标识
        project.setDelFlag(Constants.DEL_FLAG_ONE);
        project.setCreateTime(new Date());
        project.setUpdateTime(new Date());
        int result = projectDAO.insertUseGeneratedKeys(project);
        Assert.isFalse(result != 1, "新增项目失败");
        return ResultDTO.success("成功", project);
    }

    /**
     * 根据id项目信息
     *
     * @param projectId    测试用例主键id
     * @param createUserId 创建人用户id
     * @return 返回接口测试用例查询结果
     */
    @Override
    public ResultDTO<Project> getById(Integer projectId, Integer createUserId) {
        // 根据传入的测试用例id和创建人id，查询测试用例信息，且该用例未被逻辑删除
        Project queryProject = new Project();
        queryProject.setId(projectId)
                .setCreateUserId(createUserId)
                .setDelFlag(Constants.DEL_FLAG_ONE);
        Project result = projectDAO.selectOne(queryProject);
        //如果为空，则提示，也可以直接返回成功
        if (Objects.isNull(result)) {
            return ResultDTO.fail("未查到项目信息");
        }
        return ResultDTO.success("成功", result);
    }

    /**
     * 查询测试用例信息列表
     *
     * @param pageTableRequest 分页查询
     * @return 返回接口测试用例分页查询结果
     */
    @Override
    public ResultDTO<PageTableResponse<Project>> list(PageTableRequest<QueryProjectListDTO> pageTableRequest) {
        // 测试用例分页列表
        QueryProjectListDTO params = pageTableRequest.getParams();
        Integer pageNum = pageTableRequest.getPageNum();
        Integer pageSize = pageTableRequest.getPageSize();
        //总数
        Integer recordsTotal = projectDAO.count(params);
        //分页查询数据
        List<Project> projectList = projectDAO.list(params, (pageNum - 1) * pageSize, pageSize);

        PageTableResponse<Project> projectsPageTableResponse = new PageTableResponse<>();
        projectsPageTableResponse.setRecordsTotal(recordsTotal);
        projectsPageTableResponse.setData(projectList);
        return ResultDTO.success("成功", projectsPageTableResponse);
    }

    /**
     * 删除项目信息
     *
     * @param projectId    项目主键id
     * @param createUserId 创建人用户id
     * @return 返回接口测试用例删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Project> delete(Integer projectId, Integer createUserId) {
        // 根据传入的测试用例id和创建人id，查询测试用例信息，且该用例未被逻辑删除
        Project queryProject = new Project();
        queryProject.setId(projectId)
                .setCreateUserId(createUserId)
                .setDelFlag(Constants.DEL_FLAG_ONE);
        Project result = projectDAO.selectOne(queryProject);
        // 如果为空，则提示
        if (Objects.isNull(result)) {
            return ResultDTO.fail("未查到项目信息");
        }
        // 设置测试用例逻辑删除（del_flag=1）
        result.setDelFlag(Constants.DEL_FLAG_ZERO);
        // 数据库更新项目
        int update = projectDAO.updateByPrimaryKey(result);
        Assert.isFalse(update != 1, "逻辑删除（更新）项目失败");
        return ResultDTO.success("成功");
    }

    /**
     * 修改项目信息
     *
     * @param project 测试用例更新信息
     * @return 返回接口测试用例更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Project> update(Project project) {
        // 根据传入的测试用例id和创建人id，查询测试用例信息，且该用例未被逻辑删除
        Project queryProject = new Project();
        queryProject.setId(project.getId())
                .setCreateUserId(project.getCreateUserId())
                .setDelFlag(Constants.DEL_FLAG_ONE);
        Project result = projectDAO.selectOne(queryProject);
        if (Objects.isNull(result)) {
            return ResultDTO.fail("未查到项目信息");
        }
        // 修改测试用例信息
        project.setCreateTime(result.getCreateTime());
        project.setUpdateTime(new Date());
        project.setDelFlag(Constants.DEL_FLAG_ONE);
        // 数据库更新测试用例
        int update = projectDAO.updateByPrimaryKey(project);
        Assert.isFalse(update != 1, "修改项目信息失败");
        return ResultDTO.success("成功");
    }
}
