package com.yybf.chenoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yybf.chenoj.annotation.AuthCheck;
import com.yybf.chenoj.common.BaseResponse;
import com.yybf.chenoj.common.ErrorCode;
import com.yybf.chenoj.common.ResultUtils;
import com.yybf.chenoj.constant.UserConstant;
import com.yybf.chenoj.exception.BusinessException;
import com.yybf.chenoj.model.dto.question.QuestionQueryRequest;
import com.yybf.chenoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.yybf.chenoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.yybf.chenoj.model.entity.Question;
import com.yybf.chenoj.model.entity.QuestionSubmit;
import com.yybf.chenoj.model.entity.User;
import com.yybf.chenoj.model.vo.QuestionSubmitVO;
import com.yybf.chenoj.service.QuestionSubmitService;
import com.yybf.chenoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目提交接口
 *
 * @author 杨毅不逢
 */
@RestController
@RequestMapping("/question_submit")
@Slf4j
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return resultNum 题目提交ID，用户可以根据题目提交id去查询判题状态等
     */
    @PostMapping("/")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                         HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }

    /**
     * 分页获取提交列表（除管理员外，其余用户只能看到除代码、答案等之外的公开信息）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @PostMapping("/list/page")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                         HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent(); //当前所请求的分页的页码（当前页)
        long size = questionSubmitQueryRequest.getPageSize(); //这个变量表示每页包含的条目数量，即分页的大小
        // 从数据库中获取到了原始的题目提交信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        // 获取登录信息，将信息直接传递给service，减少http请求
        final User loginUser = userService.getLoginUser(request);
        // 返回脱敏信息 传入request是为了可以通过用户状态来判断脱敏层级
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage,loginUser));
    }
}
