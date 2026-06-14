package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.mapper.EduSubjectMapper;
import com.course.model.entity.EduSubject;
import com.course.service.EduSubjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {

    @Override
    public List<EduSubject> getSubjectTree() {
        // 获取所有学科
        List<EduSubject> allSubjects = list(new LambdaQueryWrapper<EduSubject>()
                .eq(EduSubject::getDelFlag, 0)
                .orderByAsc(EduSubject::getOrderNum));

        // 构建树形结构
        // 按parentId分组
        Map<Long, List<EduSubject>> parentMap = allSubjects.stream()
                .collect(Collectors.groupingBy(EduSubject::getParentId));

        // 获取根节点
        List<EduSubject> rootList = parentMap.getOrDefault(0L, new ArrayList<>());

        // 递归设置子节点
        rootList.forEach(root -> buildChildren(root, parentMap));

        return rootList;
    }

    @Override
    public List<EduSubject> getTopSubjects() {
        // 获取所有顶级学科
        return list(new LambdaQueryWrapper<EduSubject>()
                .eq(EduSubject::getParentId, 0L)
                .eq(EduSubject::getDelFlag, 0)
                .orderByAsc(EduSubject::getOrderNum));
    }

    @Override
    public List<EduSubject> getChildrenByParentId(Long parentId) {
        // 获取子学科
        return list(new LambdaQueryWrapper<EduSubject>()
                .eq(EduSubject::getParentId, parentId)
                .eq(EduSubject::getDelFlag, 0)
                .orderByAsc(EduSubject::getOrderNum));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeSubject(Long subjectId) {
        // 检查是否有子学科
        int childCount = (int) count(new LambdaQueryWrapper<EduSubject>()
                .eq(EduSubject::getParentId, subjectId)
                .eq(EduSubject::getDelFlag, 0));

        if (childCount > 0) {
            throw new RuntimeException("该学科下还有子学科，不能删除");
        }

        // 逻辑删除
        EduSubject subject = getById(subjectId);
        if (subject != null) {
            subject.setDelFlag(1);  // 设置删除标志
            return updateById(subject);
        }
        return false;
    }
    
    /**
     * 递归构建子节点
     * @param parent 父节点
     * @param parentMap 按parentId分组的学科Map
     */
    private void buildChildren(EduSubject parent, Map<Long, List<EduSubject>> parentMap) {
        List<EduSubject> children = parentMap.getOrDefault(parent.getSubjectId(), new ArrayList<>());
        if (!children.isEmpty()) {
            parent.setChildren(children);
            children.forEach(child -> buildChildren(child, parentMap));
        }
    }
} 