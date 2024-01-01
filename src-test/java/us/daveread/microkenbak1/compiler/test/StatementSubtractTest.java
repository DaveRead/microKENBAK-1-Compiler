package us.daveread.microkenbak1.compiler.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import us.daveread.microkenbak1.compiler.Statement;
import us.daveread.microkenbak1.compiler.instruction.OpCodes;

public class StatementSubtractTest {

  @Test
  public void testSubtractALiteral() {
    String statement = "SUBTRACT 1 FROM A";
    Statement stmt = new Statement(statement.split(" "));
    OpCodes[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in SUBTRACT statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for SUBTRACT statement",
        "0013", inst[0].getFormattedOp());
    assertEquals("Incorrect second formatted operation for SUBTRACT statement",
        "0001", inst[1].getFormattedOp());
  }

  @Test
  public void testSubtractBLiteral() {
    String statement = "SUBTRACT 010 FROM B";
    Statement stmt = new Statement(statement.split(" "));
    OpCodes[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in SUBTRACT statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for SUBTRACT statement",
        "0113", inst[0].getFormattedOp());
    assertEquals("Incorrect second formatted operation for SUBTRACT statement",
        "0010", inst[1].getFormattedOp());
  }

  @Test
  public void testSubtractXFromB() {
    String statement = "SUBTRACT X FROM B";
    Statement stmt = new Statement(statement.split(" "));
    OpCodes[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in SUBTRACT statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for SUBTRACT statement",
        "0114", inst[0].getFormattedOp());
    assertEquals("Incorrect second formatted operation for SUBTRACT statement",
        "0002", inst[1].getFormattedOp());
  }

  @Test(expected = IllegalStateException.class)
  public void testSubtractMissingLexeme() {
    String statement = "SUBTRACT A FROM";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testSubtractTypoFrom() {
    String statement = "ADD A FRUM B";
    new Statement(statement.split(" "));
  }
}
