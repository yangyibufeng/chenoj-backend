package com.yybf.chenoj.judge.codesandbox.impl;

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

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        return null;
    }
}