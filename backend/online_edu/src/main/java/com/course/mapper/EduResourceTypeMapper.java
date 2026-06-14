package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.model.entity.EduResourceType;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface EduResourceTypeMapper extends BaseMapper<EduResourceType> {
    /**
     * 获取全部资源类型列表
     * @return 资源类型列表
     */
    List<EduResourceType> selectResourceTypeList();
} 