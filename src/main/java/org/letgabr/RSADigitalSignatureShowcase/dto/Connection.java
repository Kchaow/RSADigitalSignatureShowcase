package org.letgabr.RSADigitalSignatureShowcase.dto;

import lombok.Data;
import org.letgabr.RSADigitalSignatureShowcase.util.ConnectionStatus;

@Data
public class Connection {
    private String userId = "";
    private ConnectionStatus status = ConnectionStatus.NO_CONNECTION;
    private String messagingTopic = "";
}
