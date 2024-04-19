package org.letgabr.RSADigitalSignatureShowcase.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.letgabr.RSADigitalSignatureShowcase.dto.RSAKeys;
import org.letgabr.RSADigitalSignatureShowcase.dto.RSAPrimes;
import org.letgabr.RSADigitalSignatureShowcase.service.CryptoSessionService;
import org.letgabr.RSADigitalSignatureShowcase.util.MathCrypto;
import org.letgabr.RSADigitalSignatureShowcase.util.PrimeTester;
import org.letgabr.RSADigitalSignatureShowcase.util.RSACryptoSystem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
public class CryptoController
{
    final int length = 512;
    final PrimeTester primeTester;
    final CryptoSessionService cryptoSessionService;
    public CryptoController(PrimeTester primeTester, CryptoSessionService cryptoSessionService) {
        this.primeTester = primeTester;
        this.cryptoSessionService = cryptoSessionService;
    }
    @GetMapping("/primes")
    public ResponseEntity<RSAPrimes> getPrimes(HttpServletRequest httpServletRequest) {
        return new ResponseEntity<>(cryptoSessionService.getPrimes(httpServletRequest), HttpStatus.OK);
    }

    @PostMapping("/primes-check")
    public ResponseEntity<Object> primeCheck(@RequestBody RSAPrimes rsaPrimes)
    {
        BigInteger p = new BigInteger(rsaPrimes.p());
        BigInteger q = new BigInteger(rsaPrimes.q());
        if (p.bitLength() > length || q.bitLength() > length)
            return new ResponseEntity<>("p or q too big", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(new RSAPrimes(primeTester.isPrime(p) ? "true" : "false",
                primeTester.isPrime(q) ? "true" : "false"),
                HttpStatus.OK);
    }

    @PostMapping("/keys")
    public ResponseEntity<Object> getKeysByPrimes(@RequestBody RSAPrimes rsaPrimes)
    {
        BigInteger p = new BigInteger(rsaPrimes.p());
        BigInteger q = new BigInteger(rsaPrimes.q());
        if (!primeTester.isPrime(p))
            return new ResponseEntity<>("p isn't prime", HttpStatus.BAD_REQUEST);
        if (!primeTester.isPrime(q))
            return new ResponseEntity<>("q isn't prime", HttpStatus.BAD_REQUEST);
        RSACryptoSystem rsaCryptoSystem = new RSACryptoSystem(p, q);
        return new ResponseEntity<>(new RSAKeys(rsaCryptoSystem.getPrivateKey().toString(),
                rsaCryptoSystem.getPublicKey().toString(),
                rsaCryptoSystem.getN().toString()),
                HttpStatus.OK);
    }
}
