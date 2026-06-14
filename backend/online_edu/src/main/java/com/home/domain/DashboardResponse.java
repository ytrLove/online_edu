package com.home.domain;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.course.model.dto.response.CourseListResponse;
import com.exam.model.vo.AssignmentVO;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Data
public class DashboardResponse {
    // 课程总数
    private Integer courseCount;
    // 待完成作业
    private Integer assignmentCount;
    // 课程资源总数
    private Integer resourceCount;
    // 我的课程
    private IPage<CourseListResponse> myCourses;
    //待完成的作业
    private List<AssignmentVO> pendingAssignments;
}
