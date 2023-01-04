package com.sorcery.api.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.sorcery.api.constants.Constants;
import com.sorcery.api.dao.CaseDAO;
import com.sorcery.api.dao.ProjectDAO;
import com.sorcery.api.dto.ResultDTO;
import com.sorcery.api.dto.cases.QueryCaseListDTO;
import com.sorcery.api.dto.page.PageTableRequest;
import com.sorcery.api.dto.page.PageTableResponse;
import com.sorcery.api.entity.Cases;
import com.sorcery.api.entity.Project;
import com.sorcery.api.service.CaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 测试用例服务层实现
 *
 * @author jingLv
 * @date 2021/01/19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaseServiceImpl implements CaseService {

    private final CaseDAO caseDAO;
    private final ProjectDAO projectDAO;

    /**
     * 新增测试用例
     *
     * @param cases 测试用例信息
     * @return 返回接口测试用例保存结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Cases> save(Cases cases) {
        Project queryProject = new Project();
        queryProject.setId(cases.getProjectId())
                .setDelFlag(Constants.DEL_FLAG_ONE);
        Project project = projectDAO.selectOne(queryProject);
        if (Objects.isNull(project)) {
            return ResultDTO.fail("未查到项目信息");
        }
        // 设置测试用例未逻辑删除的标识
        cases.setDelFlag(Constants.DEL_FLAG_ONE);
        cases.setCreateTime(LocalDateTime.now());
        cases.setUpdateTime(LocalDateTime.now());
        int result = caseDAO.insertUseGeneratedKeys(cases);
        Assert.isFalse(result != 1, "新增测试用例失败");
        return ResultDTO.success("成功", cases);
    }

    /**
     * 删除测试用例信息（逻辑删除，使用的是update）
     *
     * @param caseId       测试用例主键id
     * @param createUserId 创建人用户id
     * @return 返回接口测试用例删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Cases> delete(Integer caseId, Integer createUserId) {
        // 根据传入的测试用例id和创建人id，查询测试用例信息，且该用例未被逻辑删除
        Cases queryCase = new Cases();
        queryCase.setId(caseId)
                .setCreateUserId(createUserId)
                .setDelFlag(Constants.DEL_FLAG_ONE);
        Cases result = caseDAO.selectOne(queryCase);
        // 如果为空，则提示
        if (Objects.isNull(result)) {
            return ResultDTO.fail("未查到测试用例信息");
        }
        // 设置测试用例逻辑删除（del_flag=1）
        result.setDelFlag(Constants.DEL_FLAG_ZERO);
        // 数据库更新测试用例
        int update = caseDAO.updateByPrimaryKey(result);
        Assert.isFalse(update != 1, "逻辑删除（更新）测试用例失败");
        return ResultDTO.success("成功");
    }

    /**
     * 修改测试用例信息（只能修改未被逻辑删除的测试用例）
     *
     * @param cases 测试用例更新信息
     * @return 返回接口测试用例更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Cases> update(Cases cases) {
        Project queryProject = new Project();
        queryProject.setId(cases.getProjectId())
                .setDelFlag(Constants.DEL_FLAG_ONE);
        Project project = projectDAO.selectOne(queryProject);
        if (Objects.isNull(project)) {
            return ResultDTO.fail("未查到项目信息");
        }
        // 根据传入的测试用例id和创建人id，查询测试用例信息，且该用例未被逻辑删除
        Cases queryCase = new Cases();
        queryCase.setId(cases.getId())
                .setCreateUserId(cases.getCreateUserId())
                .setDelFlag(Constants.DEL_FLAG_ONE);
        Cases result = caseDAO.selectOne(queryCase);
        if (Objects.isNull(result)) {
            return ResultDTO.fail("未查到测试用例信息");
        }
        // 修改测试用例信息
        cases.setCreateTime(result.getCreateTime());
        cases.setUpdateTime(LocalDateTime.now());
        cases.setDelFlag(Constants.DEL_FLAG_ONE);
        // 数据库更新测试用例
        int update = caseDAO.updateByPrimaryKey(cases);
        Assert.isFalse(update != 1, "修改测试用例信息失败");
        return ResultDTO.success("成功");
    }

    /**
     * 根据id查询测试用例
     *
     * @param caseId       测试用例主键id
     * @param createUserId 创建人用户id
     * @return 返回接口测试用例查询结果
     */
    @Override
    public ResultDTO<Cases> getById(Integer caseId, Integer createUserId) {
        // 根据传入的测试用例id和创建人id，查询测试用例信息，且该用例未被逻辑删除
        Cases queryCase = new Cases();
        queryCase.setId(caseId)
                .setCreateUserId(createUserId)
                .setDelFlag(Constants.DEL_FLAG_ONE);
        Cases result = caseDAO.selectOne(queryCase);
        //如果为空，则提示，也可以直接返回成功
        if (Objects.isNull(result)) {
            return ResultDTO.fail("未查到测试用例信息");
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
    public ResultDTO<PageTableResponse<Cases>> list(PageTableRequest<QueryCaseListDTO> pageTableRequest) {
        // 测试用例分页列表
        QueryCaseListDTO params = pageTableRequest.getParams();
        Integer pageNum = pageTableRequest.getPageNum();
        Integer pageSize = pageTableRequest.getPageSize();
        //总数
        Integer recordsTotal = caseDAO.count(params);
        //分页查询数据
        List<Cases> casesList = caseDAO.list(params, (pageNum - 1) * pageSize, pageSize);
        PageTableResponse<Cases> casesPageTableResponse = new PageTableResponse<>();
        casesPageTableResponse.setRecordsTotal(recordsTotal);
        casesPageTableResponse.setData(casesList);
        return ResultDTO.success("成功", casesPageTableResponse);
    }

    /**
     * 根据用户id和caseId查询case原始数据-直接返回字符串，因为会保存为文件
     *
     * @param createUserId 创建人用户id
     * @param caseId       测试用例主键id
     * @return 返回接口测试用例分页结果
     */
    @Override
    public ResultDTO<String> getCaseDataById(Integer createUserId, Integer caseId) {
        if (Objects.isNull(caseId)) {
            return ResultDTO.fail("用例id为空");
        }
        Cases queryCase = new Cases();
        queryCase.setCreateUserId(createUserId).setId(caseId);
        log.info("根据测试用例id查询case原始数据-查库入参：{}", JSONUtil.parse(queryCase));

        Cases result = caseDAO.selectOne(queryCase);
        if (Objects.isNull(result)) {
            return ResultDTO.fail("测试用例信息未查到");
        }
        if (ObjectUtils.isEmpty(result.getCaseData())) {
            return ResultDTO.fail("测试用例原始数据未查到");
        }
        return ResultDTO.success(result.getCaseData());
    }
}
