package io.github.raitraidma.raft.transport.server;

import io.github.raitraidma.raft.transport.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
class Session {
  private static AtomicLong sessionCounter = new AtomicLong();
  private long sessionId;

  private ByteBuffer readBuffer;

  private byte[] newMessageLength = new byte[Integer.BYTES];
  private int newMessageLengthSize = 0;
  private byte[] newMessage;
  private int newMessageSize = 0;
  private Message response;
  private RequestHandler requestHandler;

  public Session(ByteBuffer readBuffer, RequestHandler requestHandler) {
    sessionId = sessionCounter.incrementAndGet();
    this.readBuffer = readBuffer;
    this.requestHandler = requestHandler;
  }

  @Override
  public String toString() {
    return String.valueOf(sessionId);
  }

  public int read(SelectionKey key) throws IOException {
    SocketChannel channel = (SocketChannel)key.channel();
    int read;

    readBuffer.clear();
    while ((read = channel.read(readBuffer)) > 0) {
      readBuffer.flip();
      byte[] bytes = new byte[readBuffer.limit()];
      readBuffer.get(bytes);
      readBuffer.clear();
      int bytePosition = 0;

      while (bytePosition < bytes.length) {
        if (newMessageLengthSize < newMessageLength.length) {
          for (; newMessageLengthSize < newMessageLength.length && bytePosition < bytes.length; bytePosition++, newMessageLengthSize++) {
            newMessageLength[newMessageLengthSize] = bytes[bytePosition];
          }
          newMessage = new byte[ByteBuffer.wrap(this.newMessageLength).getInt()];
        }

        int bytesToCopy = Math.min(bytes.length - bytePosition, newMessage.length - newMessageSize);
        System.arraycopy(bytes, bytePosition, newMessage, newMessageSize, bytesToCopy);
        newMessageSize += bytesToCopy;
        bytePosition += bytesToCopy;

        if (newMessage.length == newMessageSize) {
          handleRequest(new Message(newMessage));
          newMessageLengthSize = 0;
          newMessageSize = 0;
          channel.register(key.selector(), SelectionKey.OP_WRITE, this);
        }
      }
    }
    return read;
  }

  private void handleRequest(Message requestMessage) {
    response = requestHandler.handle(requestMessage);
    log.debug("Handle request");
  }

  void sendResponse(SelectionKey key) throws IOException {
    SocketChannel channel = (SocketChannel)key.channel();
    try {
      ByteBuffer message = ByteBuffer.allocate(Integer.BYTES + response.length());
      message.putInt(response.length());
      message.put(response.getMessage());
      message.rewind();
      int write;
      int writtenBytes = 0;
      while(writtenBytes < message.capacity()) {
        write = channel.write(message);
        if (write > 0) {
          writtenBytes += write;
        }
      }
    } finally {
      response = null;
    }
    channel.register(key.selector(), SelectionKey.OP_READ, this);
  }

  SocketChannel accept(SelectionKey key) throws IOException {
    ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
    SocketChannel channel = serverSocketChannel.accept();
    channel.configureBlocking(false);
    channel.register(key.selector(), SelectionKey.OP_READ, this);
    return channel;
  }

  public void close(SelectionKey key) throws IOException {
    try {
      key.channel().close();
    } finally {
      key.cancel();
    }
  }
}
