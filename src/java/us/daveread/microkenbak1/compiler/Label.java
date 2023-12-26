package us.daveread.microkenbak1.compiler;

/**
 * A label for a memory location.
 * 
 * @author readda
 *
 */
public class Label extends OpCodes {
  /**
   * The label name. Labels are case sensitive and immutable. Labels act as
   * place holders in the compiled program, representing a memory location that
   * can be jumped to but not representing an operating code.
   */
  private String name;

  /**
   * Create the label.
   * 
   * @param name
   *          The label name
   */
  public Label(String name) {
    setName(name);
  }

  /**
   * Get the label name.
   * 
   * @return A label name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the label name.
   * 
   * @param name
   *          A label name
   */
  private void setName(String name) {
    this.name = name;
  }

  @Override
  public int numMemoryCells() {
    return 0;
  }

  /**
   * There is no instruction for a label
   */
  @Override
  public String getFormattedOp() {
    return null;
  }
}
