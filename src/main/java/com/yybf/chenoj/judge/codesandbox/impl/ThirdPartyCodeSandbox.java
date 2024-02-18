package com.yybf.chenoj.judge.codesandbox.impl;

import com.yybf.chenoj.judge.codesandbox.CodeSandbox;
import com.yybf.chenoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yybf.chenoj.judge.codesandbox.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yangyibufeng
 * @Description 第三方代码沙箱（为了方便接入第三方的代码沙箱）
 * @date 2024/2/6
 */
@Slf4j
public class ThirdPartyCodeSandbox implements CodeSandbox {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}