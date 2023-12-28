package us.daveread.microkenbak1.compiler.instruction;

/**
 * Instruction with a single operation code.
 * 
 * @author readda
 *
 */
public class OperationInstruction extends OpCodes {
  /**
   * The operation code for this instruction.
   */
  private int operationCode;

  /**
   * Create the instruction for this operation code. Currently immutable.
   * 
   * @param operationCode
   *          An operation code (byte)
   */
  public OperationInstruction(int operationCode) {
    setOperationCode(operationCode);
  }

  /**
   * Get the operation code.
   * 
   * @return An operation code (byte)
   */
  public int getOperationCode() {
    return operationCode;
  }

  /**
   * Set the operation code.
   * 
   * @param operationCode
   *          An operation code (byte)
   */
  private void setOperationCode(int operationCode) {
    this.operationCode = operationCode;
  }

  @Override
  public String getFormattedOp() {
    return String.format("%04o", getOperationCode());
  }
}
