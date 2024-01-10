package us.daveread.microkenbak1.compiler.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import us.daveread.microkenbak1.compiler.Statement;
import us.daveread.microkenbak1.compiler.instruction.ByteContent;

public class StatementLogicalTest {

  @Test
  public void testAndLiteral() {
    String statement = "AND 0177";
    Statement stmt = new Statement(statement.split(" "));
    ByteContent[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in AND statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for AND statement",
        "0323", inst[0].getFormattedByte());
    assertEquals("Incorrect second formatted operation for AND statement",
        "0177", inst[1].getFormattedByte());
  }

  @Test
  public void testOrLiteral() {
    String statement = "OR 0376";
    Statement stmt = new Statement(statement.split(" "));
    ByteContent[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in OR statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for OR statement", "0303",
        inst[0].getFormattedByte());
    assertEquals("Incorrect second formatted operation for OR statement",
        "0376", inst[1].getFormattedByte());
  }

  @Test(expected = IllegalStateException.class)
  public void testAndMissingLiteral() {
    String statement = "AND";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testOrMissingLiteral() {
    String statement = "OR";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testOrTooManyArgs() {
    String statement = "OR A 0177";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testMemcopyTypoToAddressIn() {
    String statement = "MEMCOPY A TOO ADDRESSIN 0377";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testMemcopyTypoAddressIn() {
    String statement = "MEMCOPY A TO ADDRESSINN 0377";
    new Statement(statement.split(" "));
  }

}
