package com.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_menu")
public class SysMenu {
    @TableId(type = IdType.AUTO)
    private Long menuId;
    private String menuName;
    private Long parentId;
    private String path;
    private String component;
    @TableField(exist = false)
    private List<SysMenu> children;
    private String perms;
    private String icon;
    private String menuType;
    private Integer orderNum;
    private Integer status;
    private Integer delFlag;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;
    private Boolean isButton;
}