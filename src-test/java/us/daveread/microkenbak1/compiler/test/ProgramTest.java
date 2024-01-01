package us.daveread.microkenbak1.compiler.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import us.daveread.microkenbak1.compiler.Program;
import us.daveread.microkenbak1.compiler.Statement;
import us.daveread.microkenbak1.compiler.instruction.JumpInstruction;
import us.daveread.microkenbak1.compiler.instruction.Label;
import us.daveread.microkenbak1.compiler.instruction.OpCodes;

/**
 * Unit tests for the Program class.
 * 
 * @author readda
 *
 */
public class ProgramTest {

  /**
   * Test addition of statements.
   */
  @Test
  public void testLabelAndLetStatements() {
    Program prog = new Program();
    prog.addStatement(new Statement("LABEL top".split(" ")));
    prog.addStatement(new Statement("LET A = 0105".split(" ")));
    prog.addStatement(new Statement("LET B = 01".split(" ")));

    Statement[] stmts = prog.getStatements();

    assertEquals("Incorrect number of stmts in program", 3, stmts.length);

    int memoryLoc = 4;

    for (Statement stmt : stmts) {
      for (OpCodes inst : stmt.getOpCodes()) {
        inst.setMemoryLocation(memoryLoc);
        if (!(inst instanceof Label)) {
          memoryLoc++;
        }
      }
    }

    assertEquals("Incorrect ending memory location", 8, memoryLoc);

    OpCodes[] inst = stmts[2].getOpCodes();
    assertEquals("Incorrect first formatted operation for second statement",
        "0006: 0123", inst[0].toString());
    assertEquals("Incorrect second formatted operation for second statement",
        "0007: 0001", inst[1].toString());
  }

  /**
   * Test output of operating codes without EOF signal (e.g., no "s" added at
   * end of output)
   */
  @Test
  public void testGetInstructionsNoEof() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET A = 0105".split(" ")));
    prog.addStatement(new Statement("LET B = 01".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0023\n0105\n0123\n0001\n";

    assertEquals("Incorrect instructions without EOF signal", expected,
        instructions);
  }

  /**
   * Test output of operating codes with EOF signal (adding "s" as last line of
   * output, used by microKenbak-1 upload feature to detect end of program)
   */
  @Test
  public void testGetInstructionsWithEof() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET B = 0217".split(" ")));
    prog.addStatement(new Statement("LET A = 0035".split(" ")));
    prog.addStatement(new Statement("LET X = 0177".split(" ")));

    String instructions = prog.getInstructions(true);

    String expected = "0000\n0000\n0000\n0004\n0123\n0217\n0023\n0035\n0223\n0177\ns\n";

    assertEquals("Incorrect instructions with EOF signal", expected,
        instructions);
  }

  /**
   * Test output of operating codes for unconditional jump to label located
   * before the jump point.
   */
  @Test
  public void testGetInstructionsWithLegalJumpPredefined() {
    Program prog = new Program();
    prog.addStatement(new Statement("LABEL begin".split(" ")));
    prog.addStatement(new Statement("LET B = 0217".split(" ")));
    prog.addStatement(new Statement("LET A = 0035".split(" ")));
    prog.addStatement(new Statement("LET X = 0177".split(" ")));
    prog.addStatement(new Statement("GOTO begin".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0123\n0217\n0023\n0035\n0223\n0177\n0344\n0004\n";

    assertEquals("Incorrect instructions using predefined label", expected,
        instructions);
  }

  /**
   * Test output of operating codes for unconditional jump to label located
   * after the jump point.
   */
  @Test
  public void testGetInstructionsWithLegalJumpPostdefined() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET B = 0217".split(" ")));
    prog.addStatement(new Statement("GOTO skipA".split(" ")));
    prog.addStatement(new Statement("LET A = 0035".split(" ")));
    prog.addStatement(new Statement("LABEL skipA".split(" ")));
    prog.addStatement(new Statement("LET X = 0177".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0123\n0217\n0344\n0012\n0023\n0035\n0223\n0177\n";

    assertEquals("Incorrect instructions using post-defined label", expected,
        instructions);
  }

  /**
   * Test detection of missing jump label name.
   */
  @Test(expected = IllegalStateException.class)
  public void testGetInstructionsMissingJumpLabelName() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET B = 0217".split(" ")));
    prog.addStatement(new Statement("LET A = 0035".split(" ")));
    prog.addStatement(new Statement("LET X = 0177".split(" ")));
    prog.addStatement(new Statement("GOTO".split(" ")));

    prog.getInstructions(false);

    fail("Missing jump label not correctly identified");
  }

  /**
   * Test detection of jump label name with spaces.
   */
  @Test(expected = IllegalStateException.class)
  public void testGetInstructionsJumpLabelNameWithSpaces() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET B = 0217".split(" ")));
    prog.addStatement(new Statement("LET A = 0035".split(" ")));
    prog.addStatement(new Statement("LET X = 0177".split(" ")));
    prog.addStatement(new Statement("GOTO Top_Of loop".split(" ")));

    prog.getInstructions(false);

    fail("Jump label with spaces not correctly identified");
  }

  /**
   * Test detection of missing jump label.
   */
  @Test(expected = IllegalStateException.class)
  public void testGetInstructionsMissingJumpTarget() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET B = 0217".split(" ")));
    prog.addStatement(new Statement("LET A = 0035".split(" ")));
    prog.addStatement(new Statement("LET X = 0177".split(" ")));
    prog.addStatement(new Statement("GOTO begin".split(" ")));

    prog.getInstructions(false);

    fail("Missing jump target not correctly identified");
  }

