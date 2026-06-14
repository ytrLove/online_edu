package com.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@TableName("sys_user")
public class SysUser implements UserDetails {
    @TableId(type = IdType.AUTO)
    private Long userId;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private String sex;
    private String avatar;
    private Integer status;
    private Integer delFlag;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;

    @TableField(exist = false)
    private List<SysRole> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        
        // 添加角色权限
        if (roles != null) {
            for (SysRole role : roles) {
                // 添加角色
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleKey()));
                
                // 获取角色对应的菜单权限
                if (role.getMenus() != null) {
                    authorities.addAll(role.getMenus().stream()
                            .filter(menu -> menu.getPerms() != null && !menu.getPerms().isEmpty())
                            .map(menu -> new SimpleGrantedAuthority(menu.getPerms()))
                            .collect(Collectors.toList()));
                }
            }
        }
        
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == 1;
    }
}