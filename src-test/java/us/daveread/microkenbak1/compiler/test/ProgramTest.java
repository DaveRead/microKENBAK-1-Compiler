package us.daveread.microkenbak1.compiler.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import us.daveread.microkenbak1.compiler.Program;
import us.daveread.microkenbak1.compiler.Statement;
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
  @Test(expected=IllegalStateException.class)
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
  @Test(expected=IllegalStateException.class)
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
  @Test(expected=IllegalStateException.class)
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
  @Test(expected=IllegalStateException.class)
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
   * Test output of operating codes for ADD to A.
   */
  @Test
  public void testGetInstructionsForAddToA() {
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
  public void testGetInstructionsForAddToB() {
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
  public void testGetInstructionsForAddToX() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET X = 0x8B".split(" ")));
    prog.addStatement(new Statement("ADD 0x40 TO X".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0223\n0213\n0203\n0100\n";

    assertEquals("Incorrect instructions with ADD", expected, instructions);
  }

  /**
   * Test output of operating codes for SUBTRACT from A.
   */
  @Test
  public void testGetInstructionsForSubtractFromA() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET A = 0210".split(" ")));
    prog.addStatement(new Statement("SUBTRACT 014 FROM A".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0023\n0210\n0013\n0014\n";

    assertEquals("Incorrect instructions with ADD", expected, instructions);
  }

  /**
   * Test output of operating codes for SUBTRACT from B.
   */
  @Test
  public void testGetInstructionsForSubtractFromB() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET B = 0210".split(" ")));
    prog.addStatement(new Statement("SUBTRACT 022 FROM B".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0123\n0210\n0113\n0022\n";

    assertEquals("Incorrect instructions with ADD", expected, instructions);
  }

  /**
   * Test output of operating codes for SUBTRACT from X.
   */
  @Test
  public void testGetInstructionsForSubtractFromX() {
    Program prog = new Program();
    prog.addStatement(new Statement("LET X = 0213".split(" ")));
    prog.addStatement(new Statement("ADD 0100 TO X".split(" ")));

    String instructions = prog.getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0223\n0213\n0203\n0100\n";

    assertEquals("Incorrect instructions with ADD", expected, instructions);
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
}