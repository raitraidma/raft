package io.github.raitraidma.raft.transport.client;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClientConfiguration {
  private String host;
  private int port;
}
