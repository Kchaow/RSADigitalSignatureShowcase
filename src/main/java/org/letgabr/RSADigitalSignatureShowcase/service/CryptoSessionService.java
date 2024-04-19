package org.letgabr.RSADigitalSignatureShowcase.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.letgabr.RSADigitalSignatureShowcase.dao.UserSessionRepository;
import org.letgabr.RSADigitalSignatureShowcase.dto.RSAPrimes;
import org.letgabr.RSADigitalSignatureShowcase.dto.UserSession;
import org.letgabr.RSADigitalSignatureShowcase.util.MathCrypto;
import org.letgabr.RSADigitalSignatureShowcase.util.RSACryptoSystem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class CryptoSessionService
{
    final int length = 512;
    private final UserSessionRepository userSessionRepository;
    public CryptoSessionService(UserSessionRepository userSessionRepository) {
        this.userSessionRepository = userSessionRepository;
    }
    public RSAPrimes getPrimes(HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        UserSession userSession = userSessionRepository.findById(httpSession.getId())
                .orElseGet(() ->{
                    RSACryptoSystem rsaCryptoSystem = new RSACryptoSystem(length, length);
                    return userSessionRepository.save(new UserSession(httpSession.getId(), rsaCryptoSystem));
                });
        return new RSAPrimes(userSession.getRsaCryptoSystem().getP().toString(),
                userSession.getRsaCryptoSystem().getQ().toString());
    }
}
