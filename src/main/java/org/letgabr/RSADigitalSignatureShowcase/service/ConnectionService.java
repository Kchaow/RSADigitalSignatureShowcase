package org.letgabr.RSADigitalSignatureShowcase.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.letgabr.RSADigitalSignatureShowcase.dao.UserSessionRepository;
import org.letgabr.RSADigitalSignatureShowcase.dto.Connection;
import org.letgabr.RSADigitalSignatureShowcase.dto.ResponseRequestMessage;
import org.letgabr.RSADigitalSignatureShowcase.dto.UserSession;
import org.letgabr.RSADigitalSignatureShowcase.util.ConnectionStatus;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ConnectionService {
    private final UserSessionRepository userSessionRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    public ConnectionService(UserSessionRepository userSessionRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userSessionRepository = userSessionRepository;
    }
    public ResponseRequestMessage getMessagingTopic(HttpServletRequest httpServletRequest) {
        String sessionId = httpServletRequest.getSession().getId();
        Optional<UserSession> userSession = userSessionRepository.findById(sessionId);
        return new ResponseRequestMessage(userSession.map(x -> x.getConnection().getMessagingTopic()).orElseGet(()->""));
    }
    public boolean isConnectionValid(HttpServletRequest httpServletRequest) {
        String currentUserSessionId = httpServletRequest.getSession().getId();
        Optional<UserSession> currentUserSession = userSessionRepository.findById(currentUserSessionId);
        if (currentUserSession.isEmpty() || !currentUserSession.get().getConnection().getStatus().equals(ConnectionStatus.CONNECTED))
            return false;
        Optional<UserSession> anotherUserSession = userSessionRepository.findById(currentUserSession.get().getConnection().getUserId());
        return anotherUserSession.isPresent() && anotherUserSession.get().getConnection().getStatus().equals(ConnectionStatus.CONNECTED) &&
                anotherUserSession.get().getConnection().getUserId().equals(currentUserSessionId);
    }
    public void connectUsers(Connection request, SimpMessageHeaderAccessor headerAccessor) {
        String receiverSessionId = request.getUserId();
        String initiatorSessionId = (String) headerAccessor.getSessionAttributes().get("jsessionId");
        Optional<UserSession> receiver = userSessionRepository.findById(receiverSessionId);
        UserSession initiator = userSessionRepository.findById(initiatorSessionId).orElseThrow(() -> new RuntimeException("Session doesn't create yet"));
        if (receiver.isEmpty()) {
            request.setUserId(null);
            log.info("Client {} trying to connect with user {} without generated RSA", initiatorSessionId, receiverSessionId);
            simpMessagingTemplate.convertAndSend("/topic/connections/" + initiatorSessionId, request);
        }
        else if (receiver.get().getConnection().getStatus().equals(ConnectionStatus.CONNECTED)) {
            request.setStatus(ConnectionStatus.CONNECTED);
            log.info("Client {} trying connect with user {} which already connected", initiatorSessionId, receiverSessionId);
            simpMessagingTemplate.convertAndSend("/topic/connections/" + initiatorSessionId, request);
        }
        else if (request.getStatus().equals(ConnectionStatus.CONNECTION_REQUEST)) {
            sendConnectionRequestToReceiver(initiator, initiatorSessionId, receiver.get(), request, receiverSessionId);
        }
        else if (request.getStatus().equals(ConnectionStatus.CONNECTION_ACCEPT)
                && receiver.get().getConnection().getStatus().equals(ConnectionStatus.CONNECTION_REQUEST)
                && receiver.get().getConnection().getUserId().equals(initiatorSessionId)) {
            sendAcceptResponseToReceiver(initiator, receiverSessionId, request, initiatorSessionId, receiver.get());
        }
        else if (request.getStatus().equals(ConnectionStatus.CONNECTION_CONFIRM)
                && receiver.get().getConnection().getStatus().equals(ConnectionStatus.CONNECTION_ACCEPT)
                && receiver.get().getConnection().getUserId().equals(initiatorSessionId)) {
            sendConfirmResponseToReceiver(initiator, receiver.get(), request, initiatorSessionId, receiverSessionId);
        }
    }
    public void disconnectUsers(HttpServletRequest httpServletRequest) {
        String sessionId = httpServletRequest.getSession().getId();
        Optional<UserSession> initiator = userSessionRepository.findById(sessionId);
        if (initiator.isEmpty() || !initiator.get().getConnection().getStatus().equals(ConnectionStatus.CONNECTED))
            return;
        Optional<UserSession> receiver = userSessionRepository.findById(initiator.get().getConnection().getUserId());
        if (receiver.isEmpty() || !receiver.get().getConnection().getStatus().equals(ConnectionStatus.CONNECTED)
                || !receiver.get().getConnection().getUserId().equals(sessionId))
            return;
        initiator.get().getConnection().setStatus(ConnectionStatus.NO_CONNECTION);
        initiator.get().getConnection().setUserId("");
        userSessionRepository.save(initiator.get());
        receiver.get().getConnection().setStatus(ConnectionStatus.NO_CONNECTION);
        String receiverId = receiver.get().getJsessionId();
        receiver.get().getConnection().setUserId("");
        userSessionRepository.save(receiver.get());
        simpMessagingTemplate.convertAndSend("/topic/disconnect/" + receiverId,
                new ResponseRequestMessage(sessionId));
    }
    private void sendConnectionRequestToReceiver(UserSession initiator, String initiatorSessionId,
                                                 UserSession receiver, Connection request,
                                                 String receiverSessionId) {
        initiator.getConnection().setUserId(receiverSessionId);
        initiator.getConnection().setStatus(ConnectionStatus.CONNECTION_REQUEST);
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
        initiator.getConnection().setStatus(ConnectionStatus.CONNECTION_ACCEPT);
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
        initiator.getConnection().setStatus(ConnectionStatus.CONNECTED);
        initiator.getConnection().setMessagingTopic(connectionTopic);
        userSessionRepository.save(initiator);
        receiver.getConnection().setStatus(ConnectionStatus.CONNECTED);
        receiver.getConnection().setMessagingTopic(connectionTopic);
        userSessionRepository.save(receiver);
        response.setUserId(initiatorSessionId);
        log.info("Client {} confirm connection with user {}", initiatorSessionId, receiverSessionId);
        log.info("Initiator connection status: {}", initiator.getConnection().toString());
        log.info("Receiver connection status: {}", receiver.getConnection().toString());
        simpMessagingTemplate.convertAndSend("/topic/connections/" + receiverSessionId, response);
    }
}
