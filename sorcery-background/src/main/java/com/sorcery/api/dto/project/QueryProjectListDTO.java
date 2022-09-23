package com.sorcery.api.dto.project;

import com.sorcery.api.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author jinglv
 * @date 2022/9/19 15:25
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "查询项目信息列表对象")
@Data
public class QueryProjectListDTO extends BaseDTO {
    @ApiModelProperty(value = "项目名称")
    private String projectName;
    @ApiModelProperty(value = "git工程名称")
    private String gitName;
    @ApiModelProperty(value = "git工程地址")
    private String gitAddress;
    @ApiModelProperty(value = "git认证id")
    private String gitCredentialsId;
    @ApiModelProperty(value = "项目描述")
    private String description;
    @ApiModelProperty(value = "项目图片")
    private String image;
    @ApiModelProperty(value = "创建者id(客户端传值无效，以token数据为准)")
    private Integer createUserId;
}
