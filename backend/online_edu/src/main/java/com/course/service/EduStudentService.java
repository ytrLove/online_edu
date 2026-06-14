package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.course.model.entity.EduCourse;
import com.course.model.entity.EduStudent;
import com.exam.model.entity.ExamAssignment;
import com.exam.model.vo.StudentAssignmentVO;

import java.util.List;

/**
 * 学生管理服务接口
 */
public interface EduStudentService {

    /**
     * 分页查询学生列表
     *
     * @param student 查询条件
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    IPage<EduStudent> listStudents(EduStudent student, Integer pageNum, Integer pageSize);

    /**
     * 获取学生详情
     *
     * @param studentId 学生ID
     * @return 学生详情
     */
    EduStudent getStudentById(Long studentId);

    /**
     * 创建学生
     *
     * @param student 学生信息
     * @return 创建后的学生
     */
    EduStudent createStudent(EduStudent student);

    /**
     * 更新学生信息
     *
     * @param student 学生信息
     * @return 更新后的学生
     */
    EduStudent updateStudent(EduStudent student);

    /**
     * 删除学生
     *
     * @param studentId 学生ID
     * @return 操作结果
     */
    boolean deleteStudent(Long studentId);

    /**
     * 获取学生的选课列表
     *
     * @param studentId 学生ID
     * @return 课程列表
     */
    List<EduCourse> getStudentCourses(Long studentId);

    /**
     * 获取学生的作业列表
     *
     * @param studentId 学生ID
     * @return 作业VO列表
     */
    List<StudentAssignmentVO> getStudentAssignments(Long studentId);
} 