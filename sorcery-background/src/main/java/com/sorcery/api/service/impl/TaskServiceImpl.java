package com.sorcery.api.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.sorcery.api.common.exception.ServiceException;
import com.sorcery.api.common.jenkins.JenkinsClient;
import com.sorcery.api.common.jenkins.JenkinsUtils;
import com.sorcery.api.common.utils.StrUtils;
import com.sorcery.api.constants.Constants;
import com.sorcery.api.dao.CaseDAO;
import com.sorcery.api.dao.JenkinsDAO;
import com.sorcery.api.dao.TaskCaseRelDAO;
import com.sorcery.api.dao.TaskDAO;
import com.sorcery.api.dto.RequestInfoDTO;
import com.sorcery.api.dto.ResultDTO;
import com.sorcery.api.dto.TokenDTO;
import com.sorcery.api.dto.jenkins.OperateJenkinsJobDTO;
import com.sorcery.api.dto.page.PageTableRequest;
import com.sorcery.api.dto.page.PageTableResponse;
import com.sorcery.api.dto.task.AddTaskDTO;
import com.sorcery.api.dto.task.QueryTaskListDTO;
import com.sorcery.api.dto.task.TaskDTO;
import com.sorcery.api.entity.*;
import com.sorcery.api.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author jingLv
 * @date 2021/01/21
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskDAO taskDAO;
    private final JenkinsDAO jenkinsDAO;
    private final CaseDAO caseDAO;
    private final TaskCaseRelDAO taskCaseRelDAO;
    private final JenkinsClient jenkinsClient;

    /**
     * 新增测试任务信息
     *
     * @param taskDto  task信息
     * @param taskType task类型
     * @return 返回接口task保存结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Task> save(TaskDTO taskDto, Integer taskType) {
        StringBuilder testCommand = new StringBuilder();
        AddTaskDTO testTask = taskDto.getTestTask();
        List<Integer> caseIdList = taskDto.getCaseIdList();

        log.info("新增任务数据：{}", testTask);
        Jenkins queryJenkins = new Jenkins();
        queryJenkins.setId(testTask.getJenkinsId());
        queryJenkins.setCreateUserId(testTask.getCreateUserId());
        Jenkins result = jenkinsDAO.selectOne(queryJenkins);
        if (Objects.isNull(result)) {
            return ResultDTO.fail("Jenkins信息为空");
        }
        // 根据测试用例Id的列表，数据库中查询测试用例
        List<Cases> casesList = caseDAO.selectByIds(StrUtils.list2IdsStr(caseIdList));
        // 组装测试命令
        makeTestCommand(testCommand, result, casesList);
        Task task = new Task();
        task.setName(testTask.getName())
                .setJenkinsId(testTask.getJenkinsId())
                .setCreateUserId(testTask.getCreateUserId())
                .setRemark(testTask.getRemark())
                .setTaskType(taskType)
                .setCommand(testCommand.toString())
                .setCaseCount(caseIdList.size())
                .setStatus(Constants.STATUS_ONE)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        // 新建测试任务
        int taskInsert = taskDAO.insert(task);
        Assert.isFalse(taskInsert != 1, "新建测试任务失败！");
        // 测试任务与测试用例对应关联，测试任务与测试用例，一对多的关系
        if (!caseIdList.isEmpty()) {
            List<TaskCaseRel> testTaskCaseList = new ArrayList<>();
            for (Integer testCaseId : caseIdList) {
                TaskCaseRel taskCaseRel = new TaskCaseRel();
                taskCaseRel.setTaskId(task.getId())
                        .setCaseId(testCaseId)
                        .setCreateUserId(task.getCreateUserId())
                        .setCreateUserId(task.getCreateUserId())
                        .setCreateTime(LocalDateTime.now())
                        .setUpdateTime(LocalDateTime.now());
                testTaskCaseList.add(taskCaseRel);
            }
            log.info("测试任务详情保存，存入数据库参数：{}", JSONUtil.toJsonStr(testTaskCaseList));
            int taskCaseInsert = taskCaseRelDAO.insertList(testTaskCaseList);
            Assert.isFalse(taskCaseInsert < 0, "新建测试任务与测试用例关系失败！");
        }
        return ResultDTO.success("成功", task);
    }

    /**
     * 删除测试任务信息
     *
     * @param taskId       taskId主键
     * @param createUserId 创建人用户id
     * @return 返回接口task删除结果
     */
    @Override
    public ResultDTO<Task> delete(Integer taskId, Integer createUserId) {
        Task queryTask = new Task();
        queryTask.setId(taskId);
        queryTask.setCreateUserId(createUserId);

        Task result = taskDAO.selectOne(queryTask);
        // 如果为空，则提示，也可以直接返回成功
        if (Objects.isNull(result)) {
            return ResultDTO.fail("未查到测试任务信息");
        }
        taskDAO.deleteByPrimaryKey(taskId);
        return ResultDTO.success("成功");
    }

    /**
     * 修改测试任务信息
     *
     * @param task task信息
     * @return 返回接口task更新结果
     */
    @Override
    public ResultDTO<Task> update(Task task) {
        Task queryTask = new Task();
        queryTask.setId(task.getId());
        queryTask.setCreateUserId(task.getCreateUserId());

        Task result = taskDAO.selectOne(queryTask);
        //如果为空，则提示，也可以直接返回成功
        if (Objects.isNull(result)) {
            return ResultDTO.fail("未查到测试任务信息");
        }
        result.setUpdateTime(LocalDateTime.now())
                .setName(task.getName())
                .setJenkinsId(task.getJenkinsId())
                .setRemark(task.getRemark());
        int taskUpdate = taskDAO.updateByPrimaryKeySelective(result);
        Assert.isFalse(taskUpdate != 1, "更新测试任务失败！");
        return ResultDTO.success("成功");
    }

    /**
     * 根据id查询
     *
     * @param taskId       askId主键
     * @param createUserId 创建人用户id
     * @return 返回接口task查询结果
     */
    @Override
    public ResultDTO<Task> getById(Integer taskId, Integer createUserId) {
        Task queryTask = new Task();
        queryTask.setId(taskId);
        queryTask.setCreateUserId(createUserId);

        Task result = taskDAO.selectOne(queryTask);
        //如果为空，则提示，也可以直接返回成功
        if (Objects.isNull(result)) {
            ResultDTO.fail("未查到测试任务信息");
        }
        return ResultDTO.success("成功", result);
    }

    /**
     * 查询测试任务信息列表
     *
     * @param pageTableRequest 分页查询
     * @return 返回接口task分页查询结果
     */
    @Override
    public ResultDTO<PageTableResponse<Task>> list(PageTableRequest<QueryTaskListDTO> pageTableRequest) {
        QueryTaskListDTO params = pageTableRequest.getParams();
        Integer pageNum = pageTableRequest.getPageNum();
        Integer pageSize = pageTableRequest.getPageSize();
        //总数
        Integer recordsTotal = taskDAO.count(params);
        //分页查询数据
        List<Task> jenkinsList = taskDAO.list(params, (pageNum - 1) * pageSize, pageSize);
        PageTableResponse<Task> jenkinsPageTableResponse = new PageTableResponse<>();
        jenkinsPageTableResponse.setRecordsTotal(recordsTotal);
        jenkinsPageTableResponse.setData(jenkinsList);
        return ResultDTO.success("成功", jenkinsPageTableResponse);
    }

    /**
     * 开始执行测试任务信息
     *
     * @param tokenDto       token信息
     * @param requestInfoDto 接口请求信息
     * @param task           任务信息
     * @return 返回执行结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<User> startTask(TokenDTO tokenDto, RequestInfoDTO requestInfoDto, Task task) throws IOException {
        log.info("测试任务执行, 请求参数：{}, 测试任务请求参数：{}", JSONUtil.toJsonStr(requestInfoDto), JSONUtil.toJsonStr(task));
        // 参数校验和默认Jenkins是否有效
        if (Objects.isNull(task)) {
            return ResultDTO.fail("测试任务参数不能为空");
        }
        Task taskResult = taskDAO.selectOne(task);
        // 获取配置JenkinsId
        Integer jenkinsId = taskResult.getJenkinsId();
        if (Objects.isNull(jenkinsId)) {
            return ResultDTO.fail("未配置Jenkins");
        }
        Jenkins queryJenkins = new Jenkins();
        queryJenkins.setId(jenkinsId);
        queryJenkins.setCreateUserId(tokenDto.getUserId());
        Jenkins resultJenkins = jenkinsDAO.selectOne(queryJenkins);
        if (Objects.isNull(resultJenkins)) {
            return ResultDTO.fail("Jenkins不存在或已失效");
        }
        Task queryTask = new Task();
        queryTask.setId(task.getId());
        queryTask.setCreateUserId(task.getCreateUserId());
        Task resultTask = taskDAO.selectOne(queryTask);
        if (Objects.isNull(resultTask)) {
            String tips = "未查到测试任务";
            log.error(tips + ", 任务Id{}", resultTask.getId());
            return ResultDTO.fail(tips);
        }
        // 获取执行Jenkins执行测试命令
        String testCommandStr = task.getCommand();
        if (ObjectUtils.isEmpty(testCommandStr)) {
            testCommandStr = resultTask.getCommand();
        }
        if (ObjectUtils.isEmpty(testCommandStr)) {
            return ResultDTO.fail("任务的测试命令不能为空");
        }
        // 更新任务状态，任务状态：执行中
        resultTask.setStatus(Constants.STATUS_TWO);
        taskDAO.updateByPrimaryKeySelective(resultTask);

        // 添加保存测试任务接口拼装的mvn test 命令
        String testCommand = testCommandStr + " \n";
        // 更新执行测试任务状态
        StringBuilder updateStatusUrl = JenkinsUtils.getUpdateTaskStatusUrl(requestInfoDto, resultTask);
        // Jenkins构建参数组装
        Map<String, String> params = new HashMap<>(16);
        // 与resources/jenkins下xml的配置文件中的参数一致
        params.put("baseUrl", requestInfoDto.getBaseUrl());
        params.put("token", requestInfoDto.getToken());
        params.put("testCommand", testCommand);
        params.put("updateStatusData", updateStatusUrl.toString());
        log.info("执行测试Job的构建参数组装：{}", JSONUtil.toJsonStr(params));
        log.info("执行测试Job的修改任务状态的数据组装：{}", updateStatusUrl);

        // 构建执行Jenkins Job
        OperateJenkinsJobDTO operateJenkinsJobDto = new OperateJenkinsJobDTO();
        // tokenDto赋值
        operateJenkinsJobDto.setTokenDto(tokenDto);
        // 查询到Jenkins信息，进行赋值
        operateJenkinsJobDto.setJenkins(resultJenkins);
        // 传入Jenkins配置文件的构建参数
        operateJenkinsJobDto.setParams(params);

        // 调用Jenkins，操作Jenkins
        ResultDTO<User> resultDto = jenkinsClient.operateJenkinsJob(operateJenkinsJobDto, task.getId());
        // 此处抛出异常，阻止事务提交
        if (0 == resultDto.getResultCode()) {
            throw new ServiceException("Jenkins Job执行测试时异常:" + resultDto.getMessage());
        }
        return resultDto;
    }

    /**
     * 修改测试任务状态信息
     *
     * @param task task信息
     * @return 返回接口task修改状态结果
     */
    @Override
    public ResultDTO<Task> updateStatus(Task task) {
        Task queryTask = new Task();
        queryTask.setId(task.getId());
        queryTask.setCreateUserId(task.getCreateUserId());
        Task result = taskDAO.selectOne(queryTask);
        //如果为空，则提示
        if (Objects.isNull(result)) {
            return ResultDTO.fail("未查到测试任务信息");
        }
        //如果任务已经完成，则不重复修改
        if (Constants.STATUS_THREE.equals(result.getStatus())) {
            return ResultDTO.fail("测试任务已完成，无需修改");
        }
        result.setUpdateTime(LocalDateTime.now());
        //仅状态为已完成时修改
        if (Constants.STATUS_THREE.equals(task.getStatus())) {
            result.setBuildUrl(task.getBuildUrl());
            result.setStatus(Constants.STATUS_THREE);
            taskDAO.updateByPrimaryKey(result);
        }
        return ResultDTO.success("成功");
    }

    /**
     * 组装测试命令
     *
     * @param testCommand Jenkins测试命令
     * @param jenkins     jenkins测试信息
     * @param casesList   测试用例列表
     */
    private void makeTestCommand(StringBuilder testCommand, Jenkins jenkins, List<Cases> casesList) {
        //打印测试目录
        testCommand.append("pwd");
        testCommand.append("\n");
        if (Objects.isNull(jenkins)) {
            throw new ServiceException("组装测试命令时，Jenkins信息为空");
        }
        if (Objects.isNull(casesList) || casesList.isEmpty()) {
            throw new ServiceException("组装测试命令时，测试用例列表信息为空");
        }
        String runCommand = jenkins.getCommand();

        Integer commandRunCaseType = jenkins.getCommandRunCaseType();
        String systemTestCommand = jenkins.getCommand();

        if (ObjectUtils.isEmpty(systemTestCommand)) {
            throw new ServiceException("组装测试命令时，运行的测试命令信息为空");
        }
        //默认文本类型
        if (Objects.isNull(commandRunCaseType)) {
            commandRunCaseType = 1;
        }
        //文本类型
        if (commandRunCaseType == 1) {
            for (Cases cases : casesList) {
                //拼装命令前缀
                testCommand.append(systemTestCommand).append(" ");
                //拼装测试数据
                testCommand.append(cases.getCaseData()).append("\n");
            }
        }
        //文件类型 例如 hrun的yaml文件进行执行
        if (commandRunCaseType == 2) {
            String commandRunCaseSuffix = jenkins.getCommandRunCaseSuffix();
            if (ObjectUtils.isEmpty(commandRunCaseSuffix)) {
                throw new ServiceException("组装测试命令且case为文件时，测试用例后缀名不能为空");
            }
            for (Cases cases : casesList) {
                //拼装下载文件的curl命令
                makeCurlCommand(testCommand, cases, commandRunCaseSuffix);
                // 命令结束后 加换行
                testCommand.append("\n");
                //拼装命令前缀
                testCommand.append(systemTestCommand).append(" ");
                //平台测试用例名称
                testCommand.append(cases.getCaseName())
                        //拼装.分隔符
                        .append(".")
                        //拼装case文件后缀
                        .append(commandRunCaseSuffix)
                        .append(" || true")
                        .append("\n");
            }
        }
        log.info("Jenkins测试命令：{},执行测试命令：{} ", testCommand.toString(), runCommand);
        testCommand.append("\n");
    }

    /**
     * 拼装下载文件的curl命令
     *
     * @param testCommand          Jenkins执行命令
     * @param cases                测试用例
     * @param commandRunCaseSuffix 执行配置文件后缀
     */
    private void makeCurlCommand(StringBuilder testCommand, Cases cases, String commandRunCaseSuffix) {
        //通过curl命令获取测试数据并保存为文件
        testCommand.append("curl ")
                .append("-o ");
        String caseName = cases.getCaseName();
        if (ObjectUtils.isEmpty(caseName)) {
            caseName = "测试用例无测试名称";
        }
        testCommand.append(caseName)
                .append(".")
                .append(commandRunCaseSuffix)
                .append(" ${baseUrl}/testCase/data/")
                .append(cases.getId())
                .append(" -H \"token: ${token}\" ");
        //本行命令执行失败，继续运行下面的命令行
        testCommand.append(" || true");
        testCommand.append("\n");
    }
}
