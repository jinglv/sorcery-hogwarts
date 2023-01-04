package com.sorcery.api.service.impl;

import com.sorcery.api.common.utils.ReportUtils;
import com.sorcery.api.constants.Constants;
import com.sorcery.api.dao.JenkinsDAO;
import com.sorcery.api.dao.TaskDAO;
import com.sorcery.api.dto.ResultDTO;
import com.sorcery.api.dto.TokenDTO;
import com.sorcery.api.dto.report.AllureReportDTO;
import com.sorcery.api.dto.report.TaskDataDTO;
import com.sorcery.api.dto.report.TaskReportDTO;
import com.sorcery.api.entity.Jenkins;
import com.sorcery.api.entity.Task;
import com.sorcery.api.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author jingLv
 * @date 2021/01/25
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class ReportServiceImpl implements ReportService {
    private final TaskDAO taskDAO;
    private final JenkinsDAO jenkinsDAO;

    /**
     * 获取allure报告
     *
     * @param tokenDto token信息
     * @param taskId   执行任务id
     * @return 返回接口allure测试报告
     */
    @Override
    public ResultDTO<AllureReportDTO> getAllureReport(TokenDTO tokenDto, Integer taskId) {
        Task queryTask = new Task();
        queryTask.setId(taskId);
        queryTask.setCreateUserId(tokenDto.getUserId());

        Task resultTask = taskDAO.selectOne(queryTask);
        if (Objects.isNull(resultTask)) {
            return ResultDTO.fail("测试任务不存在 " + taskId);
        }
        String buildUrl = resultTask.getBuildUrl();
        if (ObjectUtils.isEmpty(buildUrl)) {
            return ResultDTO.fail("测试任务的构建地址不存在 " + taskId);
        }
        Integer testJenkinsId = resultTask.getJenkinsId();
        if (Objects.isNull(testJenkinsId)) {
            return ResultDTO.fail("测试任务的jenkinsId不存在 " + taskId);
        }
        // 构造查询Jenkins条件，根据JenkinsId和当前登录的用户id
        Jenkins queryJenkins = new Jenkins();
        queryJenkins.setId(testJenkinsId)
                .setCreateUserId(tokenDto.getUserId());
        Jenkins resultJenkins = jenkinsDAO.selectOne(queryJenkins);
        // 获取Allure测试报告Url
        String allureReportUrl = ReportUtils.getAllureReportUrl(buildUrl, resultJenkins, true);

        AllureReportDTO allureReportDto = new AllureReportDTO();
        allureReportDto.setTaskId(taskId)
                .setAllureReportUrl(allureReportUrl);
        return ResultDTO.success("成功", allureReportDto);
    }

    /**
     * 根据任务类型获取任务统计信息
     *
     * @param tokenDto token信息
     * @return 接口返回报告查询结果
     */
    @Override
    public ResultDTO<TaskReportDTO> getTaskByType(TokenDTO tokenDto) {
        TaskReportDTO taskReportDto = new TaskReportDTO();
        int taskSum = 0;
        List<TaskDataDTO> taskDataDTOList = taskDAO.getTaskByType(tokenDto.getUserId());
        if (Objects.isNull(taskDataDTOList) || taskDataDTOList.isEmpty()) {
            ResultDTO.fail("无任务数据");
        }
        List<TaskDataDTO> newtTaskDataDTOList = new ArrayList<>();
        for (TaskDataDTO taskDataDto : taskDataDTOList) {
            Integer taskKey = taskDataDto.getTaskKey();
            if (Objects.isNull(taskKey)) {
                taskKey = 0;
            }
            if (Constants.STATUS_ZERO.equals(taskKey)) {
                taskDataDto.setDescription("无匹配任务");
            }
            if (Constants.TASK_TYPE_ONE.equals(taskKey)) {
                taskDataDto.setDescription("普通测试任务");
            }
            if (Constants.TASK_TYPE_TWO.equals(taskKey)) {
                taskDataDto.setDescription("一键执行测试的任务");
            }
            taskSum = taskSum + taskDataDto.getTaskCount();
            newtTaskDataDTOList.add(taskDataDto);
        }
        taskReportDto.setTaskSum(taskSum);
        taskReportDto.setTaskDataDTOList(newtTaskDataDTOList);

        return ResultDTO.success("成功", taskReportDto);
    }

    /**
     * 根据任务状态获取任务统计信息
     *
     * @param tokenDto token信息
     * @return 接口返回报告查询结果
     */
    @Override
    public ResultDTO<TaskReportDTO> getTaskByStatus(TokenDTO tokenDto) {
        TaskReportDTO taskReportDto = new TaskReportDTO();
        int taskSum = 0;
        List<TaskDataDTO> taskDataDTOList = taskDAO.getTaskByStatus(tokenDto.getUserId());
        if (Objects.isNull(taskDataDTOList) || taskDataDTOList.isEmpty()) {
            ResultDTO.fail("无任务数据");
        }
        List<TaskDataDTO> newtTaskDataDTOList = new ArrayList<>();
        for (TaskDataDTO taskDataDto : taskDataDTOList) {
            Integer taskKey = taskDataDto.getTaskKey();
            if (Objects.isNull(taskKey)) {
                taskKey = 0;
            }
            if (Constants.STATUS_ZERO.equals(taskKey)) {
                taskDataDto.setDescription("无匹配任务");
            }
            if (Constants.STATUS_ONE.equals(taskKey)) {
                taskDataDto.setDescription("新建");
            }
            if (Constants.STATUS_TWO.equals(taskKey)) {
                taskDataDto.setDescription("执行中");
            }
            if (Constants.STATUS_THREE.equals(taskKey)) {
                taskDataDto.setDescription("已完成");
            }
            taskSum = taskSum + taskDataDto.getTaskCount();
            newtTaskDataDTOList.add(taskDataDto);
        }
        taskReportDto.setTaskSum(taskSum).setTaskDataDTOList(newtTaskDataDTOList);
        return ResultDTO.success("成功", taskReportDto);
    }

    /**
     * 任务中用例的数量统计信息
     *
     * @param tokenDto token信息
     * @param start    按时间倒叙开始序号
     * @param end      按时间倒叙结束序号
     * @return 接口返回报告查询结果
     */
    @Override
    public ResultDTO<List<Task>> getTaskByCaseCount(TokenDTO tokenDto, Integer start, Integer end) {
        List<Task> taskList = taskDAO.getCaseCountByTask(tokenDto.getUserId(), start, end);
        if (Objects.isNull(taskList) || taskList.isEmpty()) {
            return ResultDTO.fail("无任务数据");
        }
        return ResultDTO.success("成功", taskList);
    }
}
