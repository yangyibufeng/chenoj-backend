package com.yybf.chenoj.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yybf.chenoj.common.ErrorCode;
import com.yybf.chenoj.constant.CommonConstant;
import com.yybf.chenoj.exception.BusinessException;
import com.yybf.chenoj.judge.JudgeService;
import com.yybf.chenoj.mapper.QuestionSubmitMapper;
import com.yybf.chenoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.yybf.chenoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.yybf.chenoj.model.entity.Question;
import com.yybf.chenoj.model.entity.QuestionSubmit;
import com.yybf.chenoj.model.entity.User;
import com.yybf.chenoj.model.enums.QuestionSubmitLanguageEnum;
import com.yybf.chenoj.model.enums.QuestionSubmitStatusEnum;
import com.yybf.chenoj.model.vo.QuestionSubmitVO;
import com.yybf.chenoj.service.QuestionService;
import com.yybf.chenoj.service.QuestionSubmitService;
import com.yybf.chenoj.service.UserService;
import com.yybf.chenoj.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Lenovo
 * @description 针对表【question_submit(提交题目)】的数据库操作Service实现
 * @createDate 2024-01-28 22:19:52
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {
    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    @Lazy // 设置懒加载，因为QuestionSubmitServiceImpl与JudgeServiceImpl相互依赖
    private JudgeService judgeService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 判断语言是都合法
        String language = questionSubmitAddRequest.getLanguage();
        // 通过value获取到相应的枚举值
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言异常");
        }

        long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 从题目提交信息中获取相应的数据
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        // 设置题目初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目数据插入失败");
        }
        long questionSubmitId = questionSubmit.getId();
        // todo 执行判题服务
        CompletableFuture.runAsync(()->{
            judgeService.doJudge(questionSubmitId);
        });


        return questionSubmitId;
    }

    /**
     * 获取查询包装类 （本质是用户根据那些字段进行查询，根据前端传来的请求对象，得到mybatis框架支持的查询类-QueryWrapper）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();

        String sortField = questionSubmitQueryRequest.getSortField(); // 获取排序字段
        String sortOrder = questionSubmitQueryRequest.getSortOrder(); // 获取排序顺序


        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status); // 当查询的状态不为空
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    /**
     * @param questionSubmit:
     * @param loginUser:      直接传入登录用户信息，减少http请求用户信息
     * @return com.yybf.chenoj.model.vo.QuestionSubmitVO:
     * @author yangyibufeng
     * @description 从后端获取到脱敏的用户信息（根据问题得到问题的包装类）
     * @date 2024/1/29 12:16
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);

        // 脱敏：除管理员外，其余用户只能看到除代码等之外的公开信息
//        User loginUser = userService.getLoginUser(request);
        long loginUserId = loginUser.getId();

        if (loginUserId != questionSubmit.getUserId() && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    /**
     * @param questionSubmitPage: *@param request:
     * @param loginUser:          直接传入登录用户信息，减少http请求用户信息
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.yybf.chenoj.model.vo.QuestionSubmitVO>:
     * @author yangyibufeng
     * @description 相当于循环遍历上面的方法--根据问题分页得到问题分页的包装类
     * @date 2024/1/29 12:17
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(
                questionSubmitPage.getCurrent(),
                questionSubmitPage.getSize(),
                questionSubmitPage.getTotal());
        if (CollUtil.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser)
                ).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }


}




