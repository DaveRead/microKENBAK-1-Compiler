package us.daveread.microkenbak1.compiler.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import us.daveread.microkenbak1.compiler.Statement;
import us.daveread.microkenbak1.compiler.instruction.ByteContent;

public class StatementMemcopyTest {

  @Test
  public void testMemcopyAToInputAddress() {
    String statement = "MEMCOPY A TO 0377";
    Statement stmt = new Statement(statement.split(" "));
    ByteContent[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in MEMCOPY statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for MEMCOPY statement",
        "0034", inst[0].getFormattedByte());
    assertEquals("Incorrect second formatted operation for MEMCOPY statement",
        "0377", inst[1].getFormattedByte());
  }

  @Test
  public void testMemcopyXToDisplayAddress() {
    String statement = "MEMCOPY X TO DISPLAY";
    Statement stmt = new Statement(statement.split(" "));
    ByteContent[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in MEMCOPY statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for MEMCOPY statement",
        "0234", inst[0].getFormattedByte());
    assertEquals("Incorrect second formatted operation for MEMCOPY statement",
        "0200", inst[1].getFormattedByte());
  }

  @Test
  public void testMemcopyBToAddressInX() {
    String statement = "MEMCOPY B TO ADDRESSIN X";
    Statement stmt = new Statement(statement.split(" "));
    ByteContent[] inst = stmt.getOpCodes();

    assertEquals("Incorrect number of instructions in MEMCOPY statement", 2,
        inst.length);
    assertEquals("Incorrect first formatted operation for MEMCOPY statement",
        "0135", inst[0].getFormattedByte());
    assertEquals("Incorrect second formatted operation for MEMCOPY statement",
        "0002", inst[1].getFormattedByte());
  }

  @Test(expected = IllegalStateException.class)
  public void testMemcopyMissingTo() {
    String statement = "MEMCOPY A 0377";
    new Statement(statement.split(" "));
  }

  @Test(expected = IllegalStateException.class)
  public void testMemcopyTypoTo() {
    String statement = "MEMCOPY A TOO 0377";
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
