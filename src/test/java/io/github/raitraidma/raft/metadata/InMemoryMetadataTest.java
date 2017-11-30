package io.github.raitraidma.raft.metadata;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryMetadataTest {
  @Test
  void currentTerm_whenSettingCurrentTerm_thenMustGetTheSameCurrentTerm() throws MetadataException {
    Metadata metadata = new InMemoryMetadata();
    metadata.setCurrentTerm(10);
    assertEquals(10, metadata.getCurrentTerm());
  }

  @Test
  void setCurrentTerm_whenNewTermIsNotGreaterThanCurrentTerm_thenThrowAnException() throws MetadataException {
    Metadata metadata = new InMemoryMetadata();
    metadata.setCurrentTerm(10);

    assertThrows(MetadataException.class, () -> {
      metadata.setCurrentTerm(10);
    });
  }

  @Test
  void votedFor_whenSettingVotedFor_thenMustGetTheSameVotedForm() throws MetadataException {
    Metadata metadata = new InMemoryMetadata();
    metadata.setVotedFor(20);
    assertEquals(20, metadata.getVotedFor());
  }

  @Test
  void getCurrentTerm_whenNewMetadataIsCreated_thenInitialValueMustBeZero() throws MetadataException {
    Metadata metadata = new InMemoryMetadata();
    assertEquals(0, metadata.getCurrentTerm());
  }

  @Test
  void getVotedFor_whenNewMetadataIsCreated_thenInitialValueMustBeZero() throws MetadataException {
    Metadata metadata = new InMemoryMetadata();
    assertEquals(0, metadata.getVotedFor());
  }
}