package org.letgabr.RSADigitalSignatureShowcase.unit;

import org.junit.jupiter.api.Test;
import org.letgabr.RSADigitalSignatureShowcase.util.RSACryptoSystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RSACryptoSystemTest
{
    @Test
    public void encodeDecodeTest()
    {
        int length = 512;
        RSACryptoSystem rsaCryptoSystem = new RSACryptoSystem(length, length);
        String text = "Hello world";
        String encodedText = RSACryptoSystem.encode(text, rsaCryptoSystem.getPublicKey(), rsaCryptoSystem.getN());
        assertEquals(text, RSACryptoSystem.decode(encodedText, rsaCryptoSystem.getPrivateKey(), rsaCryptoSystem.getN()));
    }
}
