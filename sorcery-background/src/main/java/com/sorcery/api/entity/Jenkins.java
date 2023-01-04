package com.sorcery.api.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Jenkins实体
 *
 * @author jinglv
 * @date 2020/01/22
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_jenkins")
public class Jenkins extends BaseEntityNew {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 名称
     */
    @Column(name = "jenkins_name")
    private String name;
    /**
     * 测试命令
     */
    private String command;
    /**
     * Jenkins的baseUrl
     */
    @Column(name = "jenkins_url")
    private String url;
    /**
     * Jenkins登录用户名
     */
    @Column(name = "jenkins_username")
    private String jenkinsUsername;
    /**
     * Jenkins登录密码
     */
    @Column(name = "jenkins_password")
    private String jenkinsPassword;
    /**
     * 创建人id
     */
    @Column(name = "create_user_id")
    private Integer createUserId;
    /**
     * 命令运行的测试用例类型  1 文本 2 文件
     */
    @Column(name = "command_run_case_type")
    private Integer commandRunCaseType;
    /**
     * 备注
     */
    private String remark;
    /**
     * 测试用例后缀名 如果case为文件时，此处必填
     */
    @Column(name = "command_run_case_suffix")
    private String commandRunCaseSuffix;
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
}