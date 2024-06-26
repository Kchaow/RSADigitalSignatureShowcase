package org.letgabr.RSADigitalSignatureShowcase.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.letgabr.RSADigitalSignatureShowcase.dto.UserMessage;
import org.letgabr.RSADigitalSignatureShowcase.dto.RSAKeys;
import org.letgabr.RSADigitalSignatureShowcase.dto.RSAPrimes;
import org.letgabr.RSADigitalSignatureShowcase.dto.ResponseRequestMessage;
import org.letgabr.RSADigitalSignatureShowcase.service.ConnectionService;
import org.letgabr.RSADigitalSignatureShowcase.service.CryptoSessionService;
import org.letgabr.RSADigitalSignatureShowcase.util.PrimeTester;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@PropertySource("classpath:application.yaml")
public class CryptoController
{
    @Value("${crypto.primeNumberLength}")
    private int length;
    private final CryptoSessionService cryptoSessionService;
    public CryptoController(PrimeTester primeTester, CryptoSessionService cryptoSessionService, ConnectionService connectionService) {
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
    public ResponseEntity<ResponseRequestMessage> encrypt(@RequestBody UserMessage messageForEncrypt) {
        return new ResponseEntity<>(cryptoSessionService.encrypt(messageForEncrypt.getResponseRequestMessage(), messageForEncrypt.getRsaKeys()), HttpStatus.OK);
    }
    @PostMapping("/decipher")
    public ResponseEntity<ResponseRequestMessage> decipherBySessionKey(@RequestBody ResponseRequestMessage responseRequestMessage, HttpServletRequest httpServletRequest) {
        return new ResponseEntity<>(cryptoSessionService.decipherBySessionKey(responseRequestMessage, httpServletRequest), HttpStatus.OK);
    }
    @PostMapping("/decipher-by-keys")
    public ResponseEntity<ResponseRequestMessage> decipherByKeys(@RequestBody UserMessage userMessage) {
        return new ResponseEntity<>(cryptoSessionService.decipherByKeys(userMessage.getResponseRequestMessage(), userMessage.getRsaKeys()), HttpStatus.OK);
    }
    @PostMapping("/hash")
    public ResponseEntity<ResponseRequestMessage> getHash(@RequestBody ResponseRequestMessage responseRequestMessage) {
        return new ResponseEntity<>(cryptoSessionService.getHashForResponseRequestMessage(responseRequestMessage), HttpStatus.OK);
    }
}
