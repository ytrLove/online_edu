package com.common.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.ai.chat.messages.Message;

@AllArgsConstructor
@Data
public class TimestampedMessage {
    private final Long timestamp;
    private final Message message;

}

