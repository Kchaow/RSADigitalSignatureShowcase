package org.letgabr.RSADigitalSignatureShowcase.dto;

import lombok.Data;

@Data
public class UserMessage {
    private ResponseRequestMessage responseRequestMessage;
    private RSAKeys rsaKeys;
    private String hash;
}
