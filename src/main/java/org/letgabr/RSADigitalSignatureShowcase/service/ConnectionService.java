package org.letgabr.RSADigitalSignatureShowcase.service;

import jakarta.servlet.http.HttpServletRequest;
import org.letgabr.RSADigitalSignatureShowcase.dao.UserSessionRepository;
import org.letgabr.RSADigitalSignatureShowcase.dto.ResponseRequestMessage;
import org.letgabr.RSADigitalSignatureShowcase.dto.UserSession;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConnectionService {
    private final UserSessionRepository userSessionRepository;
    public ConnectionService(UserSessionRepository userSessionRepository) {
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
        if (currentUserSession.isEmpty() || !currentUserSession.get().getConnection().getStatus().equals("connected"))
            return false;
        Optional<UserSession> anotherUserSession = userSessionRepository.findById(currentUserSession.get().getConnection().getUserId());
        return anotherUserSession.isPresent() && anotherUserSession.get().getConnection().getStatus().equals("connected") &&
                anotherUserSession.get().getConnection().getUserId().equals(currentUserSessionId);
    }
}
