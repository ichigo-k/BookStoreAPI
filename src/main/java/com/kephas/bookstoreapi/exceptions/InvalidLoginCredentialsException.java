package com.kephas.bookstoreapi.exceptions;

import org.springframework.security.core.AuthenticationException;

public class InvalidLoginCredentialsException extends AuthenticationException {


  public InvalidLoginCredentialsException(String message) {
    super(message);
  }


  public InvalidLoginCredentialsException(String message, Throwable cause) {
    super(message, cause);
  }
}
