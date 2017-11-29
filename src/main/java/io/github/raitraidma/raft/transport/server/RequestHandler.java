package io.github.raitraidma.raft.transport.server;

import io.github.raitraidma.raft.transport.Message;

public interface RequestHandler {
  Message handle(Message request);
}