  /**
   * Test detection of duplicate jump label.
   */
  @Test(expected = IllegalStateException.class)
  public void testGetInstructionsDuplicatedJumpTarget() {
    Program prog = new Program();
    prog.addStatement(new Statement("LABEL begin".split(" ")));
    prog.addStatement(new Statement("LET B = 0217".split(" ")));
    prog.addStatement(new Statement("LET A = 0035".split(" ")));
    prog.addStatement(new Statement("LABEL begin".split(" ")));
    prog.addStatement(new Statement("LET X = 0177".split(" ")));
    prog.addStatement(new Statement("GOTO begin".split(" ")));

    prog.getInstructions(false);

    fail("Duplicated jump target not correctly identified");
  }

  /**
   * Test output of operating codes for SYSCALL. Uses decimal values.
   */
  @Test
  public void testGetInstructionsForSysCall() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET A = 17".split(" ")));
    prog.addStatement(new Statement("SYSCALL".split(" ")));
    prog.addStatement(new Statement("LET A = 144".split(" ")));
    prog.addStatement(new Statement("LET B = 128".split(" ")));
    prog.addStatement(new Statement("SYSCALL".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0023\n0021\n0360\n0023\n0220\n0123\n0200\n0360\n";

    assertEquals("Incorrect instructions with SYSCALL", expected, instructions);
  }

  /**
   * Test output of operating codes for MEMCOPY of A to B.
   */
  @Test
  public void testGetInstructionsForMemcopyAtoB() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET A = 021".split(" ")));
    prog.addStatement(new Statement("MEMCOPY A TO B".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0023\n0021\n0034\n0001\n";

    assertEquals("Incorrect instructions with syscall", expected, instructions);
  }

  /**
   * Test output of operating codes for MEMCOPY of B to A.
   */
  @Test
  public void testGetInstructionsForMemcopyBtoA() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET B = 0146".split(" ")));
    prog.addStatement(new Statement("MEMCOPY B TO A".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0123\n0146\n0134\n0000\n";

    assertEquals("Incorrect instructions with MEMCOPY", expected, instructions);
  }

  /**
   * Test output of operating codes for MEMCOPY of B to DISPLAY.
   */
  @Test
  public void testGetInstructionsForMemcopyBtoDisplay() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET B = 0252".split(" ")));
    prog.addStatement(new Statement("MEMCOPY B TO DISPLAY".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0123\n0252\n0134\n0200\n";

    assertEquals("Incorrect instructions with syscall", expected, instructions);
  }

  /**
   * Test output of operating codes for AND.
   */
  @Test
  public void testGetInstructionsForAnd() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET A = 0252".split(" ")));
    prog.addStatement(new Statement("AND 0143".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0023\n0252\n0323\n0143\n";

    assertEquals("Incorrect instructions with AND", expected, instructions);
  }

  /**
   * Test output of operating codes for ADD literal to A.
   */
  @Test
  public void testGetInstructionsForAddLiteralToA() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET A = 0210".split(" ")));
    prog.addStatement(new Statement("ADD 014 TO A".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0023\n0210\n0003\n0014\n";

    assertEquals("Incorrect instructions with ADD", expected, instructions);
  }

  /**
   * Test output of operating codes for ADD to B.
   */
  @Test
  public void testGetInstructionsForAddLiteralToB() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET B = 0210".split(" ")));
    prog.addStatement(new Statement("ADD 022 TO B".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0123\n0210\n0103\n0022\n";

    assertEquals("Incorrect instructions with ADD", expected, instructions);
  }

  /**
   * Test output of operating codes for ADD to X. Uses hexadecimal values.
   */
  @Test
  public void testGetInstructionsForAddLiteralToX() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET X = 0x8B".split(" ")));
    prog.addStatement(new Statement("ADD 0x40 TO X".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0223\n0213\n0203\n0100\n";

    assertEquals("Incorrect instructions with ADD", expected, instructions);
  }

  /**
   * Test output of operating codes for ADD A to A.
   */
  @Test
  public void testGetInstructionsForAddAToA() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET A = 0210".split(" ")));
    prog.addStatement(new Statement("ADD A TO A".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0023\n0210\n0004\n0000\n";

    assertEquals("Incorrect instructions with ADD", expected, instructions);
  }

  /**
   * Test output of operating codes for ADD B to A.
   */
  @Test
  public void testGetInstructionsForAddBToA() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET A = 0210".split(" ")));
    prog.addStatement(new Statement("LET B = 03".split(" ")));
    prog.addStatement(new Statement("ADD B TO A".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0023\n0210\n0123\n0003\n0004\n0001\n";

    assertEquals("Incorrect instructions with ADD", expected, instructions);
  }

  /**
   * Test output of operating codes for ADD A to X.
   */
  @Test
  public void testGetInstructionsForAddAToX() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET A = 0210".split(" ")));
    prog.addStatement(new Statement("LET X = 03".split(" ")));
    prog.addStatement(new Statement("ADD A TO X".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0023\n0210\n0223\n0003\n0204\n0000\n";

    assertEquals("Incorrect instructions with ADD", expected, instructions);
  }

  /**
   * Test output of operating codes for ADD X to B.
   */
  @Test
  public void testGetInstructionsForAddXToB() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET B = 0210".split(" ")));
    prog.addStatement(new Statement("LET X = 03".split(" ")));
    prog.addStatement(new Statement("ADD X TO B".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0123\n0210\n0223\n0003\n0104\n0002\n";

    assertEquals("Incorrect instructions with ADD", expected, instructions);
  }

  /**
   * Test output of operating codes for SUBTRACT literal from A.
   */
  @Test
  public void testGetInstructionsForSubtractLiteralFromA() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET A = 0210".split(" ")));
    prog.addStatement(new Statement("SUBTRACT 014 FROM A".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0023\n0210\n0013\n0014\n";

    assertEquals("Incorrect instructions with SUBTRACT", expected,
        instructions);
  }

  /**
   * Test output of operating codes for SUBTRACT literal from B.
   */
  @Test
  public void testGetInstructionsForSubtractLiteralFromB() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET B = 0210".split(" ")));
    prog.addStatement(new Statement("SUBTRACT 022 FROM B".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0123\n0210\n0113\n0022\n";

    assertEquals("Incorrect instructions with SUBTRACT", expected,
        instructions);
  }

  /**
   * Test output of operating codes for SUBTRACT literal from X.
   */
  @Test
  public void testGetInstructionsForSubtractLiteralFromX() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET X = 0213".split(" ")));
    prog.addStatement(new Statement("ADD 0100 TO X".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0223\n0213\n0203\n0100\n";

    assertEquals("Incorrect instructions with SUBTRACT", expected,
        instructions);
  }

  /**
   * Test output of operating codes for SUBTRACT A from A.
   */
  @Test
  public void testGetInstructionsForSubtractAFromA() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET A = 0210".split(" ")));
    prog.addStatement(new Statement("SUBTRACT A FROM A".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0023\n0210\n0014\n0000\n";

    assertEquals("Incorrect instructions with SUBTRACT", expected,
        instructions);
  }

  /**
   * Test output of operating codes for SUBTRACT B from A.
   */
  @Test
  public void testGetInstructionsForSubtractBFromA() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET A = 0210".split(" ")));
    prog.addStatement(new Statement("LET B = 3".split(" ")));
    prog.addStatement(new Statement("SUBTRACT B FROM A".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0023\n0210\n0123\n0003\n0014\n0001\n";

    assertEquals("Incorrect instructions with SUBTRACT", expected,
        instructions);
  }

  /**
   * Test output of operating codes for SUBTRACT A from X.
   */
  @Test
  public void testGetInstructionsForSubtractAFromX() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET A = 0210".split(" ")));
    prog.addStatement(new Statement("LET X = 3".split(" ")));
    prog.addStatement(new Statement("SUBTRACT A FROM X".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0023\n0210\n0223\n0003\n0214\n0000\n";

    assertEquals("Incorrect instructions with SUBTRACT", expected,
        instructions);
  }

  /**
   * Test output of operating codes for conditional jump if A is zero.
   */
  @Test
  public void testGetInstructionsForIfAIsZero() {
    Program prog = new Program();
    prog.addStatement(new Statement("LABEL TopLoop".split(" ")));
    prog.addStatement(new Statement("IF A ISZERO GOTO TopLoop".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0044\n0004\n";

    assertEquals("Incorrect instructions with IF A ISZERO", expected,
        instructions);
  }

  /**
   * Test output of operating codes for conditional jump if A is not zero.
   */
  @Test
  public void testGetInstructionsForIfANotZero() {
    Program prog = new Program();
    prog.addStatement(new Statement("LABEL TopLoop".split(" ")));
    prog.addStatement(new Statement("IF A NOTZERO GOTO TopLoop".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0043\n0004\n";

    assertEquals("Incorrect instructions with IF A NOTZERO", expected,
        instructions);
  }

  /**
   * Test output of operating codes for conditional jump if B is zero.
   */
  @Test
  public void testGetInstructionsForIfBIsZero() {
    Program prog = new Program();
    prog.addStatement(new Statement("LABEL TopLoop".split(" ")));
    prog.addStatement(new Statement("IF B ISZERO GOTO TopLoop".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0144\n0004\n";

    assertEquals("Incorrect instructions with IF B ISZERO", expected,
        instructions);
  }

  /**
   * Test output of operating codes for conditional jump if B is not zero.
   */
  @Test
  public void testGetInstructionsForIfBNotZero() {
    Program prog = new Program();
    prog.addStatement(new Statement("LABEL TopLoop".split(" ")));
    prog.addStatement(new Statement("IF B NOTZERO GOTO TopLoop".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0143\n0004\n";

    assertEquals("Incorrect instructions with IF B ISZERO", expected,
        instructions);
  }

  /**
   * Test output of operating codes for conditional jump if X is zero.
   */
  @Test
  public void testGetInstructionsForIfXIsZero() {
    Program prog = new Program();
    prog.addStatement(new Statement("LABEL TopLoop".split(" ")));
    prog.addStatement(new Statement("IF X ISZERO GOTO TopLoop".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0244\n0004\n";

    assertEquals("Incorrect instructions with IF X ISZERO", expected,
        instructions);
  }

  /**
   * Test output of operating codes for conditional jump if X is not zero.
   */
  @Test
  public void testGetInstructionsForIfXNonZero() {
    Program prog = new Program();
    prog.addStatement(new Statement("LABEL TopLoop".split(" ")));
    prog.addStatement(new Statement("IF X NOTZERO GOTO TopLoop".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0243\n0004\n";

    assertEquals("Incorrect instructions with IF X NOTZERO", expected,
        instructions);
  }

  /**
   * Test output of operating codes for test of a conditional jump if A
   * overflowed.
   */
  @Test
  public void testGetInstructionsForIfOverflowA() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET A = 255".split(" ")));
    prog.addStatement(new Statement("LABEL TopLoop".split(" ")));
    prog.addStatement(new Statement("ADD 1 to A".split(" ")));
    prog.addStatement(new Statement("IF A OVERFLOW GOTO TopLoop".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0023\n0377\n0003\n0001\n0212\n0201\n0344\n0006\n";

    assertEquals("Incorrect instructions with IF X NOTZERO", expected,
        instructions);
  }

  /**
   * Test output of operating codes for test of a conditional jump if B
   * overflowed.
   */
  @Test
  public void testGetInstructionsForIfOverflowB() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET B = 0".split(" ")));
    prog.addStatement(new Statement("LABEL TopLoop".split(" ")));
    prog.addStatement(new Statement("SUBTRACT 1 from B".split(" ")));
    prog.addStatement(new Statement("IF B OVERFLOW GOTO TopLoop".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0123\n0000\n0113\n0001\n0212\n0202\n0344\n0006\n";

    assertEquals("Incorrect instructions with IF X NOTZERO", expected,
        instructions);
  }

  /**
   * Test output of operating codes for HALT.
   */
  @Test
  public void testGetInstructionsForHalt() {
    Program prog = new Program();
    prog.addStatement(new Statement("HALT".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0000\n";

    assertEquals("Incorrect instructions with HALT", expected, instructions);
  }

  /**
   * Test for one bit (default) left shift of A
   */
  @Test
  public void testGetInstructionsForBitshiftLeftOneADefault() {
    Program prog = new Program();
    prog.addStatement(new Statement("BITSHIFT A LEFT".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0211\n";

    assertEquals("Incorrect instructions with BITSHIFT LEFT A", expected,
        instructions);
  }

  /**
   * Test for one bit (default) right shift of B
   */
  @Test
  public void testGetInstructionsForBitshiftRightOneBDefault() {
    Program prog = new Program();
    prog.addStatement(new Statement("BITSHIFT B RIGHT".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0051\n";

    assertEquals("Incorrect instructions with BITSHIFT RIGHT B", expected,
        instructions);
  }

  /**
   * Test for two bit left shift of B
   */
  @Test
  public void testGetInstructionsForBitshiftLeftTwoB() {
    Program prog = new Program();
    prog.addStatement(new Statement("BITSHIFT B LEFT 2".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0261\n";

    assertEquals("Incorrect instructions with BITSHIFT LEFT B 2", expected,
        instructions);
  }

  /**
   * Test for three bit right shift of A
   */
  @Test
  public void testGetInstructionsForBitshiftRightThreeA() {
    Program prog = new Program();
    prog.addStatement(new Statement("BITSHIFT A RIGHT 3".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0031\n";

    assertEquals("Incorrect instructions with BITSHIFT RIGHT A 3", expected,
        instructions);
  }

  /**
   * Test for four bit left shift of B
   */
  @Test
  public void testGetInstructionsForBitshiftLeftFourB() {
    Program prog = new Program();
    prog.addStatement(new Statement("BITSHIFT B LEFT 4".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0241\n";

    assertEquals("Incorrect instructions with BITSHIFT LEFT B 4", expected,
        instructions);
  }

  /**
   * Test long program for jump around display memory location (0200)
   */
  @Test
  public void testLongProgramForJumpAroundDisplayMemoryNoGap() {
    Program prog = new Program();

    // Fill memory locations 4 through 123 with NOOP
    for (int statementCount = 0; statementCount < 120; ++statementCount) {
      prog.addStatement(new Statement("NOOP".split(" ")));
    }

    // Memory locations 124-125
    prog.addStatement(new Statement("LET A = 0100".split(" ")));

    // Memory locations 126-127 (should be moved to 201-202 and replaced with
    // jump to 201)
    prog.addStatement(new Statement("LET B = 010".split(" ")));

    // Memory locations 128-129 (should be moved to 203-204)
    prog.addStatement(new Statement("LET X = 1".split(" ")));

    Statement[] stmts = prog.getStatements();

    for (int stmtCount = 119; stmtCount < stmts.length; ++stmtCount) {
      System.out.println("Statement [" + stmtCount + "] ");
      OpCodes[] opCodes = stmts[stmtCount].getOpCodes();
      for (OpCodes oc : opCodes) {
        System.out.println("    " + oc);
      }
    }

    // Check for the expected updated statements: LET A, JUMP, NOOP, NOOP, NOOP,
    // NOOP, LABEL, LET
    // B, LET X and corresponding locations
    assertEquals("Incorrect number of statements in long program", 129,
        stmts.length);

    // Check jump past display memory address
    assertTrue("Missing jump instruction prior to display address",
        stmts[121].getOpCodes()[0] instanceof JumpInstruction);
    JumpInstruction jumpInst = (JumpInstruction) stmts[121].getOpCodes()[0];
    assertEquals("Jump not correct prior to display address", 0344,
        jumpInst.getType().getOpCode());
    assertEquals("Jump target not correct prior to display address", 0204,
        jumpInst.getDestinationAddress());

    // Check NOOP in display and overflow flag addresses
    assertEquals("NOOP not set in display address", "0300",
        stmts[122].getOpCodes()[0].getFormattedOp());
    assertEquals("NOOP not set in display address", "0300",
        stmts[123].getOpCodes()[0].getFormattedOp());
    assertEquals("NOOP not set in display address", "0300",
        stmts[124].getOpCodes()[0].getFormattedOp());
    assertEquals("NOOP not set in display address", "0300",
        stmts[125].getOpCodes()[0].getFormattedOp());

    // Check moved assignment past display address
    assertEquals("Missing assignment instruction after display address", 2,
        stmts[127].getOpCodes().length);
    assertEquals("Assignment to B not correct after move past display address",
        "0123",
        stmts[127].getOpCodes()[0].getFormattedOp());
    assertEquals(
        "Assignment value to B not cirrect after move past display address",
        "0010",
        stmts[127].getOpCodes()[1].getFormattedOp());

    assertEquals("Missing assignment instruction after display address", 2,
        stmts[128].getOpCodes().length);
    assertEquals("Assignment to X not correct after move past display address",
        "0223",
        stmts[128].getOpCodes()[0].getFormattedOp());
    assertEquals(
        "Assignment value to X not correct after move past display address",
        "0001",
        stmts[128].getOpCodes()[1].getFormattedOp());

  }

  /**
   * Test long program for jump around display memory location (0200). Will
   * require a NOOP in memory locations 0177-0200.
   */
  @Test
  public void testLongProgramForJumpAroundDisplayMemoryWithGap() {
    Program prog = new Program();

    // Fill memory locations 4 through 124 with NOOP
    for (int statementCount = 0; statementCount < 121; ++statementCount) {
      prog.addStatement(new Statement("NOOP".split(" ")));
    }

    // Memory locations 125-126 (should be moved to 201-202 and replaced with
    // jump and noops)
    prog.addStatement(new Statement("LET A = 0100".split(" ")));

    // Memory locations 127-128 (should be moved to 203-204 and replaced with
    // jump to 201)
    prog.addStatement(new Statement("LET B = 010".split(" ")));

    // Memory locations 129-130 (should be moved to 205-206)
    prog.addStatement(new Statement("LET X = 1".split(" ")));

    Statement[] stmts = prog.getStatements();

    // for (int stmtCount = 119; stmtCount < stmts.length; ++stmtCount) {
    // System.out.println("Statement [" + stmtCount + "] ");
    // OpCodes[] opCodes = stmts[stmtCount].getOpCodes();
    // for (OpCodes oc : opCodes) {
    // System.out.println(" " + oc);
    // }
    // }

    // Check for the expected updated statements: JUMP, NOOP, NOOP, NOOP,
    // NOOP, NOOP, LABEL, LET A, LET B, LET X and corresponding locations
    assertEquals("Incorrect number of statements in long program", 131,
        stmts.length);

    // Check jump past display memory address
    assertTrue("Missing jump instruction prior to display address",
        stmts[121].getOpCodes()[0] instanceof JumpInstruction);
    JumpInstruction jumpInst = (JumpInstruction) stmts[121].getOpCodes()[0];
    assertEquals("Jump not correct prior to display address", 0344,
        jumpInst.getType().getOpCode());
    assertEquals("Jump target not correct prior to display address", 0204,
        jumpInst.getDestinationAddress());

    // Check NOOP in 0177, display, and overflow addresses
    assertEquals("NOOP not set in display address", "0300",
        stmts[122].getOpCodes()[0].getFormattedOp());
    assertEquals("NOOP not set in display address", "0300",
        stmts[123].getOpCodes()[0].getFormattedOp());
    assertEquals("NOOP not set in display address", "0300",
        stmts[124].getOpCodes()[0].getFormattedOp());
    assertEquals("NOOP not set in display address", "0300",
        stmts[125].getOpCodes()[0].getFormattedOp());
    assertEquals("NOOP not set in display address", "0300",
        stmts[126].getOpCodes()[0].getFormattedOp());

    // Check moved assignments past display address
    assertEquals("Missing assignment instruction after display address", 2,
        stmts[128].getOpCodes().length);
    assertEquals("Assignment to A not correct after move past display address",
        "0023",
        stmts[128].getOpCodes()[0].getFormattedOp());
    assertEquals(
        "Assignment value to A not correct after move past display address",
        "0100",
        stmts[128].getOpCodes()[1].getFormattedOp());

    assertEquals("Missing assignment instruction after display address", 2,
        stmts[129].getOpCodes().length);
    assertEquals("Assignment to B not correct after move past display address",
        "0123",
        stmts[129].getOpCodes()[0].getFormattedOp());
    assertEquals(
        "Assignment value to B not cirrect after move past display address",
        "0010",
        stmts[129].getOpCodes()[1].getFormattedOp());

    assertEquals("Missing assignment instruction after display address", 2,
        stmts[130].getOpCodes().length);
    assertEquals("Assignment to X not correct after move past display address",
        "0223",
        stmts[130].getOpCodes()[0].getFormattedOp());
    assertEquals(
        "Assignment value to X not correct after move past display address",
        "0001",
        stmts[130].getOpCodes()[1].getFormattedOp());

  }

  /**
   * Test long program for jump around display memory location (0200). Will
   * require a NOOP in memory locations 0177-0200.
   */
  @Test
  public void testLongProgramForJumpAroundDisplayMemoryWithLabelBeforeGap() {
    Program prog = new Program();

    // Fill memory locations 4 through 124 with NOOP
    for (int statementCount = 0; statementCount < 119; ++statementCount) {
      prog.addStatement(new Statement("NOOP".split(" ")));
    }

    prog.addStatement(new Statement("SYSCALL".split(" ")));
    prog.addStatement(new Statement("SYSCALL".split(" ")));

    prog.addStatement(new Statement("LABEL LABEL1".split(" ")));

    prog.addStatement(new Statement("LET A = 0100".split(" ")));

    // Memory locations 125-126 (should be moved to 201-202 and replaced with
    // jump and noops)
    prog.addStatement(new Statement("LET B = 0010".split(" ")));

    Statement[] stmts = prog.getStatements();

    for (int stmtCount = 116; stmtCount < stmts.length; ++stmtCount) {
      System.out.println("Statement [" + stmtCount + "] ");
      OpCodes[] opCodes = stmts[stmtCount].getOpCodes();
      for (OpCodes oc : opCodes) {
        System.out.println(" " + oc);
      }
    }

    // Check for the expected updated statements: SYSCALL, LABEL, JUMP, LABEL,
    // LET A, LET B and corresponding locations
    assertEquals("Incorrect number of statements in long program", 131,
        stmts.length);

    // Check jump past display memory address
    assertTrue("Missing jump instruction prior to display address",
        stmts[122].getOpCodes()[0] instanceof JumpInstruction);
    JumpInstruction jumpInst = (JumpInstruction) stmts[122].getOpCodes()[0];
    assertEquals("Jump not correct prior to display address", 0344,
        jumpInst.getType().getOpCode());
    assertEquals("Jump target not correct prior to display address", 0204,
        jumpInst.getDestinationAddress());

    // Check NOOP in 0177, display, and overflow addresses
    assertEquals("NOOP not set in display address", "0300",
        stmts[123].getOpCodes()[0].getFormattedOp());
    assertEquals("NOOP not set in display address", "0300",
        stmts[124].getOpCodes()[0].getFormattedOp());
    assertEquals("NOOP not set in display address", "0300",
        stmts[125].getOpCodes()[0].getFormattedOp());
    assertEquals("NOOP not set in display address", "0300",
        stmts[126].getOpCodes()[0].getFormattedOp());
    assertEquals("NOOP not set in display address", "0300",
        stmts[127].getOpCodes()[0].getFormattedOp());

    // Check moved assignments past display address
    assertEquals("Missing assignment instruction after display address", 2,
        stmts[129].getOpCodes().length);
    assertEquals("Assignment to A not correct after move past display address",
        "0023",
        stmts[129].getOpCodes()[0].getFormattedOp());
    assertEquals(
        "Assignment value to A not correct after move past display address",
        "0100",
        stmts[129].getOpCodes()[1].getFormattedOp());

  }

  @Test
  public void testGetLexemes() {
    Program prog = new Program();
    prog.addStatement(new Statement("BITSHIFT B LEFT 4".split(" ")));

    Statement stmt = prog.getStatements()[0];

    String[] lexemes = stmt.getLexemes();

    String[] expected = { "BITSHIFT", "B", "LEFT", "4" };

    assertArrayEquals("Incorrect lexemes", expected,
        lexemes);

  }

  @Test
  public void getFormattedStatement() {
    Program prog = new Program();
    prog.addStatement(new Statement("IF B NOTZERO GOTO TopLoop".split(" ")));
    prog.addStatement(new Statement("LABEL TopLoop".split(" ")));

    String statement = prog.getStatements()[0].getFormattedStatement();

    String expected = "IF B NOTZERO GOTO TopLoop";

    assertEquals("Incorrect formatted statement", expected, statement);
  }
}