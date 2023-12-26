package us.daveread.microkenbak1.compiler.test;

import org.junit.Test;

import us.daveread.microkenbak1.compiler.Statement;

public class StatementUndefinedTest {

  @Test(expected = IllegalStateException.class)
  public void testUndefinedKeyword() {
    String statement = "ASSIGN A = 12";
    new Statement(statement.split(" "));
  }
}
