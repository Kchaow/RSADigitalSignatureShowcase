package org.letgabr.RSADigitalSignatureShowcase.controller;

import lombok.extern.slf4j.Slf4j;
import org.letgabr.RSADigitalSignatureShowcase.dao.UserSessionRepository;
import org.letgabr.RSADigitalSignatureShowcase.dto.Connection;
import org.letgabr.RSADigitalSignatureShowcase.dto.UserSession;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

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
    public void connectUsers(Connection request, SimpMessageHeaderAccessor headerAccessor)
    {
        String receiverSessionId = request.getUserId();
        String initiatorSessionId = (String) headerAccessor.getSessionAttributes().get("jsessionId");
        Optional<UserSession> receiver = userSessionRepository.findById(receiverSessionId);
        UserSession initiator = userSessionRepository.findById(initiatorSessionId).orElseThrow(() -> new RuntimeException("Session doesn't create yet"));
        if (receiver.isEmpty()) {
            request.setUserId(null);
            log.info("Client {} trying to connect with user {} without generated RSA", initiatorSessionId, receiverSessionId);
            simpMessagingTemplate.convertAndSend("/topic/connections/" + initiatorSessionId, request);
        }
        else if (receiver.get().getConnection().getStatus().equals(CONNECTED)) {
            request.setStatus(CONNECTED);
            log.info("Client {} trying connect with user {} which already connected", initiatorSessionId, receiverSessionId);
            simpMessagingTemplate.convertAndSend("/topic/connections/" + initiatorSessionId, request);
        }
        else if (request.getStatus().equals(CONNECTION_REQUEST)) {
            sendConnectionRequestToReceiver(initiator, initiatorSessionId, receiver.get(), request, receiverSessionId);
        }
        else if (request.getStatus().equals(CONNECTION_ACCEPT)
                && receiver.get().getConnection().getStatus().equals(CONNECTION_REQUEST)
                && receiver.get().getConnection().getUserId().equals(initiatorSessionId)) {
            sendAcceptResponseToReceiver(initiator, receiverSessionId, request, initiatorSessionId, receiver.get());
        }
        else if (request.getStatus().equals(CONNECTION_CONFIRM)
                && receiver.get().getConnection().getStatus().equals(CONNECTION_ACCEPT)
                && receiver.get().getConnection().getUserId().equals(initiatorSessionId)) {
            sendConfirmResponseToReceiver(initiator, receiver.get(), request, initiatorSessionId, receiverSessionId);
        }
    }

    private void sendConnectionRequestToReceiver(UserSession initiator, String initiatorSessionId,
                                                      UserSession receiver, Connection request,
                                                      String receiverSessionId) {
        initiator.getConnection().setUserId(receiverSessionId);
        initiator.getConnection().setStatus(CONNECTION_REQUEST);
        userSessionRepository.save(initiator);
        request.setUserId(initiatorSessionId);
        log.info("Client {} send connectionRequest to user {}", initiatorSessionId, receiverSessionId);
        log.info("Initiator connection status: {}", initiator.getConnection().toString());
        log.info("Receiver connection status: {}", receiver.getConnection().toString());
        simpMessagingTemplate.convertAndSend("/topic/connections/" + receiverSessionId, request);
    }

    private void sendAcceptResponseToReceiver(UserSession initiator, String receiverSessionId, Connection response,
                                              String initiatorSessionId, UserSession receiver) {
        initiator.getConnection().setUserId(receiverSessionId);
        initiator.getConnection().setStatus(CONNECTION_ACCEPT);
        userSessionRepository.save(initiator);
        response.setUserId(initiatorSessionId);
        log.info("Client {} accept connectionRequest from user {}", initiatorSessionId, receiverSessionId);
        log.info("Initiator connection status: {}", initiator.getConnection().toString());
        log.info("Receiver connection status: {}", receiver.getConnection().toString());
        simpMessagingTemplate.convertAndSend("/topic/connections/" + receiverSessionId, response);
    }

    private void sendConfirmResponseToReceiver(UserSession initiator, UserSession receiver, Connection response,
                                               String initiatorSessionId, String receiverSessionId) {
        String connectionTopic = "/topic/messages/" + initiatorSessionId + receiverSessionId;
        initiator.getConnection().setStatus(CONNECTED);
        initiator.getConnection().setMessagingTopic(connectionTopic);
        userSessionRepository.save(initiator);
        receiver.getConnection().setStatus(CONNECTED);
        receiver.getConnection().setMessagingTopic(connectionTopic);
        userSessionRepository.save(receiver);
        response.setUserId(initiatorSessionId);
        log.info("Client {} confirm connection with user {}", initiatorSessionId, receiverSessionId);
        log.info("Initiator connection status: {}", initiator.getConnection().toString());
        log.info("Receiver connection status: {}", receiver.getConnection().toString());
        simpMessagingTemplate.convertAndSend("/topic/connections/" + receiverSessionId, response);
    }
}
