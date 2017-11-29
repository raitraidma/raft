package io.github.raitraidma.raft.transport.exception;

public class MissingConfigurationException extends RuntimeException {
  public MissingConfigurationException(String message) {
    super(message);
  }
}
