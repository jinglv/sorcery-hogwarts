package com.sorcery.api.dto.report;

import com.sorcery.api.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Allure测试报告对象
 *
 * @author jingLv
 * @date 2021/01/19
 */
@Accessors(chain = true)
@ApiModel(value = "Allure测试报告对象")
@EqualsAndHashCode(callSuper = true)
@Data
public class AllureReportDTO extends BaseDTO {
    /**
     * 任务id
     */
    private Integer taskId;
    /**
     * Allure测试报告Url
     */
    private String allureReportUrl;
}
