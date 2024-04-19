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

    public UserSession() {}
    public UserSession(String jsessionId, RSACryptoSystem rsaCryptoSystem)
    {
        this.jsessionId = jsessionId;
        this.rsaCryptoSystem = rsaCryptoSystem;
    }
}
