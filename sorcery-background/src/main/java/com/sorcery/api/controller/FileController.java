package com.sorcery.api.controller;

import com.sorcery.api.common.utils.FileUtils;
import com.sorcery.api.dto.ResultDTO;
import com.sorcery.api.dto.TokenDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理
 *
 * @author jinglv
 * @date 2022/9/16 16:47
 */
@Slf4j
@Api(tags = "文件管理")
@RestController
@RequestMapping("/file")
public class FileController {

    private final FileUtils fileUtils;

    public FileController(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    /**
     * 文件上传接口
     *
     * @param file 上传文件
     * @return 返回请求接口结果
     */
    @ApiOperation(value = "文件上传", notes = "文件上传接口")
    @PostMapping("uploads")
    public ResultDTO<TokenDTO> uploads(@RequestParam("file") MultipartFile file) {
        String url = fileUtils.uploads(file);
        return ResultDTO.success(url);
    }
}
