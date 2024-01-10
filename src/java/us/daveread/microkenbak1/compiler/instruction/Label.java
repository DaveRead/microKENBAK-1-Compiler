package us.daveread.microkenbak1.compiler.instruction;

/**
 * A label for a memory location.
 * 
 * @author readda
 *
 */
public class Label extends ByteContent {
  /**
   * The label name. Labels are case sensitive and immutable. Labels act as
   * place holders in the compiled program, representing a memory location that
   * can be jumped to but not representing an operating code.
   */
  private String name;

  /**
   * Track whether the label is ever the target of a GOTO or IF.
   */
  private boolean isUsed;

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

  /**
   * Check whether the label is used as a target for an IF or GOTO.
   * 
   * @return Whether label was used as a jump target
   */
  public boolean isUsed() {
    return isUsed;
  }

  /**
   * Set is the label is used as a target for an IF or GOTO.
   * 
   * It is expected that the compiler will scan the program and call this
   * function when a label is found in an IF or GOTO. The compiler can then
   * check all labels an announce warning for unused labels.
   * 
   * @param isUsed
   *          Whether the label is used as a jump target.
   */
  public void setUsed(boolean isUsed) {
    this.isUsed = isUsed;
  }

  @Override
  public int numMemoryCells() {
    return 0;
  }

  /**
   * There is no instruction for a label
   */
  @Override
  public String getFormattedByte() {
    return null; // "(label " + getName() + ")";
  }

  @Override
  public String toString() {
    return "(label " + getName() + ")";
  }
}
