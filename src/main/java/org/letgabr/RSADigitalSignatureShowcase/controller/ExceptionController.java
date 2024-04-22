package org.letgabr.RSADigitalSignatureShowcase.controller;

import org.letgabr.RSADigitalSignatureShowcase.exception.CompositeNumberException;
import org.letgabr.RSADigitalSignatureShowcase.exception.LargeNumberException;
import org.letgabr.RSADigitalSignatureShowcase.exception.NoUserKeysException;
import org.letgabr.RSADigitalSignatureShowcase.exception.NotConnectedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController
{
    @ExceptionHandler({CompositeNumberException.class, LargeNumberException.class, NoUserKeysException.class, NotConnectedException.class})
    public ResponseEntity<String> exceptionHandle(RuntimeException runtimeException) {
        return new ResponseEntity<>(runtimeException.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
