package org.letgabr.RSADigitalSignatureShowcase.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.letgabr.RSADigitalSignatureShowcase.dto.MessageForEncrypt;
import org.letgabr.RSADigitalSignatureShowcase.dto.RSAKeys;
import org.letgabr.RSADigitalSignatureShowcase.dto.RSAPrimes;
import org.letgabr.RSADigitalSignatureShowcase.dto.ResponseRequestMessage;
import org.letgabr.RSADigitalSignatureShowcase.service.CryptoSessionService;
import org.letgabr.RSADigitalSignatureShowcase.util.MathCrypto;
import org.letgabr.RSADigitalSignatureShowcase.util.PrimeTester;
import org.letgabr.RSADigitalSignatureShowcase.util.RSACryptoSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.Optional;

@RestController
@Slf4j
@PropertySource("classpath:application.yaml")
public class CryptoController
{
    @Value("${crypto.primeNumberLength}")
    int length;
    final PrimeTester primeTester;
    final CryptoSessionService cryptoSessionService;
    public CryptoController(PrimeTester primeTester, CryptoSessionService cryptoSessionService) {
        this.primeTester = primeTester;
        this.cryptoSessionService = cryptoSessionService;
    }
    @PostMapping("/rsa")
    public ResponseEntity<Object> createUserKeys(HttpServletRequest httpServletRequest) {
        cryptoSessionService.createUserKeys(httpServletRequest);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }
    @GetMapping("/primes")
    public ResponseEntity<RSAPrimes> getPrimes(HttpServletRequest httpServletRequest) {
        return cryptoSessionService.getPrimes(httpServletRequest)
                .map(x -> new ResponseEntity<>(x, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }
    @PostMapping("/primes-check")
    public ResponseEntity<RSAPrimes> primeCheck(@RequestBody RSAPrimes rsaPrimes) {
        return new ResponseEntity<>(new RSAPrimes(cryptoSessionService.primeCheck(rsaPrimes.p()) ? "true" : "false",
                cryptoSessionService.primeCheck(rsaPrimes.q()) ? "true" : "false"),
                HttpStatus.OK);
    }
    @PostMapping("/keys-by-primes")
    public ResponseEntity<RSAKeys> createKeysByPrimes(@RequestBody RSAPrimes rsaPrimes, HttpServletRequest httpServletRequest) {
        return new ResponseEntity<>(cryptoSessionService.createKeysByPrimes(rsaPrimes, httpServletRequest),
                HttpStatus.OK);
    }
    @GetMapping("/keys")
    public ResponseEntity<RSAKeys> getKeys(HttpServletRequest httpServletRequest) {
        return cryptoSessionService.getKeys(httpServletRequest)
                .map(rsaKeys -> new ResponseEntity<>(rsaKeys, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }
    @PostMapping("/sign")
    public ResponseEntity<ResponseRequestMessage> sign(@RequestBody ResponseRequestMessage responseRequestMessage,
                                                       HttpServletRequest httpServletRequest) {
        return new ResponseEntity<>(cryptoSessionService.sign(responseRequestMessage, httpServletRequest), HttpStatus.OK);
    }
    @GetMapping("/connected-keys")
    public ResponseEntity<RSAKeys> getKeysOfConnected(HttpServletRequest httpServletRequest) {
        return new ResponseEntity<>(cryptoSessionService.getKeysOfConnected(httpServletRequest), HttpStatus.OK);
    }
    @PostMapping("/encrypt")
    public ResponseEntity<ResponseRequestMessage> encrypt(@RequestBody MessageForEncrypt messageForEncrypt) {
        return new ResponseEntity<>(cryptoSessionService.encrypt(messageForEncrypt.getResponseRequestMessage(), messageForEncrypt.getRsaKeys()), HttpStatus.OK);
    }
    @PostMapping("/decipher")
    public ResponseEntity<ResponseRequestMessage> decipherBySessionKey(@RequestBody ResponseRequestMessage responseRequestMessage, HttpServletRequest httpServletRequest) {
        return new ResponseEntity<>(cryptoSessionService.decipherBySessionKey(responseRequestMessage, httpServletRequest), HttpStatus.OK);
    }
}
