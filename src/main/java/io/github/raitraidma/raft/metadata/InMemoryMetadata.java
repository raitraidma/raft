package io.github.raitraidma.raft.metadata;

public class InMemoryMetadata implements Metadata {
  private long currentTerm = 0;
  private long votedFor = 0;

  @Override
  public long getCurrentTerm() {
    return currentTerm;
  }

  @Override
  public void setCurrentTerm(long currentTerm) throws MetadataException {
    if (this.currentTerm >= currentTerm) {
      throw new MetadataException("New term must be greater than current term");
    }
    this.currentTerm = currentTerm;
  }

  @Override
  public long getVotedFor() {
    return votedFor;
  }

  @Override
  public void setVotedFor(long votedFor) {
    this.votedFor = votedFor;
  }
}
