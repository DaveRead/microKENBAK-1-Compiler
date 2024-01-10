package us.daveread.microkenbak1.compiler.instruction;

/**
 * A ByteContent represents the content at a memory location, but there may not
 * be any content associated. (e.g., a label is a memory address without an
 * independent instruction)
 * 
 * @author readda
 *
 */
public abstract class ByteContent {
  /**
   * The target memory location for the byte. This is only used
   * to calculate jump addresses for goto and if statement labels.
   */
  private int memoryLocation;

  /**
   * Create a byte
   */
  public ByteContent() {

  }

  /**
   * Get the memory location.
   * 
   * @return A memory location
   */
  public int getMemoryLocation() {
    return memoryLocation;
  }

  /**
   * Set the memory location.
   * 
   * @param memoryLocation
   *          A memory location
   */
  public void setLocation(int memoryLocation) {
    this.memoryLocation = memoryLocation;
  }

  /**
   * Subclasses must define how to format the byte content (operation, value)
   * for loading onto the microKenbek-1 via the USB serial interface.
   * 
   * @return The formatted byte (representing an operation or value)
   */
  public abstract String getFormattedByte();

  /**
   * The number of memory cells the underlying instruction requires. Default is
   * 1. Subclasses may need to override this if an operation requires more than
   * one byte.
   * 
   * @return Number of memory cells required for the associated operation
   */
  public int numMemoryCells() {
    return 1;
  }

  @Override
  public String toString() {
    return String.format("%04o", getMemoryLocation()) + ": "
        + getFormattedByte();
  }
}