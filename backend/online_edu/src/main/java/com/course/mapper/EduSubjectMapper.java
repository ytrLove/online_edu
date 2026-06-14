package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.model.entity.EduSubject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EduSubjectMapper extends BaseMapper<EduSubject> {
    /**
     * 获取全部学科列表，不分页
     * @param subject 查询条件
     * @return 学科列表
     */
    List<EduSubject> selectSubjectList(@Param("subject") EduSubject subject);
    
    /**
     * 根据父ID获取子学科列表
     * @param parentId 父ID
     * @return 子学科列表
     */
    List<EduSubject> selectSubjectsByParentId(@Param("parentId") Long parentId);
} 