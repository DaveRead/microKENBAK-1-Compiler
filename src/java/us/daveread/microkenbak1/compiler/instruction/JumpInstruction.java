package us.daveread.microkenbak1.compiler.instruction;

/**
 * A jump instruction. Jump may be based on a value in A, B, or X. Otherwise it
 * is an unconditional jump.
 * 
 * @author readda
 *
 */
public class JumpInstruction extends OpCodes {
  /**
   * The type of jump (e.g., based on value in variable or unconditional).
   */
  private JumpType type;

  /**
   * The name of the label to jump to. This is used to find the destination
   * address.
   */
  private String name;

  /**
   * The destination address for the jump.
   */
  private int destinationAddress;

  /**
   * A jump is defined as a type and destination label name. The compiler
   * process will find the destination address for the label once the program
   * has been compiled.
   * 
   * @param type
   *          The type of Jump (immutable)
   * @param name
   *          A label name (immutable)
   */
  public JumpInstruction(JumpType type, String name) {
    setType(type);
    setName(name);
  }

  /**
   * Get the type of jump.
   * 
   * @return A jump type
   */
  public JumpType getType() {
    return type;
  }

  /**
   * Set the type of jump.
   * 
   * @param type
   *          The jump type
   */
  private void setType(JumpType type) {
    this.type = type;
  }

  /**
   * Get the target label name.
   * 
   * @return A label name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the target label name.
   * 
   * @param name
   *          A label name
   */
  private void setName(String name) {
    this.name = name;
  }

  /**
   * Get the destination byte address.
   * 
   * @return A byte address
   */
  public int getDestinationAddress() {
    return destinationAddress;
  }

  /**
   * Set the destination byte address.
   * 
   * @param destinationAddress
   *          A byte address
   */
  public void setDestinationAddress(int destinationAddress) {
    this.destinationAddress = destinationAddress;
  }

  @Override
  public int numMemoryCells() {
    return 2;
  }

  @Override
  public String getFormattedOp() {
    return getType().getFormattedOp() + "\n"
        + String.format("%04o", getDestinationAddress());
    //    + "        : " + String.format("%04o", getDestinationAddress());
  }
}
