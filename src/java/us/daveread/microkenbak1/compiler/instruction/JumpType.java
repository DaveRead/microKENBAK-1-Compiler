package us.daveread.microkenbak1.compiler.instruction;

/**
 * Jump types supported by the compiler.
 * 
 * @author readda
 *
 */
public enum JumpType {
  /**
   * Jump if A is zero.
   */
  A_0(044),

  /**
   * Jump if A is not zero.
   */
  A_NON_0(043),

  /**
   * Jump if B is zero.
   */
  B_0(0144),

  /**
   * Jump if B is not zero.
   */
  B_NON_0(0143),

  /**
   * Jump if X is zero.
   */
  X_0(0244),

  /**
   * Jump if X is not zero.
   */
  X_NON_0(0243),

  /**
   * Unconditional jump.
   */
  UNCONDITIONAL(0344),

  /**
   * Overflow of A. Value is memory location of overflow/carry flag.
   */
  A_OVERFLOW(0201),

  /**
   * Overflow of B. Value is memory location of overflow/carry flag.
   */
  B_OVERFLOW(0202),

  /**
   * Overflow of X. Value is memory location of overflow/carry flag.
   */
  X_OVERFLOW(0203);

  /**
   * The operation code associated with this jump type.
   */
  private int opCode;

  /**
   * Create the jump type and set its operation code.
   * 
   * @param opCode
   *          An operating code (byte)
   */
  JumpType(int opCode) {
    this.opCode = opCode;
  }

  /**
   * Get the operating code for this jump type.
   * 
   * @return A microKenbnbak-1 operating code (byte)
   */
  public int getOpCode() {
    return opCode;
  }

  /**
   * Get the operating code formatted properly for uploading into the
   * microKenbak-1 computer.
   * 
   * @return The formatted operating code (octal, always 4 digits)
   */
  public String getFormattedOp() {
    return String.format("%04o", getOpCode());
  }
}
