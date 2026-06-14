package com.user.mapper;

import com.user.model.dto.request.RoleCreateRequest;
import com.user.model.dto.request.RoleUpdateRequest;
import com.user.model.dto.response.RoleDetailResponse;
import com.user.model.entity.SysRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 角色对象转换器
 */
@Mapper(
    componentModel = "default", 
    uses = {MenuConverter.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RoleConverter {
    
    RoleConverter INSTANCE = Mappers.getMapper(RoleConverter.class);
    
    /**
     * 将创建请求转换为实体
     */
    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "menus", ignore = true)
    SysRole toEntity(RoleCreateRequest request);
    
    /**
     * 将更新请求转换为实体
     */
    @Mapping(target = "delFlag", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "menus", ignore = true)
    SysRole toEntity(RoleUpdateRequest request);
    
    /**
     * 将实体转换为响应DTO
     */
    @Mapping(target = "menus", source = "menus", qualifiedByName = "toMenuInfoResponseList")
    RoleDetailResponse toRoleDetailResponse(SysRole role);
    
    /**
     * 将实体列表转换为响应DTO列表
     */
    List<RoleDetailResponse> toRoleDetailResponseList(List<SysRole> roles);
} 