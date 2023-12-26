package us.daveread.microkenbak1.compiler.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import us.daveread.microkenbak1.compiler.Label;
import us.daveread.microkenbak1.compiler.OpCodes;

/**
 * Unit tests for the OpCodes class.
 * 
 * @author readda
 *
 */
public class OpCodesTest {
  private OpCodes testInstruction;

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
    testInstruction.setMemoryLocation(010);
    assertEquals("Incorrect default memory location", 8,
        testInstruction.getMemoryLocation());
  }
}
