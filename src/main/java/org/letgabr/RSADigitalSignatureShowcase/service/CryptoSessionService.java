package org.letgabr.RSADigitalSignatureShowcase.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.User;
import org.letgabr.RSADigitalSignatureShowcase.dao.UserSessionRepository;
import org.letgabr.RSADigitalSignatureShowcase.dto.RSAKeys;
import org.letgabr.RSADigitalSignatureShowcase.dto.RSAPrimes;
import org.letgabr.RSADigitalSignatureShowcase.dto.ResponseRequestMessage;
import org.letgabr.RSADigitalSignatureShowcase.dto.UserSession;
import org.letgabr.RSADigitalSignatureShowcase.exception.CompositeNumberException;
import org.letgabr.RSADigitalSignatureShowcase.exception.LargeNumberException;
import org.letgabr.RSADigitalSignatureShowcase.exception.NoUserKeysException;
import org.letgabr.RSADigitalSignatureShowcase.util.MathCrypto;
import org.letgabr.RSADigitalSignatureShowcase.util.PrimeTester;
import org.letgabr.RSADigitalSignatureShowcase.util.RSACryptoSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Optional;

@Service
@PropertySource("classpath:application.yaml")
public class CryptoSessionService
{
    @Value("${crypto.primeNumberLength}")
    int length;
    private final UserSessionRepository userSessionRepository;
    private final PrimeTester primeTester;
    public CryptoSessionService(UserSessionRepository userSessionRepository, PrimeTester primeTester) {
        this.userSessionRepository = userSessionRepository;
        this.primeTester = primeTester;
    }
    public void createUserKeys(HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        RSACryptoSystem rsaCryptoSystem = new RSACryptoSystem(length, length);
        userSessionRepository.save(new UserSession(httpSession.getId(), rsaCryptoSystem));
    }
    public Optional<RSAPrimes> getPrimes(HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        return userSessionRepository.findById(httpSession.getId())
                .map(x -> new RSAPrimes(x.getRsaCryptoSystem().getP().toString(),
                        x.getRsaCryptoSystem().getQ().toString()));
    }
    public RSAKeys createKeysByPrimes(RSAPrimes rsaPrimes, HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        Optional<UserSession> userSessionOpt = userSessionRepository.findById(httpSession.getId());
        BigInteger p = new BigInteger(rsaPrimes.p());
        BigInteger q = new BigInteger(rsaPrimes.q());
        if (!primeTester.isPrime(p))
            throw new CompositeNumberException("p isn't prime");
        if (!primeTester.isPrime(q))
            throw new CompositeNumberException("q isn't prime");
        RSACryptoSystem rsaCryptoSystem = new RSACryptoSystem(p, q);
        UserSession userSession = userSessionOpt.orElseGet(() -> {
            UserSession us = new UserSession();
            us.setJsessionId(httpSession.getId());
            return us;
        });
        userSession.setRsaCryptoSystem(rsaCryptoSystem);
        userSessionRepository.save(userSession);
        return new RSAKeys(rsaCryptoSystem.getPrivateKey().toString(),
                rsaCryptoSystem.getPublicKey().toString(),
                rsaCryptoSystem.getN().toString());
    }
    public Optional<RSAKeys> getKeys(HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        return userSessionRepository.findById(httpSession.getId())
                .map(x -> new RSAKeys(x.getRsaCryptoSystem().getPrivateKey().toString(),
                        x.getRsaCryptoSystem().getPublicKey().toString(),
                        x.getRsaCryptoSystem().getN().toString()));
    }
    public boolean primeCheck(String number) {
        BigInteger numb = new BigInteger(number);
        if (numb.bitLength() > length)
            throw new LargeNumberException("%s number too large".formatted(number));
        return primeTester.isPrime(numb);
    }
    public ResponseRequestMessage sign(ResponseRequestMessage responseRequestMessage, HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        UserSession userSession = userSessionRepository.findById(httpSession.getId())
                .orElseThrow(() -> new NoUserKeysException("No rsa keys generated"));
        return new ResponseRequestMessage(
          RSACryptoSystem.encode(responseRequestMessage.text(),
                  userSession.getRsaCryptoSystem().getPrivateKey(),
                  userSession.getRsaCryptoSystem().getN())
        );
    }
}
