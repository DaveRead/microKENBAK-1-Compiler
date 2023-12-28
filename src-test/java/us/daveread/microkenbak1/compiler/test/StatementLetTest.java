package us.daveread.microkenbak1.compiler.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import us.daveread.microkenbak1.compiler.Statement;
import us.daveread.microkenbak1.compiler.instruction.Label;
import us.daveread.microkenbak1.compiler.instruction.OpCodes;

public class StatementLetTest {

  @Test
  public void testLetA() {
    String statement = "LET A = 0105";
    Statement stmt = new Statement(statement.split(" "));
    OpCodes[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in LET statement", 2, inst.length);
    assertEquals("Incorrect first formatted operation for LET statement", "0023", inst[0].getFormattedOp());
    assertEquals("Incorrect second formatted operation for LET statement", "0105", inst[1].getFormattedOp());
  }

  @Test
  public void testLetB() {
    String statement = "LET b = 0010";
    Statement stmt = new Statement(statement.split(" "));
    OpCodes[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in LET statement", 2, inst.length);
    assertEquals("Incorrect first formatted operation for LET statement", "0123", inst[0].getFormattedOp());
    assertEquals("Incorrect second formatted operation for LET statement", "0010", inst[1].getFormattedOp());
  }
  
  @Test
  public void testLetX() {
    String statement = "LET X = 0377";
    Statement stmt = new Statement(statement.split(" "));
    OpCodes[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in LET statement", 2, inst.length);
    assertEquals("Incorrect first formatted operation for LET statement", "0223", inst[0].getFormattedOp());
    assertEquals("Incorrect second formatted operation for LET statement", "0377", inst[1].getFormattedOp());
  }
  
  @Test(expected = IllegalStateException.class)
  public void testLetWithIncorrectIdentifier() {
    String statement = "LET F = 0237";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testLetWithOutOfRangeHighValue() {
    String statement = "LET B = 0400";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testLetWithOutOfRangeLowValue() {
    String statement = "LET B = -01";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testLetMissingEqualsOperator() {
    String statement = "LET B 0400";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testLetIncorrectOperator() {
    String statement = "LET B < 0400";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testLetMissingIdentifier() {
    String statement = "LET = 0400";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testLetMissingValue() {
    String statement = "LET A =";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testLetIllegalValue() {
    String statement = "LET A = 'hello'";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testLetMultiCharacterIdentifier() {
    String statement = "LET BX = 0031";
    new Statement(statement.split(" "));
  }
}
