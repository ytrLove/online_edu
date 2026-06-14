package com.user.mapper;

import com.user.model.dto.request.MenuCreateRequest;
import com.user.model.dto.request.MenuUpdateRequest;
import com.user.model.dto.response.MenuInfoResponse;
import com.user.model.entity.SysMenu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 菜单对象转换器
 */
@Mapper(
    componentModel = "default",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MenuConverter {
    
    MenuConverter INSTANCE = Mappers.getMapper(MenuConverter.class);
    
    /**
     * 将创建请求转换为实体
     */
    @Mapping(target = "menuId", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    SysMenu toEntity(MenuCreateRequest request);
    
    /**
     * 将更新请求转换为实体
     */
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    SysMenu toEntity(MenuUpdateRequest request);
    
    /**
     * 将实体转换为响应DTO
     */
    MenuInfoResponse toMenuInfoResponse(SysMenu menu);
    
    /**
     * 将实体列表转换为响应DTO列表
     */
    @Named("toMenuInfoResponseList")
    List<MenuInfoResponse> toMenuInfoResponseList(List<SysMenu> menus);
} 