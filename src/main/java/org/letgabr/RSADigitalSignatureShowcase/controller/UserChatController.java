package org.letgabr.RSADigitalSignatureShowcase.controller;

import lombok.extern.slf4j.Slf4j;
import org.letgabr.RSADigitalSignatureShowcase.dto.ConnectionStatus;
import org.letgabr.RSADigitalSignatureShowcase.dto.ResponseRequestMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;

import java.net.http.WebSocket;
import java.security.Principal;

@Controller
@Slf4j
public class UserChatController {
    final SimpMessagingTemplate simpMessagingTemplate;
    public UserChatController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }
    @MessageMapping("/requestConnection")
    public void connectUsers(ConnectionStatus connectionStatus, SimpMessageHeaderAccessor headerAccessor)
    {
        if (connectionStatus.getStatus().equals("connectionRequest")) {
            String requestedId = connectionStatus.getUserId();
            connectionStatus.setUserId((String) headerAccessor.getSessionAttributes().get("sessionId"));
            log.info(requestedId);
            simpMessagingTemplate.convertAndSend("/topic/connections/" + requestedId, connectionStatus);
        }
    }

}
