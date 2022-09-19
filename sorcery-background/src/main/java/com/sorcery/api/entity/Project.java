package com.sorcery.api.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * 项目管理实体类
 *
 * @author jinglv
 * @date 2022/9/19 14:42
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Table(name = "t_project")
public class Project extends BaseEntityNew {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 项目名
     */
    @Column(name = "project_name")
    private String projectName;
    /**
     * git工程名称
     */
    @Column(name = "git_name")
    private String gitName;
    /**
     * git工程地址
     */
    @Column(name = "git_address")
    private String gitAddress;
    /**
     * git认证id
     */
    @Column(name = "git_credentials_id")
    private String gitCredentialsId;
    /**
     * 项目描述
     */
    private String describe;
    /**
     * 项目图片
     */
    private String image;
    /**
     * 删除标志 0 未删除 1 已删除
     */
    @Column(name = "del_flag")
    private Integer delFlag;
    /**
     * 创建人id
     */
    @Column(name = "create_user_id")
    private Integer createUserId;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;
    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;
}
