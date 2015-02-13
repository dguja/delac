package hr.fer.zemris.composite.generator.exception;

public class ParseException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ParseException() {}

  public ParseException(String msg) {
    super(msg);
  }

  public ParseException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public ParseException(String message, Throwable cause) {
    super(message, cause);
  }

  public ParseException(Throwable cause) {
    super(cause);
  }
}
