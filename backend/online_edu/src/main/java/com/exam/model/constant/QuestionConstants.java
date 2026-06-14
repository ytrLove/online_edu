package com.exam.model.constant;

/**
 * 题库和考试相关常量
 */
public class QuestionConstants {
    
    /**
     * 正常状态
     */
    public static final Integer STATUS_NORMAL = 1;
    
    /**
     * 停用状态
     */
    public static final Integer STATUS_DISABLE = 0;
    
    /**
     * 删除标志 - 正常
     */
    public static final Integer DEL_FLAG_NORMAL = 0;
    
    /**
     * 删除标志 - 已删除
     */
    public static final Integer DEL_FLAG_DELETED = 1;
    
    /**
     * 考试状态 - 未开始
     */
    public static final Integer EXAM_STATUS_NOT_STARTED = 0;
    
    /**
     * 考试状态 - 进行中
     */
    public static final Integer EXAM_STATUS_IN_PROGRESS = 1;
    
    /**
     * 考试状态 - 已结束
     */
    public static final Integer EXAM_STATUS_ENDED = 2;
    
    /**
     * 提交状态 - 未提交
     */
    public static final Integer EXAM_SUBMIT_NOT_SUBMITTED = 0;
    
    /**
     * 提交状态 - 已提交
     */
    public static final Integer EXAM_SUBMIT_COMPLETED = 1;
    
    /**
     * 批改状态 - 未批改
     */
    public static final Integer EXAM_GRADE_NOT_GRADED = 0;
    
    /**
     * 批改状态 - 已批改
     */
    public static final Integer EXAM_GRADE_COMPLETED = 1;
    
    /**
     * 批改状态 - 自动批改
     */
    public static final Integer EXAM_GRADE_AUTO = 2;
    
    /**
     * 题目类型 - 选择题
     */
    public static final Integer QUESTION_TYPE_CHOICE = 1;
    
    /**
     * 题目类型 - 多选题
     */
    public static final Integer QUESTION_TYPE_MULTIPLE_CHOICE = 2;
    
    /**
     * 题目类型 - 判断题
     */
    public static final Integer QUESTION_TYPE_JUDGE = 3;
    
    /**
     * 题目类型 - 填空题
     */
    public static final Integer QUESTION_TYPE_FILL_BLANK = 4;
    
    /**
     * 题目类型 - 简答题
     */
    public static final Integer QUESTION_TYPE_SHORT_ANSWER = 5;
    
    /**
     * 题目类型 - 编程题
     */
    public static final Integer QUESTION_TYPE_PROGRAMMING = 6;
} 