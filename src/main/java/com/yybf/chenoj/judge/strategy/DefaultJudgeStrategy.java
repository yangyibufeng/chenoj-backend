package com.yybf.chenoj.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.yybf.chenoj.model.dto.question.JudgeCase;
import com.yybf.chenoj.model.dto.question.JudgeConfig;
import com.yybf.chenoj.judge.codesandbox.model.JudgeInfo;
import com.yybf.chenoj.model.entity.Question;
import com.yybf.chenoj.model.enums.JudgeInfoMessageEnum;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * @author yangyibufeng
 * 执行默认的判题逻辑
 * @date 2024/2/16
 */
public class DefaultJudgeStrategy implements JudgeStrategy {

    /**
     * @param judgeContext:
     * @return com.yybf.chenoj.judge.codesandbox.model.JudgeInfo:
     * @author yangyibufeng
     * @description 执行判题
     * @date 2024/2/16 14:07
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {

        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Question question = judgeContext.getQuestion();

        // 根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeInfoMessageEnum judgeinfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;

        // 获取执行题目所消耗的资源数据
        Long usingMemory = judgeInfo.getMemory();
        Long usingTime = judgeInfo.getTime();

        // 定义返回值
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setMessage(judgeinfoMessageEnum.getValue());
        judgeInfoResponse.setMemory(usingMemory);
        judgeInfoResponse.setTime(usingTime);

        // 判断题目限制
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long memoryLimit = judgeConfig.getMemoryLimit();
        Long timeLimit = judgeConfig.getTimeLimit();
        if (usingMemory > memoryLimit) {
            return setJudgeResult(judgeInfoResponse, JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED);
        }
        if (usingTime > timeLimit) {
            return setJudgeResult(judgeInfoResponse, JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED);
        }

        // 沙箱输出的数据数量和预期的数量是否相等
        if (outputList.size() != inputList.size()) {
            return setJudgeResult(judgeInfoResponse, JudgeInfoMessageEnum.WRONG_ANSWER);
        }
        // 依次判断每个输出是否符合预期
        for (int i = 0; i < outputList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (!Objects.equals(judgeCase.getOutput(), outputList.get(i))) {
                return setJudgeResult(judgeInfoResponse, JudgeInfoMessageEnum.WRONG_ANSWER);
            }
        }


        return judgeInfoResponse;
    }


    /**
     * @param judgeInfoResponse:    返回判题信息类
     * @param judgeInfoMessageEnum: 表示判题机的状态
     * @return com.yybf.chenoj.judge.codesandbox.model.JudgeInfo:
     * @author yangyibufeng
     * @description 将对应的枚举值装配到返回值里面
     * @date 2024/2/16 14:03
     */
    @NotNull
    private JudgeInfo setJudgeResult(JudgeInfo judgeInfoResponse,
                                     JudgeInfoMessageEnum judgeInfoMessageEnum) {
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }
}