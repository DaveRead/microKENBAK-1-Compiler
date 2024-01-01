package us.daveread.microkenbak1.compiler.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import us.daveread.microkenbak1.compiler.Statement;
import us.daveread.microkenbak1.compiler.instruction.Label;
import us.daveread.microkenbak1.compiler.instruction.OpCodes;

public class StatementLabelTest {

  @Test
  public void testLabel() {
    String statement = "LABEL Top";
    Statement stmt = new Statement(statement.split(" "));
    OpCodes[] inst = stmt.getOpCodes();
    assertEquals("Incorrect number of instructions in LABEL statement", 1,
        inst.length);
    assertEquals("Incorrect name for LABEL statement", "Top",
        ((Label) inst[0]).getName());
    assertNull("Incorrect formatted operation for LABEL statement",
        inst[0].getFormattedOp());
  }

  @Test(expected = IllegalStateException.class)
  public void testLabelWithSpaces() {
    String statement = "LABEL Ok 12IsHere";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testLabelMissingLabelName() {
    String statement = "LABEL";
    new Statement(statement.split(" "));
  }
}
