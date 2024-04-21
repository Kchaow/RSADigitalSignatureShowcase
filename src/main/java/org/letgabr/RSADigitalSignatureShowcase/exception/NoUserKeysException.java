package org.letgabr.RSADigitalSignatureShowcase.exception;

public class NoUserKeysException extends RuntimeException {
    public NoUserKeysException() {
        super();
    }
    public NoUserKeysException(String message) {
        super(message);
    }
}
