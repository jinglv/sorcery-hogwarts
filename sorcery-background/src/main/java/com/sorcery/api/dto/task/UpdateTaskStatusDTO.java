package com.sorcery.api.dto.task;

import com.sorcery.api.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修改任务状态对象
 *
 * @author jingLv
 * @date 2021/01/26
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "修改任务状态对象")
@Data
public class UpdateTaskStatusDTO extends BaseDTO {
    /**
     * 任务主键
     */
    @ApiModelProperty(value = "任务主键", required = true)
    private Integer taskId;

    /**
     * 构建地址
     */
    @ApiModelProperty(value = "构建地址", required = true)
    private String buildUrl;

    /**
     * 测试任务状态码
     */
    @ApiModelProperty(value = "任务状态码", required = true)
    private Integer status;
}
