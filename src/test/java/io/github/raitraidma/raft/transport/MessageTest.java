package io.github.raitraidma.raft.transport;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageTest {
  @Test
  void length_whenMessageIsCreatedBasedOnByteArray_thenReturnLengthOfGivenArray() {
    assertEquals(4, new Message(new byte[4]).length());
    assertEquals(10, new Message(new byte[10]).length());
    assertEquals(120, new Message(new byte[120]).length());
  }
}