package us.daveread.microkenbak1.compiler.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import us.daveread.microkenbak1.compiler.Statement;
import us.daveread.microkenbak1.compiler.instruction.OpCodes;

public class StatementBitshiftTest {

  @Test
  public void testBitshiftALeftOneA() {
    String statement = "BITSHIFT A LEFT 1";
    Statement stmt = new Statement(statement.split(" "));
    OpCodes[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in BITSHIFT statement", 1,
        inst.length);
    assertEquals("Incorrect first formatted operation for BITSHIFT statement",
        "0211", inst[0].getFormattedOp());
  }

  @Test
  public void testBitshiftBRightThree() {
    String statement = "BITSHIFT B RIGHT 3";
    Statement stmt = new Statement(statement.split(" "));
    OpCodes[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in BITSHIFT statement", 1,
        inst.length);
    assertEquals("Incorrect first formatted operation for BITSHIFT statement",
        "0071", inst[0].getFormattedOp());
  }

  @Test(expected = IllegalStateException.class)
  public void testBitshiftMissingTerm() {
    String statement = "BITSHIFT A";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testBitshiftExtraTerm() {
    String statement = "BITSHIFT A LEFT RIGHT 2";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testBitshiftX() {
    String statement = "BITSHIFT X LEFT 2";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testBitshiftARightFive() {
    String statement = "BITSHIFT A RIGHT 5";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testBitshiftBLeftZero() {
    String statement = "BITSHIFT B LEFT 0";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testBitshiftARightTextValue() {
    String statement = "BITSHIFT A RIGHT FIVE";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testBitshiftUnsupportedDirecton() {
    String statement = "BITSHIFT A DOWN 4";
    new Statement(statement.split(" "));
  }
}
