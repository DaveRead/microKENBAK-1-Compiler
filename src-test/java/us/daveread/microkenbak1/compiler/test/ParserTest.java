package us.daveread.microkenbak1.compiler.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import us.daveread.microkenbak1.compiler.Parser;

/**
 * Unit tests for the Parser class.
 * 
 * @author readda
 *
 */
public class ParserTest {
  /**
   * The parser.
   */
  private Parser parser;

  /**
   * Create the parser object.
   */
  @Before
  public void setup() {
    parser = new Parser();
  }

  /**
   * Test the parser with a multi-line program.
   */
  @Test
  public void testParseIncludingComments() {
    parser.parse("# Display 0252 on LEDs");
    parser.parse("  LET  B =   0252  # Set B to 252");
    parser.parse("# A memcopy follows");
    parser.parse("MEMCOPY  B  TO  DISPLAY  # Update the LEDs");

    String instructions = parser.getProgram().getInstructions(false);

    String expected = "0000\n0000\n0000\n0004\n0123\n0252\n0134\n0200\n";

    assertEquals("Incorrect instructions with syscall", expected, instructions);
  }
}
