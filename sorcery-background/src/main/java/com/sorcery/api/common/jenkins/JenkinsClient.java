package com.sorcery.api.common.jenkins;

import cn.hutool.json.JSONUtil;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.QueueReference;
import com.sorcery.api.common.exception.ServiceException;
import com.sorcery.api.dao.JenkinsDAO;
import com.sorcery.api.dao.TaskDAO;
import com.sorcery.api.dto.ResultDTO;
import com.sorcery.api.dto.TokenDTO;
import com.sorcery.api.dto.jenkins.OperateJenkinsJobDTO;
import com.sorcery.api.entity.Jenkins;
import com.sorcery.api.entity.Task;
import com.sorcery.api.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * @author jingLv
 * @date 2021/01/25
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JenkinsClient {
    private final JenkinsDAO jenkinsDAO;
    private final JenkinsFactory jenkinsFactory;
    private final TaskDAO taskDAO;

    private final String PREFIX_TIPS = "调用Jenkins异常-";

    /**
     * 操作Jenkins中的job
     *
     * @param operateJenkinsJobDto 操作Jenkins job信息
     * @param taskId               测试任务Id
     * @return 返回执行结果
     */
    public ResultDTO<User> operateJenkinsJob(OperateJenkinsJobDTO operateJenkinsJobDto, Integer taskId) throws IOException {
        log.info("执行Jenkins的Job信息：{}", JSONUtil.toJsonStr(operateJenkinsJobDto));
        // operateJenkinsJobDto中获取Jenkins信息
        Jenkins jenkins = operateJenkinsJobDto.getJenkins();
        // 获取token信息
        TokenDTO tokenDto = operateJenkinsJobDto.getTokenDto();
        // 获取传入构建Job的参数
        Map<String, String> params = operateJenkinsJobDto.getParams();
        // 拼接Job名称和Job标识
        String jobName = JenkinsUtils.getStartTestJobName(tokenDto.getUserId());
        String jobSign = JenkinsUtils.getJobSignByName(jobName);
        log.info("已拼接的Job名称：{}", jobName);
        log.info("已拼接的Job标识：{}", jobSign);
        if (ObjectUtils.isEmpty(jobSign)) {
            return ResultDTO.fail("Jenkins的Job标识不符合规范");
        }
        // 获取ClassPATH下的（resources/jenkins/）下的job配置文件模板
        ClassPathResource resource = new ClassPathResource("jenkins/" + jobSign + ".xml");
        byte[] jenkinsData = FileCopyUtils.copyToByteArray(resource.getInputStream());
        String jobXml = new String(jenkinsData, StandardCharsets.UTF_8);
        log.info("解析Jenkins配置文件内容:{}", jobXml);
        if (ObjectUtils.isEmpty(jobXml)) {
            return ResultDTO.fail("Job配置信息不能为空");
        }
        Jenkins queryJenkins = new Jenkins();
        queryJenkins.setId(operateJenkinsJobDto.getJenkins().getId());
        Jenkins resultResult = jenkinsDAO.selectOne(queryJenkins);
        Integer jenkinsId = resultResult.getId();
        // 查询测试任务的JenkinsJobName
        Task queryTask = new Task();
        queryTask.setId(taskId);
        Task task = taskDAO.selectOne(queryTask);
        if (!Objects.equals(task.getJenkinsId(), jenkinsId)) {
            throw new ServiceException("测试任务taskId：{" + taskId + "}, Jenkins信息错误!");
        }
        log.info("任务Id,{}", task.getId());
        String jenkinsJobName = task.getJenkinsJobName();
        if (ObjectUtils.isEmpty(jenkinsJobName)) {
            log.info("新建Jenkins执行测试的Job");
            // 获取jenkinsServer，创建Job
            createOrUpdateJob(jobName, jobXml, tokenDto.getUserId(), jenkinsId, 1);
            // 创建Jenkins Job Name保存数据库
            // Task updateTask = new Task();
            task.setJenkinsJobName(jobName)
                    .setUpdateTime(LocalDateTime.now());
            taskDAO.updateByPrimaryKey(task);
        } else {
            log.info("更新Jenkins执行测试的Job");
            // 获取jenkinsServer，更新Job
            createOrUpdateJob(jobName, jobXml, tokenDto.getUserId(), jenkinsId, 0);
        }
        try {
            // 获取JenkinsHttpClient
            JenkinsHttpClient jenkinsHttpClient = jenkinsFactory.getJenkinsHttpClient(tokenDto.getUserId(), jenkinsId);
            // 获取对应的Job，并进行构建
            Job job = getJob(jobName, jenkinsHttpClient, jenkins.getUrl());
            build(job, params);
            return ResultDTO.success("成功");
        } catch (Exception e) {
            String tips = PREFIX_TIPS + "操作Jenkins的Job异常" + e.getMessage();
            log.error(tips, e);
            throw new ServiceException(tips);
        }
    }

    /**
     * 创建或更新Jenkins的Job
     *
     * @param jobName       job名称
     * @param jobXml        job xml配置文件
     * @param createUserId  创建人用户id
     * @param jenkinsId     Jenkins主键id
     * @param createJobFlag 1 是 0 否
     */
    public void createOrUpdateJob(String jobName, String jobXml, Integer createUserId, Integer jenkinsId, Integer createJobFlag) {
        JenkinsServer jenkinsServer = jenkinsFactory.getJenkinsServer(createUserId, jenkinsId);
        // 根据jenkinsId查询Jenkins信息
        Jenkins queryJenkins = new Jenkins();
        queryJenkins.setId(jenkinsId);
        Jenkins jenkins = jenkinsDAO.selectOne(queryJenkins);
        try {
            if (Objects.nonNull(jenkins) && createJobFlag == 1) {
                // Job不存在则创建Job
                jenkinsServer.createJob(null, jobName, jobXml, true);
            } else {
                // Job存在则更新Job
                jenkinsServer.updateJob(null, jobName, jobXml, true);
            }
            ResultDTO.success("成功");
        } catch (Exception e) {
            String tips = PREFIX_TIPS + "创建或更新Jenkins的Job时异常" + e.getMessage();
            log.error(tips, e);
            throw new ServiceException(tips);
        }
    }

    /**
     * 根据URL获取Jenkins Job
     *
     * @param jobName           Jenkins Job名称
     * @param jenkinsHttpClient jenkinsHttpClient
     * @param jenkinsBaseUrl    Jenkins基础Url
     * @return 返回Job
     */
    private Job getJob(String jobName, JenkinsHttpClient jenkinsHttpClient, String jenkinsBaseUrl) {
        // http://{ip:port}/job/{jobName}/
        Job job = new Job(jobName, jenkinsBaseUrl + "job/" + jobName + "/");
        job.setClient(jenkinsHttpClient);
        return job;
    }


    /**
     * 构建无参Jenkins Job
     *
     * @param job Jenkins Job
     * @return QueueReference
     * @throws IOException 抛出io异常
     */
    private QueueReference build(Job job) throws IOException {
        return build(job, null);
    }

    /**
     * 构建带参Jenkins Job
     *
     * @param job    Jenkins Job
     * @param params Job需要的参数
     * @return QueueReference
     * @throws IOException 抛出io异常
     */
    private QueueReference build(Job job, Map<String, String> params) throws IOException {
        QueueReference queueReference;
        if (Objects.isNull(params)) {
            queueReference = job.build(true);
        } else {
            queueReference = job.build(params, true);
        }
        log.info("构建带参Jenkins Job：{}", JSONUtil.toJsonStr(queueReference));
        return queueReference;
    }
}
