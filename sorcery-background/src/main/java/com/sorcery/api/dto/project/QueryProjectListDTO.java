package com.sorcery.api.dto.project;

import com.sorcery.api.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询项目信息列表对象
 *
 * @author jinglv
 * @date 2022/9/19 15:25
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "查询项目信息列表对象")
@Data
public class QueryProjectListDTO extends BaseDTO {
    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    /**
     * git工程名称
     */
    @ApiModelProperty(value = "git工程名称")
    private String gitName;

    /**
     * git工程地址
     */
    @ApiModelProperty(value = "git工程地址")
    private String gitAddress;

    /**
     * git认证id
     */
    @ApiModelProperty(value = "git认证id")
    private String gitCredentialsId;

    /**
     * 项目描述
     */
    @ApiModelProperty(value = "项目描述")
    private String description;

    /**
     * 项目图片
     */
    @ApiModelProperty(value = "项目图片")
    private String image;

    /**
     * 创建者id(客户端传值无效，以token数据为准)
     */
    @ApiModelProperty(value = "创建者id(客户端传值无效，以token数据为准)")
    private Integer createUserId;
}
