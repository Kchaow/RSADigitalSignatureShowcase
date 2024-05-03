package org.letgabr.RSADigitalSignatureShowcase.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.letgabr.RSADigitalSignatureShowcase.dao.UserSessionRepository;
import org.letgabr.RSADigitalSignatureShowcase.dto.RSAKeys;
import org.letgabr.RSADigitalSignatureShowcase.dto.RSAPrimes;
import org.letgabr.RSADigitalSignatureShowcase.dto.ResponseRequestMessage;
import org.letgabr.RSADigitalSignatureShowcase.dto.UserSession;
import org.letgabr.RSADigitalSignatureShowcase.exception.CompositeNumberException;
import org.letgabr.RSADigitalSignatureShowcase.exception.LargeNumberException;
import org.letgabr.RSADigitalSignatureShowcase.exception.NoUserKeysException;
import org.letgabr.RSADigitalSignatureShowcase.exception.NotConnectedException;
import org.letgabr.RSADigitalSignatureShowcase.util.PrimeTester;
import org.letgabr.RSADigitalSignatureShowcase.util.RSACryptoSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
@Slf4j
@PropertySource("classpath:application.yaml")
public class CryptoSessionService
{
    @Value("${crypto.primeNumberLength}")
    int length;
    private final UserSessionRepository userSessionRepository;
    private final PrimeTester primeTester;
    private final MessageDigest messageDigest;
    public CryptoSessionService(UserSessionRepository userSessionRepository, PrimeTester primeTester) throws NoSuchAlgorithmException {
        this.userSessionRepository = userSessionRepository;
        this.messageDigest = MessageDigest.getInstance("SHA-256");
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
        String hash = getHash(responseRequestMessage.text());
        log.info("user {} made message hash: {}", httpSession.getId(), hash);
        String signedHash = RSACryptoSystem.encode(hash, userSession.getRsaCryptoSystem().getPrivateKey(), userSession.getRsaCryptoSystem().getN());
        return new ResponseRequestMessage(signedHash);
    }
    public RSAKeys getKeysOfConnected(HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        log.info("user with {} sessionId requested public key of connected with him", httpSession.getId());
        UserSession userSession = userSessionRepository.findById(httpSession.getId())
                .orElseThrow(() -> new NoUserKeysException("No rsa keys generated"));
        UserSession userSessionOfConnected = userSessionRepository.findById(userSession.getConnection().getUserId())
                .orElseThrow(() -> new NotConnectedException("user with %s doesn't exist".formatted(userSession.getConnection().getUserId())));
        if (!userSessionOfConnected.getConnection().getUserId().equals(userSession.getJsessionId()))
            throw new NotConnectedException("user %s doesn't communicate with %s".formatted(userSessionOfConnected.getJsessionId(), userSession.getJsessionId()));
        return new RSAKeys(null,
                userSessionOfConnected.getRsaCryptoSystem().getPublicKey().toString(),
                userSessionOfConnected.getRsaCryptoSystem().getN().toString());
    }
    public ResponseRequestMessage encrypt(ResponseRequestMessage message, RSAKeys rsaKeys) {
        return new ResponseRequestMessage(RSACryptoSystem.encode(message.text(),
                new BigInteger(rsaKeys.publicKey()),
                new BigInteger(rsaKeys.primesMultiplication())));
    }
    public ResponseRequestMessage decipherBySessionKey(ResponseRequestMessage responseRequestMessage, HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        UserSession userSession = userSessionRepository.findById(httpSession.getId())
                .orElseThrow(() -> new NoUserKeysException("No rsa keys generated"));
        return new ResponseRequestMessage(RSACryptoSystem.decode(responseRequestMessage.text(),
                userSession.getRsaCryptoSystem().getPrivateKey(),
                userSession.getRsaCryptoSystem().getN()));
    }
    public  ResponseRequestMessage decipherByKeys(ResponseRequestMessage responseRequestMessage, RSAKeys rsaKeys) {
        return new ResponseRequestMessage(RSACryptoSystem.decode(responseRequestMessage.text(),
                new BigInteger(rsaKeys.privateKey()),
                new BigInteger(rsaKeys.primesMultiplication())));
    }
    public ResponseRequestMessage getHashForResponseRequestMessage(ResponseRequestMessage responseRequestMessage) {
        return new ResponseRequestMessage(getHash(responseRequestMessage.text()));
    }
    private String getHash(String str) {
        byte[] hashByte = messageDigest.digest(str.getBytes(StandardCharsets.UTF_8));
        return new BigInteger(1, hashByte).toString(16);
    }
}
