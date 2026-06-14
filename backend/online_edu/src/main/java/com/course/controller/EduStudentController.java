package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.common.core.domain.AjaxResult;
import com.course.model.entity.EduCourse;
import com.course.model.entity.EduStudent;
import com.course.service.EduStudentService;
import com.exam.model.entity.ExamAssignment;
import com.exam.model.entity.ExamPaper;
import com.exam.model.entity.ExamRecord;
import com.exam.model.param.SubmissionParam;
import com.exam.model.vo.AssignmentVO;
import com.exam.model.vo.ExamPaperVO;
import com.exam.model.vo.ExamRecordVO;
import com.exam.model.vo.StudentAssignmentVO;
import com.exam.model.vo.SubmissionVO;
import com.exam.service.ExamAssignmentService;
import com.exam.service.ExamAssignmentSubmissionService;
import com.exam.service.ExamPaperService;
import com.exam.service.ExamRecordService;
import com.util.GetId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生管理控制器
 */
@RestController
@RequestMapping("/api/edu/student")
public class EduStudentController {

    @Autowired
    private EduStudentService studentService;

    @Autowired
    private ExamPaperService examPaperService;

    @Autowired
    private ExamAssignmentService assignmentService;

    @Autowired
    private ExamRecordService recordService;

    @Autowired
    private ExamAssignmentSubmissionService submissionService;

    @Autowired
    private GetId getId;

    /**
     * 分页查询学生列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('edu:student:query')")
    public AjaxResult<IPage<EduStudent>> list(EduStudent student,
                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<EduStudent> page = studentService.listStudents(student, pageNum, pageSize);
        return AjaxResult.success(page);
    }

    /**
     * 获取学生详情
     */
    @GetMapping("/{studentId}")
    @PreAuthorize("hasAuthority('edu:student:query')")
    public AjaxResult<EduStudent> getInfo(@PathVariable Long studentId) {
        EduStudent student = studentService.getStudentById(studentId);
        return AjaxResult.success(student);
    }

    /**
     * 新增学生
     */
    @PostMapping
    @PreAuthorize("hasAuthority('edu:student:add')")
    public AjaxResult<EduStudent> add(@RequestBody EduStudent student) {
        EduStudent result = studentService.createStudent(student);
        return AjaxResult.success(result);
    }

    /**
     * 修改学生
     */
    @PutMapping
    @PreAuthorize("hasAuthority('edu:student:edit')")
    public AjaxResult<EduStudent> update(@RequestBody EduStudent student) {
        EduStudent result = studentService.updateStudent(student);
        return AjaxResult.success(result);
    }

    /**
     * 删除学生
     */
    @DeleteMapping("/{studentId}")
    @PreAuthorize("hasAuthority('edu:student:remove')")
    public AjaxResult<Boolean> remove(@PathVariable Long studentId) {
        boolean result = studentService.deleteStudent(studentId);
        return AjaxResult.success(result);
    }

    /**
     * 获取学生的选课列表
     */
    @GetMapping("/courses")
    @PreAuthorize("hasAuthority('edu:student:query')")
    public AjaxResult<List<EduCourse>> getStudentCourses() {
        List<EduCourse> courses = studentService.getStudentCourses(getId.getId());
        return AjaxResult.success(courses);
    }

    /**
     * 获取学生的作业列表
     */
    @GetMapping("/assignments")
    @PreAuthorize("hasAuthority('edu:student:query')")
    public AjaxResult<List<StudentAssignmentVO>> getStudentAssignments() {
        List<StudentAssignmentVO> examAssignments = studentService.getStudentAssignments(getId.getId());
        return AjaxResult.success(examAssignments);
    }

    /**
     * 获取学生的作业详情
     */
    @GetMapping("/assignment/{assignmentId}")
    @PreAuthorize("hasAuthority('edu:student:query')")
    public AjaxResult<AssignmentVO> getStudentAssignment(@PathVariable Long assignmentId) {
        AssignmentVO assignment = assignmentService.selectStudentAssignmentVOById(assignmentId, getId.getId());
        return AjaxResult.success(assignment);
    }

    /**
     * 提交作业
     */
    @PostMapping("/assignment/submit")
    @PreAuthorize("hasAuthority('edu:student:query')")
    public AjaxResult<Boolean> submitAssignment(@RequestBody SubmissionParam submissionParam) {
        boolean result = submissionService.submitAssignment(submissionParam, getId.getId());
        return AjaxResult.success(result);
    }

    /**
     * 查看作业提交详情
     */
    @GetMapping("/assignment/submission/{submissionId}")
    @PreAuthorize("hasAuthority('edu:student:query')")
    public AjaxResult<SubmissionVO> getSubmissionDetail(@PathVariable Long submissionId) {
        SubmissionVO submission = submissionService.selectSubmissionVOById(submissionId);
        return AjaxResult.success(submission);
    }

    /**
     * 获取学生的考试列表
     */
    @GetMapping("/exams")
    @PreAuthorize("hasAuthority('edu:student:query')")
    public AjaxResult<IPage<ExamPaper>> getStudentExams(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            ExamPaper exam) {
        IPage<ExamPaper> examPage = examPaperService.selectStudentExamPage(pageNum, pageSize, getId.getId(), exam);
        return AjaxResult.success(examPage);
    }

    /**
     * 获取学生的考试详情
     */
    @GetMapping("/exam/{examId}")
    @PreAuthorize("hasAuthority('edu:student:query')")
    public AjaxResult<ExamPaperVO> getStudentExam(@PathVariable Long examId) {
        ExamPaperVO exam = examPaperService.selectStudentExamVOById(examId, getId.getId());
        return AjaxResult.success(exam);
    }

    /**
     * 开始考试
     */
    @PostMapping("/exam/{examId}/start")
    @PreAuthorize("hasAuthority('edu:student:query')")
    public AjaxResult<Long> startExam(@PathVariable Long examId) {
        Long recordId = recordService.startExam(examId, getId.getId());
        return AjaxResult.success(recordId);
    }

    /**
     * 提交考试
     */
    @PostMapping("/exam/submit")
    @PreAuthorize("hasAuthority('edu:student:query')")
    public AjaxResult<Boolean> submitExam(@RequestBody SubmissionParam submissionParam) {
        boolean result = recordService.submitExam(submissionParam, getId.getId());
        return AjaxResult.success(result);
    }

    /**
     * 获取考试记录列表
     */
    @GetMapping("/exam/records")
    @PreAuthorize("hasAuthority('edu:student:query')")
    public AjaxResult<IPage<ExamRecord>> getExamRecords(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            ExamRecord record) {
        IPage<ExamRecord> recordPage = recordService.selectStudentRecordPage(pageNum, pageSize, getId.getId(), record);
        return AjaxResult.success(recordPage);
    }

    /**
     * 获取考试记录详情
     */
    @GetMapping("/exam/record/{recordId}")
    @PreAuthorize("hasAuthority('edu:student:query')")
    public AjaxResult<ExamRecordVO> getExamRecord(@PathVariable Long recordId) {
        ExamRecordVO record = recordService.selectRecordVOById(recordId);
        return AjaxResult.success(record);
    }
} 