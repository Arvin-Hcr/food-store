package com.hcr.exception;

import com.hcr.utils.JSONResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 捕获异常助手类
 */
@RestControllerAdvice
public class CustomExceptionHandler {

    //上传文件超过500K，捕获异常：MaxUploadSizeExceededException
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public JSONResult handleMaxUploadFile(MaxUploadSizeExceededException exceededException){
        return JSONResult.errorMsg("文件上传大小不能超过500k，请压缩图片或者降低图片质量再上传！");
    }
}
