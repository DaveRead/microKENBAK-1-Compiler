package us.daveread.microkenbak1.compiler.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import us.daveread.microkenbak1.compiler.Statement;
import us.daveread.microkenbak1.compiler.instruction.OpCodes;

public class StatementAddTest {

  @Test
  public void testAddALiteral() {
    String statement = "ADD 1 TO A";
    Statement stmt = new Statement(statement.split(" "));
    OpCodes[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in ADD statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for ADD statement",
        "0003", inst[0].getFormattedOp());
    assertEquals("Incorrect second formatted operation for AND statement",
        "0001", inst[1].getFormattedOp());
  }

  @Test
  public void testAddBLiteral() {
    String statement = "ADD 010 TO B";
    Statement stmt = new Statement(statement.split(" "));
    OpCodes[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in ADD statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for ADD statement",
        "0103", inst[0].getFormattedOp());
    assertEquals("Incorrect second formatted operation for AND statement",
        "0010", inst[1].getFormattedOp());
  }

  @Test
  public void testAddXToB() {
    String statement = "ADD X TO B";
    Statement stmt = new Statement(statement.split(" "));
    OpCodes[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in ADD statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for ADD statement",
        "0104", inst[0].getFormattedOp());
    assertEquals("Incorrect second formatted operation for AND statement",
        "0002", inst[1].getFormattedOp());
  }

  @Test(expected = IllegalStateException.class)
  public void testAddMissingLexeme() {
    String statement = "ADD A TO";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testAddTypoTo() {
    String statement = "ADD A TOO B";
    new Statement(statement.split(" "));
  }
}
