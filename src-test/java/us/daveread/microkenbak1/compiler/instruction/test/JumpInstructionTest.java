package us.daveread.microkenbak1.compiler.instruction.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import us.daveread.microkenbak1.compiler.instruction.JumpInstruction;
import us.daveread.microkenbak1.compiler.instruction.JumpType;

/**
 * Unit tests for the JumpInstruction class.
 * 
 * @author readda
 *
 */
public class JumpInstructionTest {
  private JumpInstruction testInstruction;

  /**
   * Create a default unconditional jump.
   */
  @Before
  public void setup() {
    testInstruction = new JumpInstruction(JumpType.UNCONDITIONAL, "Top");
  }

  /**
   * Test that the correct label name is returned.
   */
  @Test
  public void testGetName() {
    assertEquals("Incorrect jump name", "Top",
        testInstruction.getName());
  }

  /**
   * Test that the correct operating codes are returned for an unconditional
   * jump.
   */
  @Test
  public void testGetFormattedOpForUnconditionalJump() {
    testInstruction.setMemoryLocation(045);
    testInstruction.setDestinationAddress(04);
    assertEquals("Incorrect formatted operation code", "0344\n0004",
        testInstruction.getFormattedOp());
  }
}
