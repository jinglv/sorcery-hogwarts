package com.sorcery.api.service.impl;

import cn.hutool.core.lang.Assert;
import com.sorcery.api.dao.JenkinsDAO;
import com.sorcery.api.dto.ResultDTO;
import com.sorcery.api.dto.TokenDTO;
import com.sorcery.api.dto.jenkins.QueryJenkinsListDTO;
import com.sorcery.api.dto.page.PageTableRequest;
import com.sorcery.api.dto.page.PageTableResponse;
import com.sorcery.api.entity.Jenkins;
import com.sorcery.api.service.JenkinsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author jingLv
 * @date 2021/01/19
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class JenkinsServiceImpl implements JenkinsService {

    private final JenkinsDAO jenkinsDAO;

    /**
     * 新增Jenkins信息
     *
     * @param tokenDto token信息
     * @param jenkins  Jenkins信息
     * @return 返回接口Jenkins保存结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Jenkins> save(TokenDTO tokenDto, Jenkins jenkins) {
        int insert = jenkinsDAO.insertUseGeneratedKeys(jenkins);
        Assert.isFalse(insert != 1, "新增Jenkins信息失败");
        return ResultDTO.success("成功", jenkins);
    }

    /**
     * 删除Jenkins信息
     *
     * @param jenkinsId jenkins主键id
     * @return 返回接口Jenkins删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Jenkins> delete(Integer jenkinsId) {
        int delete = jenkinsDAO.deleteByPrimaryKey(jenkinsId);
        Assert.isFalse(delete != 1, "删除Jenkins信息失败");
        return ResultDTO.success("成功");
    }

    /**
     * 修改Jenkins信息
     *
     * @param tokenDto token信息
     * @param jenkins  jenkins更新信息
     * @return 返回接口Jenkins更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Jenkins> update(TokenDTO tokenDto, Jenkins jenkins) {
        Jenkins queryJenkins = new Jenkins();
        // 根据jenkinsId查询Jenkins信息
        queryJenkins.setId(jenkins.getId());
        queryJenkins.setCreateUserId(tokenDto.getUserId());
        Jenkins result = jenkinsDAO.selectOne(queryJenkins);
        // 如果为空，则提示，也可以直接返回成功
        if (Objects.isNull(result)) {
            return ResultDTO.fail("未查到Jenkins信息");
        }
        jenkins.setCreateTime(result.getCreateTime());
        jenkins.setUpdateTime(LocalDateTime.now());
        // 更新Jenkins信息
        int update = jenkinsDAO.updateByPrimaryKey(jenkins);
        Assert.isFalse(update != 1, "更新Jenkins信息失败");
        return ResultDTO.success("成功");
    }

    /**
     * 根据id查询Jenkins信息
     *
     * @param jenkinsId    jenkins主键id
     * @param createUserId 创建人用户id
     * @return 返回接口Jenkins查询结果
     */
    @Override
    public ResultDTO<Jenkins> getById(Integer jenkinsId, Integer createUserId) {
        Jenkins queryJenkins = new Jenkins();
        queryJenkins.setId(jenkinsId);
        queryJenkins.setCreateUserId(createUserId);
        Jenkins result = jenkinsDAO.selectOne(queryJenkins);
        //如果为空，则提示，也可以直接返回成功
        if (Objects.isNull(result)) {
            ResultDTO.fail("未查到Jenkins信息");
        }
        return ResultDTO.success("成功", result);
    }

    /**
     * 查询Jenkins信息列表
     *
     * @param pageTableRequest 分页查询
     * @return 返回接口Jenkins分页查询结果
     */
    @Override
    public ResultDTO<PageTableResponse<Jenkins>> list(PageTableRequest<QueryJenkinsListDTO> pageTableRequest) {
        QueryJenkinsListDTO params = pageTableRequest.getParams();
        Integer pageNum = pageTableRequest.getPageNum();
        Integer pageSize = pageTableRequest.getPageSize();
        //总数
        Integer recordsTotal = jenkinsDAO.count(params);
        //分页查询数据
        List<Jenkins> jenkinsList = jenkinsDAO.list(params, (pageNum - 1) * pageSize, pageSize);
        PageTableResponse<Jenkins> jenkinsPageTableResponse = new PageTableResponse<>();
        jenkinsPageTableResponse.setRecordsTotal(recordsTotal);
        jenkinsPageTableResponse.setData(jenkinsList);
        return ResultDTO.success("成功", jenkinsPageTableResponse);
    }
}
