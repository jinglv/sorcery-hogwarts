package com.sorcery.api.service;

import com.sorcery.api.dto.ResultDTO;
import com.sorcery.api.dto.cases.TaskCaseRelDetailDTO;
import com.sorcery.api.dto.cases.TaskCaseRelListDTO;
import com.sorcery.api.dto.page.PageTableRequest;
import com.sorcery.api.dto.page.PageTableResponse;

/**
 * 测试任务与测试用例关联服务层
 *
 * @author jingLv
 * @date 2021/01/19
 */
public interface TaskCaseRelService {
    /**
     * 查询任务关联的详细信息列表
     *
     * @param pageTableRequest 分页查询
     * @return 返回接口测试任务详细信息
     */
    ResultDTO<PageTableResponse<TaskCaseRelDetailDTO>> listDetail(PageTableRequest<TaskCaseRelListDTO> pageTableRequest);

}
