package io.github.raitraidma.raft.transport.server;

import io.github.raitraidma.raft.transport.exception.MissingConfigurationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServerTest {
  @Test
  void checkConfiguration_whenRequestHandlerIsMissing_thenThrowAnException() {
    MissingConfigurationException exception = assertThrows(MissingConfigurationException.class, () -> {
      ServerConfiguration configuration = ServerConfiguration.builder()
          .host("localhost")
          .port(8888)
          .build();
      new Server(configuration);
    });

    assertEquals("Request handler is missing from configuration", exception.getMessage());
  }

  @Test
  void checkConfiguration_whenHostIsMissing_thenThrowAnException() {
    MissingConfigurationException exception = assertThrows(MissingConfigurationException.class, () -> {
      ServerConfiguration configuration = ServerConfiguration.builder()
          .port(8888)
          .requestHandler(request -> request)
          .build();
      new Server(configuration);
    });

    assertEquals("Host is missing from configuration", exception.getMessage());
  }

  @Test
  void checkConfiguration_whenPortIsMissing_thenThrowAnException() {
    MissingConfigurationException exception = assertThrows(MissingConfigurationException.class, () -> {
      ServerConfiguration configuration = ServerConfiguration.builder()
          .host("localhost")
          .requestHandler(request -> request)
          .build();
      new Server(configuration);
    });

    assertEquals("Port is missing from configuration", exception.getMessage());
  }

  @Test
  void checkConfiguration_whenNothingIsMissingFromConfiguration_thenDoNotThrowAnException() {
    ServerConfiguration configuration = ServerConfiguration.builder()
        .host("localhost")
        .port(8080)
        .requestHandler(request -> request)
        .build();
    new Server(configuration);
  }
}