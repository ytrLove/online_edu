package com.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@TableName("sys_role")
public class SysRole {
    @TableId(type = IdType.AUTO)
    private Long roleId;
    
    private String roleName;
    
    private String roleKey;
    
    private Integer roleSort;
    
    private Integer status;      // 角色状态（0停用 1正常）
    
    private Integer delFlag;     // 删除标志（0代表存在 1代表删除）
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private String remark;

    @TableField(exist = false)
    private Set<String> permissions;
    
    @TableField(exist = false)
    private List<SysMenu> menus;
}