package com.sorcery.api.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 测试任务实体
 *
 * @author lvjing
 * @date 2022/9/19 14:42
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
@Table(name = "t_task")
public class Task extends BaseEntityNew {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 名称
     */
    @Column(name = "task_name")
    private String name;
    /**
     * 运行测试的Jenkins服务器id
     */
    @Column(name = "jenkins_id")
    private Integer jenkinsId;
    /**
     * 运行测试的Jenkins服务器id
     */
    @Transient
    private String jenkinsName;
    /**
     * 运行测试的Jenkins构建Job
     */
    @Column(name = "jenkins_job_name")
    private String jenkinsJobName;
    /**
     * Jenkins的构建url
     */
    @Column(name = "build_url")
    private String buildUrl;
    /**
     * 创建人id
     */
    @Column(name = "create_user_id")
    private Integer createUserId;
    /**
     * 用例数量
     */
    @Column(name = "case_count")
    private Integer caseCount;
    /**
     * 备注
     */
    private String remark;
    /**
     * 任务类型 1 执行测试任务 2 一键执行测试的任务
     */
    @Column(name = "task_type")
    private Integer taskType;
    /**
     * 状态 0 无效 1 新建 2 执行中 3 执行完成
     */
    private Integer status;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    /**
     * Jenkins执行测试时的命令脚本
     */
    private String command;
}