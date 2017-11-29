package io.github.raitraidma.raft.transport.server;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ServerConfiguration {
  private String host;
  private int port;
  private RequestHandler requestHandler;
}
