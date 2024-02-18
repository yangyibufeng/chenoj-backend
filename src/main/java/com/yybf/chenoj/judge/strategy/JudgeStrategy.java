package com.yybf.chenoj.judge.strategy;

import com.yybf.chenoj.model.dto.questionsubmit.JudgeInfo;

/**
 * @author yangyibufeng
 * @Description 判题策略（比如在语言为java的时候时间和内存都要相应改变）
 * @date 2024/2/16-13:35
 */
public interface JudgeStrategy {
    /**
     * @param judgeContext:
     * @return com.yybf.chenoj.model.dto.questionsubmit.JudgeInfo:
     * @author yangyibufeng
     * @description 执行判题
     * @date 2024/2/16 13:42
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
