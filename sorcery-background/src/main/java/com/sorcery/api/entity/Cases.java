package com.sorcery.api.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 测试用例实体
 *
 * @author jinglv
 * @date 2021/01/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Table(name = "t_case")
public class Cases extends BaseEntityNew {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 用例名称
     */
    @Column(name = "case_name")
    private String caseName;
    /**
     * 测试数据
     */
    @Column(name = "case_data")
    private String caseData;
    /**
     * 备注
     */
    private String remark;
    /**
     * 项目id
     */
    @Column(name = "project_id")
    private Integer projectId;
    /**
     * 项目名称
     */
    @Transient
    private String projectName;
    /**
     * 删除标志 1 未删除 0 已删除
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
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}