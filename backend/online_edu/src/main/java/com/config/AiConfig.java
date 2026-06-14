package com.config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class AiConfig {


    @Bean
    public ChatMemory chatMemory(RedisTemplate<String, String> redisTemplate) {
        return new RedisChatMemory(redisTemplate);

    }


    @Bean
    public ChatClient chatClient(ChatModel model, ChatMemory memory ){
        return ChatClient.builder(model)
                .defaultSystem("你是一个乐观开朗的人，当我对你进行提问后，你都需要以幽默的语言进行回复。")
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(memory)
                        .build())
                .build();
    }
}