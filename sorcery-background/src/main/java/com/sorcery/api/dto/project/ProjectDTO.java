package com.sorcery.api.dto.project;

import com.sorcery.api.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author jinglv
 * @date 2022/9/19 15:08
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "添加项目对象")
@Data
public class ProjectDTO extends BaseDTO {

    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称", required = true)
    private String projectName;
    /**
     * git项目名称
     */
    @ApiModelProperty(value = "git项目名称", required = true)
    private String gitName;
    /**
     * git项目地址
     */
    @ApiModelProperty(value = "git项目地址", required = true)
    private String gitAddress;
    /**
     * git项目认证id
     */
    @ApiModelProperty(value = "git项目认证id", required = true)
    private String gitCredentialsId;
    /**
     * 项目描述
     */
    @ApiModelProperty(value = "项目描述")
    private String description;
}
