package org.letgabr.RSADigitalSignatureShowcase.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.letgabr.RSADigitalSignatureShowcase.dto.ResponseRequestMessage;
import org.letgabr.RSADigitalSignatureShowcase.service.ConnectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConnectionController {
    private final ConnectionService connectionService;
    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
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
