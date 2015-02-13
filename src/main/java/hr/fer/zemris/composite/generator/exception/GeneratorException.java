package hr.fer.zemris.composite.generator.exception;

public class GeneratorException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public GeneratorException() {}

  public GeneratorException(String msg) {
    super(msg);
  }

  public GeneratorException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public GeneratorException(String message, Throwable cause) {
    super(message, cause);
  }

  public GeneratorException(Throwable cause) {
    super(cause);
  }
}
