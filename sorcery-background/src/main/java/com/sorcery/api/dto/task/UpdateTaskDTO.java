package com.sorcery.api.dto.task;

import com.sorcery.api.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修改任务对象
 *
 * @author jingLv
 * @date 2021/01/26
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "修改任务对象")
@Data
public class UpdateTaskDTO extends BaseDTO {
    /**
     * ID
     */
    @ApiModelProperty(value = "任务主键", required = true)
    private Integer id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "任务名称", required = true)
    private String name;

    /**
     * jenkinsId
     */
    @ApiModelProperty(value = "jenkinsId", required = true)
    private Integer jenkinsId;

    /**
     * 备注
     */
    @ApiModelProperty(value = "任务备注")
    private String remark;
}
