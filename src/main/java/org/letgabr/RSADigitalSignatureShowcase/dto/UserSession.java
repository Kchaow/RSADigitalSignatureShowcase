package org.letgabr.RSADigitalSignatureShowcase.dto;

import lombok.Data;
import org.letgabr.RSADigitalSignatureShowcase.util.RSACryptoSystem;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash
public class UserSession
{
    @Id
    private String jsessionId;
    private RSACryptoSystem rsaCryptoSystem;
    private ConnectionStatus connectionStatus;

    public UserSession() {
        connectionStatus = new ConnectionStatus();
    }
    public UserSession(String jsessionId, RSACryptoSystem rsaCryptoSystem)
    {
        connectionStatus = new ConnectionStatus();
        this.jsessionId = jsessionId;
        this.rsaCryptoSystem = rsaCryptoSystem;
    }
}
