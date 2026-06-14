package com.common.Controller;


import com.common.core.domain.AjaxResult;
import com.common.core.domain.ChatRequest;
import com.common.core.domain.SessionListResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@RestController
public class chatRoomController {

    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ChatMemory chatMemory;


    @GetMapping("/create/session_id")
    public AjaxResult createSessionId() {
        return AjaxResult.success(java.util.UUID.randomUUID().toString());
    }

    @PostMapping(value = "/chat")
    public AjaxResult chatStream(
           @RequestBody ChatRequest request) {

        String res = chatClient.prompt()
                .user(request.getQuery())
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, request.getSessionId())
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call().content();
        return AjaxResult.success(res);
    }

    @GetMapping("/chat/history")
    public AjaxResult getHistory(@RequestParam String sessionId) {
        return AjaxResult.success(chatMemory.get(sessionId, 10));

    }

    /**
     * 获取所有会话列表
     * @return 会话列表
     */

    @GetMapping("/chat/sessions")
    public AjaxResult getAllSessions() {
        Class<? extends ChatMemory> aClass = chatMemory.getClass();
        Method getAllSessions = null;
        try {
            getAllSessions = aClass.getDeclaredMethod("getAllSessions");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        getAllSessions.setAccessible(true);
        try {
            List<SessionListResponse> allSessions = (List<SessionListResponse>) getAllSessions.invoke(chatMemory);
            return AjaxResult.success(allSessions);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    @DeleteMapping("/chat/clear")
    public AjaxResult clearSession(@RequestParam String sessionId) {
        chatMemory.clear(sessionId);
        return AjaxResult.success();
    }
}


