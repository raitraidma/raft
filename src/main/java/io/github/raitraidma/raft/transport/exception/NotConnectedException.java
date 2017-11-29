package io.github.raitraidma.raft.transport.exception;

public class NotConnectedException extends RuntimeException {
  public NotConnectedException(String message) {
    super(message);
  }
}
