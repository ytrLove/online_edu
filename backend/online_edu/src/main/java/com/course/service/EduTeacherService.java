package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.course.model.entity.EduCourse;
import com.course.model.entity.EduTeacher;

import java.util.List;

/**
 * 教师管理服务接口
 */
public interface EduTeacherService {

    /**
     * 分页查询教师列表
     *
     * @param teacher 查询条件
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    IPage<EduTeacher> listTeachers(EduTeacher teacher, Integer pageNum, Integer pageSize);

    /**
     * 获取教师详情
     *
     * @param teacherId 教师ID
     * @return 教师详情
     */
    EduTeacher getTeacherById(Long teacherId);

    List<EduCourse> getTeacherCourses(Long teacherId);

    /**
     * 根据用户ID获取教师信息
     *
     * @param userId 用户ID
     * @return 教师信息，如果不存在则返回null
     */
    EduTeacher getTeacherByUserId(Long userId);

    /**
     * 创建教师
     *
     * @param teacher 教师信息
     * @return 创建后的教师
     */
    EduTeacher createTeacher(EduTeacher teacher);

    /**
     * 更新教师信息
     *
     * @param teacher 教师信息
     * @return 更新后的教师
     */
    EduTeacher updateTeacher(EduTeacher teacher);

    /**
     * 删除教师
     *
     * @param teacherId 教师ID
     * @return 操作结果
     */
    boolean deleteTeacher(Long teacherId);


    List<EduCourse> getTeacherCourses(Integer pageNum, Integer pageSize, EduCourse course);
} 