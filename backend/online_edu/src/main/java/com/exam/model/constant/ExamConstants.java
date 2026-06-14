package com.exam.model.constant;

/**
 * 作业和考试常量类
 */
public class ExamConstants {
    
    /**
     * 作业状态：已提交
     */
    public static final String SUBMISSION_STATUS_SUBMITTED = "submitted";
    
    /**
     * 作业状态：评分中
     */
    public static final String SUBMISSION_STATUS_GRADING = "grading";
    
    /**
     * 作业状态：已评分
     */
    public static final String SUBMISSION_STATUS_GRADED = "graded";
    
    /**
     * 考试状态：未开始
     */
    public static final String EXAM_STATUS_NOT_STARTED = "not_started";
    
    /**
     * 考试状态：进行中
     */
    public static final String EXAM_STATUS_IN_PROGRESS = "in_progress";
    
    /**
     * 考试状态：已提交
     */
    public static final String EXAM_STATUS_SUBMITTED = "submitted";
    
    /**
     * 考试状态：已评分
     */
    public static final String EXAM_STATUS_GRADED = "graded";
    
    /**
     * 状态：正常
     */
    public static final Integer STATUS_NORMAL = 1;
    
    /**
     * 状态：停用
     */
    public static final Integer STATUS_DISABLED = 0;
    
    /**
     * 删除标志：存在
     */
    public static final Integer DEL_FLAG_NORMAL = 0;
    
    /**
     * 删除标志：删除
     */
    public static final Integer DEL_FLAG_DELETE = 1;
} 