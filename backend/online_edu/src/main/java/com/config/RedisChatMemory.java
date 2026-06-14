package com.config;


import com.common.core.domain.SessionListResponse;
import com.common.core.domain.TimestampedMessage;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class RedisChatMemory implements ChatMemory {
    private final RedisTemplate<String, String> redisTemplate;

    public static final String CHAT_MEMORY_KEY = "chat_memory_session_ID:";

    public RedisChatMemory(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        long baseTime = System.currentTimeMillis();
        int counter = 0; // 添加计数器确保唯一性
        for (Message message : messages) {
            // 为每个消息生成唯一时间戳
            long uniqueTime = baseTime + (counter++);
            redisTemplate.opsForHash().put(CHAT_MEMORY_KEY +  conversationId, String.valueOf(uniqueTime), message.getContent());
        }
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(CHAT_MEMORY_KEY + conversationId);
        return entries.entrySet().stream()
                .sorted((o1, o2) -> {
                    long time1 = Long.parseLong(o1.getKey().toString());
                    long time2 = Long.parseLong(o2.getKey().toString());
                    return Long.compare(time1, time2);
                })
                .limit(lastN)
                .map(e -> new org.springframework.ai.chat.messages.UserMessage(e.getValue().toString()))
                .collect(Collectors.toList());
    }

    public List<TimestampedMessage> getWithTimestamp(String conversationId, int lastN) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(CHAT_MEMORY_KEY + conversationId);

        return entries.entrySet().stream()
                .map(entry -> {
                    long timestamp = Long.parseLong(entry.getKey().toString());
                    String content = entry.getValue().toString();
                    Message message = new UserMessage(content);
                    return new TimestampedMessage(timestamp, message);
                })
                .sorted(Comparator.comparing(TimestampedMessage::getTimestamp))
                .limit(lastN)
                .collect(Collectors.toList());
    }

    @Override
    public void clear(String conversationId) {
        redisTemplate.delete(conversationId);
    }


    public List<SessionListResponse> getAllSessions() {
       Set<String> keys =redisTemplate.keys(CHAT_MEMORY_KEY + "*");
        List<SessionListResponse> sessionListResponses = null;
        if (keys != null) {
            sessionListResponses = keys.stream().map(key -> {

                String sessionId = key.replace(CHAT_MEMORY_KEY, "");
                List<TimestampedMessage> messages = this.getWithTimestamp(sessionId, 1);
                return new SessionListResponse(sessionId, messages.get(0).getMessage().getText(),messages.get(0).getTimestamp());
            }).collect(Collectors.toList());
        }
        return sessionListResponses;
    }
}