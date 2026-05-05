package com.ufp.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UfpException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus status;

    public UfpException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public static UfpException notFound(String message) {
        return new UfpException(message, "NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    public static UfpException badRequest(String message) {
        return new UfpException(message, "BAD_REQUEST", HttpStatus.BAD_REQUEST);
    }

    public static UfpException conflict(String message) {
        return new UfpException(message, "CONFLICT", HttpStatus.CONFLICT);
    }
}
