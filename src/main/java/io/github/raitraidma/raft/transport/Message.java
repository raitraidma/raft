package io.github.raitraidma.raft.transport;

public class Message {
  private byte[] message;

  public Message(byte[] message) {
    this.message = message;
  }

  public int length() {
    return message.length;
  }

  public byte[] getMessage() {
    return message;
  }
}
