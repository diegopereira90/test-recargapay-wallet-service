package br.com.recargapay.wallet_service.infrastructure.adapter.in.web.handler;

import br.com.recargapay.wallet_service.domain.exception.InsufficientBalanceException;
import br.com.recargapay.wallet_service.domain.exception.WalletNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<WebErrorResponse> handleWalletNotFound(WalletNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<WebErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<WebErrorResponse> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
    }

    private ResponseEntity<WebErrorResponse> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
            .body(new WebErrorResponse(status.value(), message, LocalDateTime.now()));
    }
}
