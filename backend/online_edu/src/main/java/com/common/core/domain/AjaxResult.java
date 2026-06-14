package com.common.core.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AjaxResult<T> {
    private int code;
    private String message;
    private T data;


    public static AjaxResult<Void> success() {
        return new AjaxResult<>(200,"操作成功", null);
    }

    public static <T> AjaxResult<T> success(String message) {
        return new AjaxResult<>(200, message, null);
    }
    public static <T> AjaxResult<T> success(T data) {
        return new AjaxResult<>(200,"操作成功", data);
    }

    public static AjaxResult<Void> fail() {
        return new AjaxResult<>(500, "操作失败", null);
    }

    public static AjaxResult<Void> fail(String message) {
        return new AjaxResult<>(500, message, null);
    }


    public static AjaxResult success(String message, Object result) {
        return new AjaxResult(200, message, result);
    }
}
