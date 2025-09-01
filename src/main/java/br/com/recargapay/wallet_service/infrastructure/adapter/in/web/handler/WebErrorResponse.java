package br.com.recargapay.wallet_service.infrastructure.adapter.in.web.handler;

import java.time.LocalDateTime;

public class WebErrorResponse {

    private final int status;
    private final String message;
    private final LocalDateTime timestamp;

    public WebErrorResponse(int status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
