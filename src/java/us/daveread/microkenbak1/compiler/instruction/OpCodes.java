package us.daveread.microkenbak1.compiler.instruction;

/**
 * OpCodes have a memory location but may not have any operation
 * (e.g., labels are a memory address without an independent instruction)
 * 
 * @author readda
 *
 */
public abstract class OpCodes {
  /**
   * The target memory location for the operating code (byte). This is only used
   * to calculate jump addresses for goto and if statement labels.
   */
  private int memoryLocation;

  /**
   * Create an OpCode.
   */
  public OpCodes() {

  }

  /**
   * Get the OpCode's memory location.
   * 
   * @return A memory location
   */
  public int getMemoryLocation() {
    return memoryLocation;
  }

  /**
   * Set the OpCodes' memory location.
   * 
   * @param memoryLocation
   *          A memory location
   */
  public void setMemoryLocation(int memoryLocation) {
    this.memoryLocation = memoryLocation;
  }

  /**
   * Subclasses must define how to format an OpCode for loading onto the
   * microKenbek-1 via the USB serial interface.
   * 
   * @return
   */
  public abstract String getFormattedOp();

  /**
   * The number of memory cells this instruction requires. Default is 1.
   * Subclasses may beed to override this if an OpCode requires more than one
   * byte.
   * 
   * @return Number of memory cells required for this OpCode.
   */
  public int numMemoryCells() {
    return 1;
  }

  @Override
  public String toString() {
    return String.format("%04o", getMemoryLocation()) + ": " + getFormattedOp();
  }
}