package io.github.raitraidma.raft.transport.server;

import io.github.raitraidma.raft.transport.exception.MissingConfigurationException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Objects;

@Slf4j
public class Server {
  private ServerConfiguration configuration;
  private Selector selector;
  private ByteBuffer readBuffer = ByteBuffer.allocate(1024);

  public Server(ServerConfiguration configuration) {
    checkConfiguration(configuration);
    this.configuration = configuration;
  }

  private void checkConfiguration(ServerConfiguration configuration) {
    if (Objects.isNull(configuration.getRequestHandler())) {
      throw new MissingConfigurationException("Request handler is missing from configuration");
    }
    if (Objects.isNull(configuration.getHost()) || configuration.getHost().isEmpty()) {
      throw new MissingConfigurationException("Host is missing from configuration");
    }
    if (configuration.getPort() == 0) {
      throw new MissingConfigurationException("Port is missing from configuration");
    }
  }

  public void start() throws IOException {
    log.info("Starting server");
    try(Selector autoClosableSelector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
      selector = autoClosableSelector;

      configureServerSocketChannel(selector, serverSocketChannel);

      log.info("Server started. Listening {}:{}", configuration.getHost(), configuration.getPort());
      while (selector.isOpen()) {
        selector.select();
        Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();

        while (selectedKeys.hasNext()){
          SelectionKey key = selectedKeys.next();
          selectedKeys.remove();

          if (!key.isValid()) {
            continue;
          }

          if (key.isAcceptable()) {
            handleAccept(key);
          } else if (key.isReadable()) {
            handleRead(key);
          } else if (key.isWritable()) {
            handleWrite(key);
          }
        }
      }
    } catch (ClosedSelectorException ignore) {}
    log.info("Server closed");
  }

  private void handleWrite(SelectionKey key) {
    Session session = (Session)key.attachment();
    try {
      session.sendResponse(key);
    } catch (IOException e) {
      log.info("Failed to send response: {}", e.getMessage());
    }
  }

  private void configureServerSocketChannel(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
    InetSocketAddress address = new InetSocketAddress(configuration.getHost(), configuration.getPort());
    serverSocketChannel.bind(address);
    serverSocketChannel.configureBlocking(false);
    serverSocketChannel.register(selector, serverSocketChannel.validOps(), null);
  }

  private void handleAccept(SelectionKey key) {
    try {
      Session session = new Session(readBuffer, configuration.getRequestHandler());
      SocketChannel channel = session.accept(key);
      log.info("Client connected. Session: {}. Address: {}", session, channel.getRemoteAddress());
    } catch (IOException e) {
      log.error("Failed to connect to the client: {}", e.getMessage());
    }
  }

  private void handleRead(SelectionKey key) {
    Session session = (Session)key.attachment();
    try {
      if (session.read(key) < 0) {
        session.close(key);
        log.info("Closed channel. Session: {}", session);
      }
    } catch (IOException e) {
      log.error("Failed to read data: {}", e.getMessage());
      try {
        session.close(key);
        log.info("Closed channel. Session: {}", session);
      } catch (IOException e1) {}
    }
  }

  public void close() throws IOException {
    log.info("Closing server");
    try {
      if (selector != null) {
        selector.close();
      }
    } catch (IOException ignore) {}
  }

  public boolean isRunning() {
    return selector != null && selector.isOpen();
  }
}
