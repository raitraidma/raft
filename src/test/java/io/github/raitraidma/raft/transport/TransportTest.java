package io.github.raitraidma.raft.transport;

import io.github.raitraidma.raft.transport.client.Client;
import io.github.raitraidma.raft.transport.client.ClientConfiguration;
import io.github.raitraidma.raft.transport.server.RequestHandler;
import io.github.raitraidma.raft.transport.server.Server;
import io.github.raitraidma.raft.transport.server.ServerConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

public class TransportTest {
  @Test
  void serverClientCommunication_whenClientSendsRequestToServer_thenServerHandlesRequestAndSendsBackResponse() throws Exception {
    ServerConfiguration serverConfiguration = ServerConfiguration.builder()
        .host("localhost")
        .port(8888)
        .requestHandler(new RequestHandler() {
          @Override
          public Message handle(Message request) {
            return request;
          }
        })
        .build();

    Server server = new Server(serverConfiguration);

    assertFalse(server.isRunning());

    new Thread(() -> {
      try {
        server.start();
      } catch (IOException e) {}
    }).start();

    int maxWaitIterations = 300;
    while (!server.isRunning() && maxWaitIterations-- > 0) {
      Thread.sleep(10);
    }

    assertTrue(server.isRunning());

    ClientConfiguration clientConfiguration = ClientConfiguration.builder()
        .host("localhost")
        .port(8888)
        .build();
    Client client = new Client(clientConfiguration);
    client.connect();

    for (int i = 0; i < 100; i++) {
      byte[] message = ByteBuffer.allocate(Integer.BYTES).putInt(i).array();
      client.sendMessage(new Message(message));
      assertArrayEquals(message, client.readMessage().getMessage());
    }

    client.close();
    server.close();

    assertFalse(server.isRunning());
  }
}
