package us.daveread.microkenbak1.compiler.instruction.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import us.daveread.microkenbak1.compiler.instruction.Label;

/**
 * Unit tests for the Label class.
 * 
 * @author readda
 *
 */
public class LabelTest {
  /**
   * A test label.
   */
  private Label testLabel;

  /**
   * Create a test label.
   */
  @Before
  public void setup() {
    testLabel = new Label("test");
  }

  /**
   * Test that the correct label name is returned.
   */
  @Test
  public void testGetName() {
    assertEquals("Incorrect label name", "test", testLabel.getName());
  }

  /**
   * Test that the operating code for a Label is null.
   */
  @Test
  public void testGetFormattedOp() {
    assertNull("Label op is not null", testLabel.getFormattedOp());
  }
}
