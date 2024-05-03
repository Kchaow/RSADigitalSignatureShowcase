package org.letgabr.RSADigitalSignatureShowcase.controller;

import lombok.extern.slf4j.Slf4j;
import org.letgabr.RSADigitalSignatureShowcase.dao.UserSessionRepository;
import org.letgabr.RSADigitalSignatureShowcase.dto.Connection;
import org.letgabr.RSADigitalSignatureShowcase.dto.UserSession;
import org.letgabr.RSADigitalSignatureShowcase.service.ConnectionService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
@Slf4j
public class UserChatController {
    final ConnectionService connectionService;
    final UserSessionRepository userSessionRepository;
    public UserChatController(SimpMessagingTemplate simpMessagingTemplate, UserSessionRepository userSessionRepository, ConnectionService connectionService) {
        this.connectionService = connectionService;
        this.userSessionRepository = userSessionRepository;
    }
    @MessageMapping("/requestConnection")
    public void connectUsers(Connection request, SimpMessageHeaderAccessor headerAccessor)
    {
        connectionService.connectUsers(request, headerAccessor);
    }
}
