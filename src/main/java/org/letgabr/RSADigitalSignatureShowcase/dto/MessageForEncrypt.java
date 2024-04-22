package org.letgabr.RSADigitalSignatureShowcase.dto;

import lombok.Data;

@Data
public class MessageForEncrypt {
    private ResponseRequestMessage responseRequestMessage;
    private RSAKeys rsaKeys;
}
