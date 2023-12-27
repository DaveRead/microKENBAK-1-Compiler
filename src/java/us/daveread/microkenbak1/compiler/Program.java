package us.daveread.microkenbak1.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains all the statements making up the program.
 * 
 * @author readda
 *
 */
public class Program {
  /**
   * The statements (parsed) making up the program.
   */
  private List<Statement> statements;

  /**
   * Whether the memory locations have been calculated from the current
   * collection of statements.
   */
  private boolean memoryLocationsSet;

  /**
   * Sets up the collection for statements.
   */
  public Program() {
    statements = new ArrayList<>();
    memoryLocationsSet = false;
  }

  /**
   * Add a statement to the end of the program.
   * 
   * @param stmt
   *          A statement
   */
  public void addStatement(Statement stmt) {
    statements.add(stmt);
    memoryLocationsSet = false;
  }

  /**
   * Get the ordered collection of statements for the program.
   * 
   * @return The program statements
   */
  public Statement[] getStatements() {
    return statements.toArray(new Statement[statements.size()]);
  }

  /**
   * Get the underlying operating codes (bytes) to upload to the microKenbek-1.
   * 
   * @param includeEof
   *          Whether an "s" should be added as the last line of the output.
   *          This serves as a signla whebn uploading to the microKenbek-1 that
   *          the entire program has been loaded.
   * 
   * @see #addMemoryLocations()
   * 
   * @return A string containing all the instructions, ready for upload to the
   *         microKENBAK-1
   */
  public String getInstructions(boolean includeEof) {
    if (!memoryLocationsSet) {
      addMemoryLocations();
    }
    StringBuffer sb = new StringBuffer();

    // Account for values in memory locations 0 through 3 (A, B, X, and Program
    // Counter)
    sb.append("0000\n");
    sb.append("0000\n");
    sb.append("0000\n");
    sb.append("0004\n");

    for (Statement stmt : statements) {
      OpCodes[] insts = stmt.getOpCodes();
      for (OpCodes inst : insts) {
        String op;
        if ((op = inst.getFormattedOp()) != null) {
          sb.append(op);
          sb.append('\n');
        }
      }
    }

    if (includeEof) {
      sb.append("s\n");
    }
    return sb.toString();
  }

  /**
   * This method accounts for the memory locations of each instruction. This
   * must be called prior to using getInstructions() so that the locations of
   * any jumps can be determined.
   * 
   * @see #getInstructions(boolean)
   */
  private void addMemoryLocations() {
    Map<String, Label> labelCache = new HashMap<>();

    int memLocation = 4;
    for (Statement stmt : statements) {
      OpCodes[] insts = stmt.getOpCodes();
      for (OpCodes inst : insts) {
        inst.setMemoryLocation(memLocation);

        if (inst instanceof Label) {
          Label label = (Label) inst;

          // Detect duplicate label name
          if (labelCache.get(label.getName()) != null) {
            throw new IllegalStateException(
                "A label may only be defined once in a program. The label name "
                    + label.getName() + " is repeated");
          }

          labelCache.put(label.getName(), label);
        }

        memLocation += inst.numMemoryCells();
      }
    }

    // Use cache to update jump instructions
    for (Statement stmt : statements) {
      OpCodes[] insts = stmt.getOpCodes();
      for (OpCodes inst : insts) {
        if (inst instanceof JumpInstruction) {
          JumpInstruction jump = (JumpInstruction) inst;
          String labelName = jump.getName();
          Label target = labelCache.get(labelName);
          if (target == null) {
            throw new IllegalStateException(
                "Label " + labelName + " is not defined");
          }
          jump.setDestinationAddress(target.getMemoryLocation());
          target.setUsed(true);
        }
      }
    }

    // Check for unused labels - generate warning messages
    for (Statement stmt : statements) {
      OpCodes[] insts = stmt.getOpCodes();
      for (OpCodes inst : insts) {
        if (inst instanceof Label) {
          Label label = (Label) inst;
          if (!label.isUsed()) {
            System.out.println(
                "WARNING: Label " + label.getName() + " is never used");
          }
        }
      }
    }
  }
}
