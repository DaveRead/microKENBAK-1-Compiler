package us.daveread.microkenbak1.compiler.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import us.daveread.microkenbak1.compiler.Statement;
import us.daveread.microkenbak1.compiler.instruction.ByteContent;

public class StatementLetTest {

  @Test
  public void testLetAssignLiteralToA() {
    String statement = "LET A = 0105";
    Statement stmt = new Statement(statement.split(" "));
    ByteContent[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in LET statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for LET statement",
        "0023", inst[0].getFormattedByte());
    assertEquals("Incorrect second formatted operation for LET statement",
        "0105", inst[1].getFormattedByte());
  }

  @Test
  public void testLetAssignMemoryLocationValueToA() {
    String statement = "LET A = VALUEIN 0105";
    Statement stmt = new Statement(statement.split(" "));
    ByteContent[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in LET statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for LET statement",
        "0024", inst[0].getFormattedByte());
    assertEquals("Incorrect second formatted operation for LET statement",
        "0105", inst[1].getFormattedByte());
  }

  @Test
  public void testLetAssignMnemonicNameValueToA() {
    String statement = "LET A = VALUEIN DISPLAY";
    Statement stmt = new Statement(statement.split(" "));
    ByteContent[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in LET statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for LET statement",
        "0024", inst[0].getFormattedByte());
    assertEquals("Incorrect second formatted operation for LET statement",
        "0200", inst[1].getFormattedByte());
  }

  @Test
  public void testLetAssignLiteralToB() {
    String statement = "LET b = 0010";
    Statement stmt = new Statement(statement.split(" "));
    ByteContent[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in LET statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for LET statement",
        "0123", inst[0].getFormattedByte());
    assertEquals("Incorrect second formatted operation for LET statement",
        "0010", inst[1].getFormattedByte());
  }

  @Test
  public void testLetAssignMemoryLocationValueToB() {
    String statement = "LET b = valuein 0010";
    Statement stmt = new Statement(statement.split(" "));
    ByteContent[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in LET statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for LET statement",
        "0124", inst[0].getFormattedByte());
    assertEquals("Incorrect second formatted operation for LET statement",
        "0010", inst[1].getFormattedByte());
  }

  @Test
  public void testLetAssignMnemonicNameValueToB() {
    String statement = "LET b = valuein input";
    Statement stmt = new Statement(statement.split(" "));
    ByteContent[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in LET statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for LET statement",
        "0124", inst[0].getFormattedByte());
    assertEquals("Incorrect second formatted operation for LET statement",
        "0377", inst[1].getFormattedByte());
  }

  @Test
  public void testLetAssignMnemonicNameProgramCounterToX() {
    String statement = "LET x = valuein p";
    Statement stmt = new Statement(statement.split(" "));
    ByteContent[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in LET statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for LET statement",
        "0224", inst[0].getFormattedByte());
    assertEquals("Incorrect second formatted operation for LET statement",
        "0003", inst[1].getFormattedByte());
  }

  @Test
  public void testLetAssignLiteralToX() {
    String statement = "LET X = 0377";
    Statement stmt = new Statement(statement.split(" "));
    ByteContent[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in LET statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for LET statement",
        "0223", inst[0].getFormattedByte());
    assertEquals("Incorrect second formatted operation for LET statement",
        "0377", inst[1].getFormattedByte());
  }

  @Test
  public void testLetAssignMemoryLocationValueToX() {
    String statement = "LET X = valuein 0377";
    Statement stmt = new Statement(statement.split(" "));
    ByteContent[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in LET statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for LET statement",
        "0224", inst[0].getFormattedByte());
    assertEquals("Incorrect second formatted operation for LET statement",
        "0377", inst[1].getFormattedByte());
  }

  @Test
  public void testLetAssignMnemonicNameValueToX() {
    String statement = "LET X = valuein a";
    Statement stmt = new Statement(statement.split(" "));
    ByteContent[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in LET statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for LET statement",
        "0224", inst[0].getFormattedByte());
    assertEquals("Incorrect second formatted operation for LET statement",
        "0000", inst[1].getFormattedByte());
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

  @Test(expected = IllegalStateException.class)
  public void testLetValueInMissingValueInKeyword() {
    String statement = "LET A = OOPS 0377";
    new Statement(statement.split(" "));
  }
}
