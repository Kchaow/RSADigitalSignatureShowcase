package org.letgabr.RSADigitalSignatureShowcase.dto;

import lombok.Data;
import org.letgabr.RSADigitalSignatureShowcase.util.RSACryptoSystem;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@RedisHash
public class UserSession
{
    @Id
    @Indexed
    private String jsessionId;
    private RSACryptoSystem rsaCryptoSystem;
    private Connection connection;

    public UserSession() {
        connection = new Connection();
    }
    public UserSession(String jsessionId, RSACryptoSystem rsaCryptoSystem) {
        connection = new Connection();
        this.jsessionId = jsessionId;
        this.rsaCryptoSystem = rsaCryptoSystem;
    }
}
