package com.farmtomarket.application.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorMessage {
    String message;
    String errorCode;
}
