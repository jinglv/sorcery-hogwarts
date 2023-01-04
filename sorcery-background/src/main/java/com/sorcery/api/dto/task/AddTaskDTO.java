package com.sorcery.api.dto.task;

import com.sorcery.api.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 添加任务对象
 *
 * @author jingLv
 * @date 2021/01/19
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "添加任务对象")
@Data
public class AddTaskDTO extends BaseDTO {
    /**
     * 名称
     */
    @ApiModelProperty(value = "任务名称", required = true)
    private String name;
    /**
     * 运行测试的Jenkins服务器id
     */
    @ApiModelProperty(value = "运行测试的Jenkins服务器id", required = true)
    private Integer jenkinsId;
    /**
     * 备注
     */
    @ApiModelProperty(value = "任务备注")
    private String remark;
    /**
     * 创建人id
     */
    @ApiModelProperty(value = "创建者id(客户端传值无效，以token数据为准)")
    private Integer createUserId;
}
