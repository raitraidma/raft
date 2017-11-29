package io.github.raitraidma.raft.transport.client;

import io.github.raitraidma.raft.transport.exception.NotConnectedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ClientTest {
  @Test
  void sendMessage_whenClientIsNotConnected_thenThrowAnException() {
    Assertions.assertThrows(NotConnectedException.class, () -> {
      new Client(null).sendMessage(null);
    });
  }
}