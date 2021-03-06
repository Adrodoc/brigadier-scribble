package de.adrodoc.brigadier.exceptions;

public class ParseException extends Exception {
  private static final long serialVersionUID = 1L;

  public ParseException() {}

  public ParseException(String message) {
    super(message);
  }

  public ParseException(Throwable cause) {
    super(cause);
  }

  public ParseException(String message, Throwable cause) {
    super(message, cause);
  }

  protected ParseException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
