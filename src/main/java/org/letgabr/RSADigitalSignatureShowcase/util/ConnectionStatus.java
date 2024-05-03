package org.letgabr.RSADigitalSignatureShowcase.util;

public enum ConnectionStatus {
    CONNECTION_REQUEST("connectionRequest"),
    CONNECTION_ACCEPT("connectionAccept"),
    CONNECTION_CONFIRM("connectionConfirm"),
    CONNECTED("connected"),
    NO_CONNECTION("");
    private final String title;
    ConnectionStatus(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
}
