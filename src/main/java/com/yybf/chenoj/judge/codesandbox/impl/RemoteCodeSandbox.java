package com.yybf.chenoj.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.util.StringUtils;
import com.yybf.chenoj.common.ErrorCode;
import com.yybf.chenoj.exception.BusinessException;
import com.yybf.chenoj.judge.codesandbox.CodeSandbox;
import com.yybf.chenoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yybf.chenoj.judge.codesandbox.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yangyibufeng
 * @Description 远程代码沙箱（远程调用，真正要实现的接口）
 * @date 2024/2/6
 */
@Slf4j
public class RemoteCodeSandbox implements CodeSandbox {

    // 定义一个鉴权请求头和密钥，用于通过代码沙箱鉴权
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey1";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");

        String url = "http://localhost:8080/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER,AUTH_REQUEST_SECRET) // 鉴权
                .body(json)
                .execute()
                .body();
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "接口调用失败，message：" + responseStr);
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}