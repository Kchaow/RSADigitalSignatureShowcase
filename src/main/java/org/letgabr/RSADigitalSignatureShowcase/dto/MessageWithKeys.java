package org.letgabr.RSADigitalSignatureShowcase.dto;

import lombok.Data;

@Data
public class MessageWithKeys {
    private ResponseRequestMessage responseRequestMessage;
    private RSAKeys rsaKeys;
}
