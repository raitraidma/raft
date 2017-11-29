package io.github.raitraidma.raft.transport.client;

import io.github.raitraidma.raft.transport.Message;
import io.github.raitraidma.raft.transport.exception.NotConnectedException;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

@Slf4j
public class Client {
  private ClientConfiguration configuration;
  private volatile boolean isConnected;
  private Socket socket;
  private DataOutputStream output;
  private DataInputStream input;

  public Client(ClientConfiguration configuration) {
    this.configuration = configuration;
  }

  public void connect() throws IOException {
    InetAddress address = InetAddress.getByName(configuration.getHost());
    socket = new Socket(address, configuration.getPort());
    input = new DataInputStream((socket.getInputStream()));
    output = new DataOutputStream((socket.getOutputStream()));
    isConnected = true;
    log.info("Connected to: {}", address);
  }

  public void close() throws IOException {
    try {
      socket.close();
    } finally {
      isConnected = false;
    }
  }

  public void sendMessage(Message message) throws IOException {
    if (!isConnected) {
      throw new NotConnectedException("Client is not connected to the server");
    }
    output.write(ByteBuffer.allocate(4).putInt(message.length()).array());
    output.write(message.getMessage());
    output.flush();
  }

  public Message readMessage() throws IOException {
    int messageSize = input.readInt();
    byte[] message = new byte[messageSize];
    int read = 0;
    int bytesRead = 0;
    while (true) {
      read = input.read(message, bytesRead, messageSize-bytesRead);
      if (read < 0) {
        break;
      }
      bytesRead += read;
      if (bytesRead >= messageSize) {
        break;
      }
    }
    return new Message(message);
  }
}
