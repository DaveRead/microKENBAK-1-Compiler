package us.daveread.microkenbak1.compiler.instruction.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import us.daveread.microkenbak1.compiler.instruction.OperationInstruction;

/**
 * Unit tests for the OperationInstruction class.
 * 
 * @author readda
 *
 */
public class OperationInstructionTest {
  /**
   * A test instruction.
   */
  private OperationInstruction testInstruction;

  /**
   * Set up a test instruction.
   */
  @Before
  public void setup() {
    testInstruction = new OperationInstruction(023);
  }

  /**
   * Check that the correct operation code is returned.
   */
  @Test
  public void testGetOperationCode() {
    assertEquals("Incorrect operation code", 023,
        testInstruction.getOperationCode());
  }

  /**
   * Check that the operating code is properly formatted for export to the
   * microKenbek-1 computer.
   */
  @Test
  public void testGetFormattedOp() {
    testInstruction.setLocation(05);
    assertEquals("Incorrect formatted operation code", "0023",
        testInstruction.getFormattedByte());
  }

  /**
   * Check that the toString value includes the calculated memory location and
   * operating code.
   */
  @Test
  public void testToString() {
    testInstruction.setLocation(05);
    assertEquals("Incorrect formatted operation code", "0005: 0023",
        testInstruction.toString());
  }
}
