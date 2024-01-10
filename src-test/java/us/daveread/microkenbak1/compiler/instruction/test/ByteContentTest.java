package us.daveread.microkenbak1.compiler.instruction.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import us.daveread.microkenbak1.compiler.instruction.Label;
import us.daveread.microkenbak1.compiler.instruction.ByteContent;

/**
 * Unit tests for the ByteContent class.
 * 
 * @author readda
 *
 */
public class ByteContentTest {
  private ByteContent testInstruction;

  /**
   * Create a default instruction.
   */
  @Before
  public void setup() {
    testInstruction = new Label("test");
  }

  /**
   * Test that the default memory location is correct.
   */
  @Test
  public void testDefaultMemoryLocation() {
    assertEquals("Incorrect default memory location", 0,
        testInstruction.getMemoryLocation());
  }

  /**
   * Test that the set memory location is correct.
   */
  @Test
  public void testSetMemoryLocation() {
    testInstruction.setLocation(010);
    assertEquals("Incorrect default memory location", 8,
        testInstruction.getMemoryLocation());
  }
}
