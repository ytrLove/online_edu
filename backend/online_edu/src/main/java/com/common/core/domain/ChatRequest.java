package com.common.core.domain;

import lombok.Data;

@Data
public class ChatRequest {
    private String query;
    private String sessionId;
}