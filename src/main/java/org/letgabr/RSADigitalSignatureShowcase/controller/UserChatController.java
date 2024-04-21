package org.letgabr.RSADigitalSignatureShowcase.controller;

import lombok.extern.slf4j.Slf4j;
import org.letgabr.RSADigitalSignatureShowcase.dao.UserSessionRepository;
import org.letgabr.RSADigitalSignatureShowcase.dto.ConnectionStatus;
import org.letgabr.RSADigitalSignatureShowcase.dto.ResponseRequestMessage;
import org.letgabr.RSADigitalSignatureShowcase.dto.UserSession;
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
import java.util.Optional;

@Controller
@Slf4j
public class UserChatController {
    final String CONNECTION_REQUEST = "connectionRequest";
    final String CONNECTION_ACCEPT = "connectionAccept";
    final String CONNECTION_CONFIRM = "connectionConfirm";
    final String CONNECTED = "connected";
    final SimpMessagingTemplate simpMessagingTemplate;
    final UserSessionRepository userSessionRepository;
    public UserChatController(SimpMessagingTemplate simpMessagingTemplate, UserSessionRepository userSessionRepository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userSessionRepository = userSessionRepository;
    }
    @MessageMapping("/requestConnection")
    public void connectUsers(ConnectionStatus connectionStatus, SimpMessageHeaderAccessor headerAccessor)
    {
        String requestedId = connectionStatus.getUserId();
        String clientId = (String) headerAccessor.getSessionAttributes().get("jsessionId");
        Optional<UserSession> requestedUserSession = userSessionRepository.findById(requestedId);
        UserSession clientUserSession = userSessionRepository.findById(clientId).orElseThrow(() -> new RuntimeException("Session doesn't create yet"));
        if (requestedUserSession.isEmpty()) {
            connectionStatus.setUserId(null);
            log.info("Client {} trying to connect with user {} without generated RSA", clientId, requestedId);
            simpMessagingTemplate.convertAndSend("/topic/connections/" + clientId, connectionStatus);
        }
        else if (requestedUserSession.get().getConnectionStatus().getStatus().equals(CONNECTED)) {
            connectionStatus.setStatus(CONNECTED);
            log.info("Client {} trying connect with user {} which already connected", clientId, requestedId);
            simpMessagingTemplate.convertAndSend("/topic/connections/" + clientId, connectionStatus);
        }
        else if (connectionStatus.getStatus().equals(CONNECTION_REQUEST)) {
            clientUserSession.getConnectionStatus().setUserId(requestedId);
            clientUserSession.getConnectionStatus().setStatus(CONNECTION_REQUEST);
            userSessionRepository.save(clientUserSession);
            connectionStatus.setUserId(clientId);
            log.info("Client {} send connectionRequest to user {}", clientId, requestedId);
            log.info(clientUserSession.getConnectionStatus().toString());
            log.info(requestedUserSession.get().getConnectionStatus().toString());
            simpMessagingTemplate.convertAndSend("/topic/connections/" + requestedId, connectionStatus);
        }
        else if (connectionStatus.getStatus().equals(CONNECTION_ACCEPT)
                && requestedUserSession.get().getConnectionStatus().getStatus().equals(CONNECTION_REQUEST)
                && requestedUserSession.get().getConnectionStatus().getUserId().equals(clientId)) {
            clientUserSession.getConnectionStatus().setUserId(requestedId);
            clientUserSession.getConnectionStatus().setStatus(CONNECTION_ACCEPT);
            userSessionRepository.save(clientUserSession);
            connectionStatus.setUserId(clientId);
            log.info("Client {} accept connectionRequest from user {}", clientId, requestedId);
            log.info(clientUserSession.getConnectionStatus().toString());
            log.info(requestedUserSession.get().getConnectionStatus().toString());
            simpMessagingTemplate.convertAndSend("/topic/connections/" + requestedId, connectionStatus);
        }
        else if (connectionStatus.getStatus().equals(CONNECTION_CONFIRM)
                && requestedUserSession.get().getConnectionStatus().getStatus().equals(CONNECTION_ACCEPT)
                && requestedUserSession.get().getConnectionStatus().getUserId().equals(clientId)) {
            clientUserSession.getConnectionStatus().setStatus(CONNECTED);
            userSessionRepository.save(clientUserSession);
            requestedUserSession.get().getConnectionStatus().setStatus(CONNECTED);
            userSessionRepository.save(clientUserSession);
            connectionStatus.setUserId(clientId);
            log.info("Client {} confirm connection with user {}", clientId, requestedId);
            log.info(clientUserSession.getConnectionStatus().toString());
            log.info(requestedUserSession.get().getConnectionStatus().toString());
            simpMessagingTemplate.convertAndSend("/topic/connections/" + requestedId, connectionStatus);
        }
    }
}
