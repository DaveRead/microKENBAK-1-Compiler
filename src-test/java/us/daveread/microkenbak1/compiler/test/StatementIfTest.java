package us.daveread.microkenbak1.compiler.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import us.daveread.microkenbak1.compiler.Statement;
import us.daveread.microkenbak1.compiler.instruction.JumpInstruction;
import us.daveread.microkenbak1.compiler.instruction.ByteContent;

public class StatementIfTest {

  @Test
  public void testIfIsZeroA() {
    String statement = "IF A ISZERO GOTO TOP";
    Statement stmt = new Statement(statement.split(" "));
    ByteContent[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in IF statement", 1,
        inst.length);
    assertEquals("Incorrect first formatted operation for IF statement",
        "0044\n0000", inst[0].getFormattedByte());
    JumpInstruction jump = (JumpInstruction) inst[0];
    assertEquals("Incorrect target label for IF statement", "TOP",
        jump.getName());
  }

  @Test
  public void testIfNotZeroX() {
    String statement = "IF X NOTZERO GOTO NEXTLOOP";
    Statement stmt = new Statement(statement.split(" "));
    ByteContent[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in IF statement", 1,
        inst.length);
    assertEquals("Incorrect first formatted operation for IF statement",
        "0243\n0000", inst[0].getFormattedByte());
    JumpInstruction jump = (JumpInstruction) inst[0];
    assertEquals("Incorrect target label for IF statement", "NEXTLOOP",
        jump.getName());
  }

  @Test
  public void testIfOverflowB() {
    String statement = "IF B OVERFLOW GOTO ENDLOOP";
    Statement stmt = new Statement(statement.split(" "));
    ByteContent[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in IF statement", 3,
        inst.length);
    assertEquals("Incorrect first formatted operation for IF statement", "0212",
        inst[0].getFormattedByte());
    assertEquals("Incorrect second formatted operation for IF statement",
        "0202", inst[1].getFormattedByte());
    JumpInstruction jump = (JumpInstruction) inst[2];
    assertEquals("Incorrect third formatted operation for IF statement",
        "0344\n0000", jump.getFormattedByte());
    assertEquals("Incorrect target label for IF statement", "ENDLOOP",
        jump.getName());
  }

  @Test
  public void testIfOverflowX() {
    String statement = "IF X OVERFLOW GOTO NEXTLOOP";
    Statement stmt = new Statement(statement.split(" "));
    ByteContent[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in IF statement", 3,
        inst.length);
    assertEquals("Incorrect first formatted operation for IF statement", "0212",
        inst[0].getFormattedByte());
    assertEquals("Incorrect second formatted operation for IF statement",
        "0203", inst[1].getFormattedByte());
    JumpInstruction jump = (JumpInstruction) inst[2];
    assertEquals("Incorrect third formatted operation for IF statement",
        "0344\n0000", jump.getFormattedByte());
    assertEquals("Incorrect target label for IF statement", "NEXTLOOP",
        jump.getName());
  }

  @Test(expected = IllegalStateException.class)
  public void testIfTypoGoto() {
    String statement = "IF B ISZERO GOYO BEGIN";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testUnsupportedTest() {
    String statement = "IF A UNDERFLOW GOTO END";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testMissingTerm() {
    String statement = "IF A ISZERO GOTO";
    new Statement(statement.split(" "));
  }
}
