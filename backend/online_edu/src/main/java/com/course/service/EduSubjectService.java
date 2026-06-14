package com.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.course.model.entity.EduSubject;
import java.util.List;

public interface EduSubjectService extends IService<EduSubject> {
    /**
     * 获取学科树形结构
     * @return 树形结构的学科列表
     */
    List<EduSubject> getSubjectTree();
    
    /**
     * 获取顶级学科列表
     * @return 顶级学科列表
     */
    List<EduSubject> getTopSubjects();
    
    /**
     * 根据父ID获取子学科
     * @param parentId 父ID
     * @return 子学科列表
     */
    List<EduSubject> getChildrenByParentId(Long parentId);
    
    /**
     * 删除学科，如果有子学科则不允许删除
     * @param subjectId 学科ID
     * @return 是否删除成功
     */
    boolean removeSubject(Long subjectId);
} 