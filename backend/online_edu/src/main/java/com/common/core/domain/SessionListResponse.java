package com.common.core.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SessionListResponse {
    private String sessionId;
    private String title;
    private Long createdAt;
}
