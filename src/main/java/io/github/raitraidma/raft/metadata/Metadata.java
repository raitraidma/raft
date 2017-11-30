package io.github.raitraidma.raft.metadata;

public interface Metadata {
  long getCurrentTerm() throws MetadataException;
  void setCurrentTerm(long currentTerm) throws MetadataException;
  long getVotedFor() throws MetadataException;
  void setVotedFor(long votedFor) throws MetadataException;
}
