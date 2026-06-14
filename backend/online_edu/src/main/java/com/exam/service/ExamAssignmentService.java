package com.exam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.model.entity.ExamAssignment;
import com.exam.model.param.AssignmentParam;
import com.exam.model.param.AssignmentQuestionParam;
import com.exam.model.vo.AssignmentVO;

/**
 * 作业服务接口
 */
public interface ExamAssignmentService extends IService<ExamAssignment> {
    
    /**
     * 分页查询作业列表
     *
     * @param pageNum 当前页
     * @param pageSize 每页显示条数
     * @param assignment 查询条件
     * @return 分页结果
     */
    IPage<ExamAssignment> selectAssignmentPage(Integer pageNum, Integer pageSize, ExamAssignment assignment);
    
    /**
     * 查询作业详情
     *
     * @param assignmentId 作业ID
     * @return 作业详情
     */
    ExamAssignment selectAssignmentById(Long assignmentId);
    
    /**
     * 查询作业详情（返回VO对象）
     *
     * @param assignmentId 作业ID
     * @return 作业详情VO
     */
    AssignmentVO selectAssignmentVOById(Long assignmentId);
    
    /**
     * 查询学生的作业列表
     *
     * @param pageNum 当前页
     * @param pageSize 每页显示条数
     * @param studentId 学生ID
     * @param assignment 查询条件
     * @return 分页结果
     */
    IPage<ExamAssignment> selectStudentAssignmentPage(Integer pageNum, Integer pageSize, Long studentId, ExamAssignment assignment);
    
    /**
     * 查询学生的作业详情（包含提交信息）
     *
     * @param assignmentId 作业ID
     * @param studentId 学生ID
     * @return 作业详情VO
     */
    AssignmentVO selectStudentAssignmentVOById(Long assignmentId, Long studentId);
    
    /**
     * 新增作业
     *
     * @param assignmentParam 作业信息
     * @return 结果
     */
    boolean insertAssignment(AssignmentParam assignmentParam);
    
    /**
     * 修改作业
     *
     * @param assignmentParam 作业信息
     * @return 结果
     */
    boolean updateAssignment(AssignmentParam assignmentParam);
    
    /**
     * 删除作业
     *
     * @param assignmentId 作业ID
     * @return 结果
     */
    boolean deleteAssignmentById(Long assignmentId);
    
    /**
     * 批量删除作业
     *
     * @param assignmentIds 需要删除的作业ID数组
     * @return 结果
     */
    boolean deleteAssignmentByIds(Long[] assignmentIds);
    
    /**
     * 关联题目到作业
     *
     * @param param 题目关联参数
     * @return 结果
     */
    boolean associateQuestions(AssignmentQuestionParam param);
} 