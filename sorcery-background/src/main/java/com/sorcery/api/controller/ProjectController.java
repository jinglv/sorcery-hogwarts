package com.sorcery.api.controller;

import cn.hutool.json.JSONUtil;
import com.sorcery.api.common.token.TokenDb;
import com.sorcery.api.common.utils.FileUtils;
import com.sorcery.api.constants.UserConstants;
import com.sorcery.api.dto.ResultDTO;
import com.sorcery.api.dto.TokenDTO;
import com.sorcery.api.dto.page.PageTableRequest;
import com.sorcery.api.dto.page.PageTableResponse;
import com.sorcery.api.dto.project.ProjectDTO;
import com.sorcery.api.dto.project.QueryProjectListDTO;
import com.sorcery.api.dto.project.UpdateProjectDTO;
import com.sorcery.api.entity.Project;
import com.sorcery.api.service.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 项目管理
 *
 * @author jinglv
 * @date 2022/9/19 15:07
 */
@Slf4j
@Api(tags = "项目管理")
@RequiredArgsConstructor
@RestController
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    private final FileUtils fileUtils;
    private final TokenDb tokenDb;

    /**
     * 新增项目
     *
     * @param request    servlet request
     * @param projectDTO 请求参数
     * @return 返回接口请求结果
     */
    @ApiOperation(value = "新增项目", notes = "新增项目")
    @PostMapping("add")
    public ResultDTO<Project> save(HttpServletRequest request, @RequestParam("image") MultipartFile image, ProjectDTO projectDTO) {
        log.info("新增项目,请求参数：{}", JSONUtil.parse(projectDTO));
        if (Objects.isNull(projectDTO)) {
            return ResultDTO.fail("请求参数不能为空");
        }
        if (ObjectUtils.isEmpty(projectDTO.getProjectName())) {
            return ResultDTO.fail("项目名称不能为空");
        }
        // 上传图片
        String url = fileUtils.uploads(image);
        Project project = new Project();
        project.setProjectName(projectDTO.getProjectName())
                .setGitName(projectDTO.getGitName())
                .setGitAddress(projectDTO.getGitAddress())
                .setGitCredentialsId(projectDTO.getGitCredentialsId())
                .setDescription(projectDTO.getDescription())
                .setImage(url);
        TokenDTO tokenDto = tokenDb.getTokenDto(request.getHeader(UserConstants.LOGIN_TOKEN));
        project.setCreateUserId(tokenDto.getUserId());
        return projectService.save(project);
    }

    /**
     * 根据项目id查询
     *
     * @param request   servlet request
     * @param projectId 测试用例id
     * @return 返回接口请求结果
     */
    @ApiOperation(value = "根据项目id查询")
    @GetMapping("{projectId}")
    public ResultDTO<Project> getById(HttpServletRequest request, @PathVariable Integer projectId) {
        log.info("根据项目id查询测试用例，测试用例id:{}", projectId);
        if (Objects.isNull(projectId)) {
            return ResultDTO.fail("项目id不能为空");
        }
        TokenDTO tokenDto = tokenDb.getTokenDto(request.getHeader(UserConstants.LOGIN_TOKEN));
        return projectService.getById(projectId, tokenDto.getUserId());
    }

    /**
     * 分页列表查询
     *
     * @param request          servlet request
     * @param pageTableRequest 分页请求参数
     * @return 返回接口请求结果
     */
    @ApiOperation(value = "分页列表查询")
    @GetMapping("list")
    public ResultDTO<PageTableResponse<Project>> list(HttpServletRequest request, PageTableRequest<QueryProjectListDTO> pageTableRequest) {
        log.info("项目分页列表查询请求参数：{} ", JSONUtil.parse(pageTableRequest));
        if (Objects.isNull(pageTableRequest)) {
            return ResultDTO.success("列表查询参数不能为空");
        }
        TokenDTO tokenDto = tokenDb.getTokenDto(request.getHeader(UserConstants.LOGIN_TOKEN));
        QueryProjectListDTO params = pageTableRequest.getParams();
        if (Objects.isNull(params)) {
            params = new QueryProjectListDTO();
        }
        params.setCreateUserId(tokenDto.getUserId());
        pageTableRequest.setParams(params);
        return projectService.list(pageTableRequest);
    }

    /**
     * 通过projectId删除项目
     *
     * @param projectId 项目id（主键）
     * @return 返回接口请求结果
     */
    @ApiOperation(value = "通过projectId删除项目")
    @DeleteMapping("{projectId}")
    public ResultDTO<Project> delete(HttpServletRequest request, @PathVariable Integer projectId) {
        log.info("根据项目id删除测试-请求参数:{}", projectId);
        if (Objects.isNull(projectId)) {
            return ResultDTO.fail("项目id不能为空");
        }
        TokenDTO tokenDto = tokenDb.getTokenDto(request.getHeader(UserConstants.LOGIN_TOKEN));
        return projectService.delete(projectId, tokenDto.getUserId());
    }

    /**
     * 修改项目信息
     *
     * @param request          servlet request
     * @param updateProjectDTO 项目修改信息
     * @return 返回接口请求结果
     */
    @ApiOperation(value = "修改项目信息")
    @PutMapping
    public ResultDTO<Project> update(HttpServletRequest request, @RequestBody UpdateProjectDTO updateProjectDTO) {
        log.info("修改测试用例,请求参数：{}", JSONUtil.parse(updateProjectDTO));
        if (Objects.isNull(updateProjectDTO)) {
            return ResultDTO.fail("项目信息不能为空");
        }
        Integer projectId = updateProjectDTO.getId();
        String projectName = updateProjectDTO.getProjectName();
        if (Objects.isNull(projectId)) {
            return ResultDTO.fail("项目id不能为空");
        }
        if (ObjectUtils.isEmpty(projectName)) {
            return ResultDTO.fail("项目名称不能为空");
        }
        Project project = new Project();
        project.setId(updateProjectDTO.getId())
                .setProjectName(updateProjectDTO.getProjectName())
                .setGitName(updateProjectDTO.getGitName())
                .setGitAddress(updateProjectDTO.getGitAddress())
                .setGitCredentialsId(updateProjectDTO.getGitCredentialsId())
                .setDescription(updateProjectDTO.getDescription())
                .setImage(updateProjectDTO.getImage());
        TokenDTO tokenDto = tokenDb.getTokenDto(request.getHeader(UserConstants.LOGIN_TOKEN));
        project.setCreateUserId(tokenDto.getUserId());
        return projectService.update(project);
    }
}
