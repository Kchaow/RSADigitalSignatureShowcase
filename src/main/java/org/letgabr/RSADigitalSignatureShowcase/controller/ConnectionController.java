package org.letgabr.RSADigitalSignatureShowcase.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.letgabr.RSADigitalSignatureShowcase.dao.UserSessionRepository;
import org.letgabr.RSADigitalSignatureShowcase.dto.Connection;
import org.letgabr.RSADigitalSignatureShowcase.dto.ResponseRequestMessage;
import org.letgabr.RSADigitalSignatureShowcase.service.ConnectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class ConnectionController {
    final ConnectionService connectionService;
    final UserSessionRepository userSessionRepository;
    public ConnectionController(SimpMessagingTemplate simpMessagingTemplate, UserSessionRepository userSessionRepository, ConnectionService connectionService) {
        this.connectionService = connectionService;
        this.userSessionRepository = userSessionRepository;
    }
    @MessageMapping("/requestConnection")
    public void connectUsers(Connection request, SimpMessageHeaderAccessor headerAccessor)
    {
        connectionService.connectUsers(request, headerAccessor);
    }
    @GetMapping("/disconnect")
    public ResponseEntity<Object> disconnectUsers(HttpServletRequest httpServletRequest) {
        connectionService.disconnectUsers(httpServletRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/messagingTopic")
    public ResponseEntity<ResponseRequestMessage> getMessagingTopic(HttpServletRequest httpServletRequest) {
        return new ResponseEntity<>(connectionService.getMessagingTopic(httpServletRequest), HttpStatus.OK);
    }
    @GetMapping("/isConnectionValid")
    public ResponseEntity<ResponseRequestMessage> getIsConnectionValid(HttpServletRequest httpServletRequest) {
        return new ResponseEntity<>(new ResponseRequestMessage(connectionService.isConnectionValid(httpServletRequest)+""), HttpStatus.OK);
    }
}
