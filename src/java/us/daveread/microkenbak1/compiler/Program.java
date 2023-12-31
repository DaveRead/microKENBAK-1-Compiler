package us.daveread.microkenbak1.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import us.daveread.microkenbak1.compiler.instruction.JumpInstruction;
import us.daveread.microkenbak1.compiler.instruction.JumpType;
import us.daveread.microkenbak1.compiler.instruction.Label;
import us.daveread.microkenbak1.compiler.instruction.OpCodes;

/**
 * Contains all the statements making up the program.
 * 
 * @author readda
 *
 */
public class Program {
  /**
   * The logger.
   */
  private final static Logger LOG;

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
   * Set up the logger instance.
   */
  static {
    LOG = Logger.getLogger(Program.class);
  }

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
    if (!memoryLocationsSet) {
      addMemoryLocations();
    }
    return statements.toArray(new Statement[statements.size()]);
  }

  /**
   * Get the underlying operating codes (bytes) to upload to the microKenbek-1.
   * 
   * @param includeEof
   *          Whether an "s" should be added as the last line of the output.
   *          This serves as a signal when uploading to the microKenbek-1 that
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
    Statement stmtJustBeforeDisplayAddress = null;
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

        // Need room (minimum 0176-0177) to add a jump past display address (0200)
        if (memLocation < 0176) {
          stmtJustBeforeDisplayAddress = stmt;
        }
      }
    }

    LOG.info("Total number of memory locations used for program op codes: "
        + memLocation);

    // Deal with program needing to jump past LED display address (0200) of more
    // than 123 op codes. Skip past display and overflow flags (0200-0203)
    if (memLocation >= 0200) {
      LOG.info("Adding jump around LED display address (0200)");
      int lastLocBeforeJump = statements.indexOf(stmtJustBeforeDisplayAddress);
      if (lastLocBeforeJump < 0) {
        throw new IllegalStateException(
            "Program is over 124 operating codes, but cannot find the instruction just before the display address");
      }
      OpCodes[] opCodes = statements.get(lastLocBeforeJump).getOpCodes();
      int lastUsedAddressBeforeDisplay = opCodes[opCodes.length - 1].getMemoryLocation();
      if (opCodes[opCodes.length - 1] instanceof Label) {
        lastUsedAddressBeforeDisplay--;
      }
      memLocation = lastUsedAddressBeforeDisplay;
      int insertLocation = lastLocBeforeJump + 1;
      statements.add(insertLocation, new Statement("GOTO _SKIP_DISPLAY_ADDRESS_".split(" ")));
      lastUsedAddressBeforeDisplay += 2;
      // Fill memory locations in gap through overflow flag for X (0203) with NOOP
      while (lastUsedAddressBeforeDisplay < 0203) {
        ++insertLocation;
        statements.add(insertLocation, new Statement("NOOP".split(" ")));
        ++lastUsedAddressBeforeDisplay;
      }
      ++insertLocation;
      statements.add(insertLocation, new Statement("LABEL _SKIP_DISPLAY_ADDRESS_".split(" ")));
      Label labelPastDisplay = (Label)statements.get(insertLocation).getOpCodes()[0];
      labelCache.put(labelPastDisplay.getName(), labelPastDisplay);
      
      // Renumber memory locations
      memLocation++;
      for (int stmtLoc = lastLocBeforeJump + 1; stmtLoc < statements
          .size(); ++stmtLoc) {
        opCodes = statements.get(stmtLoc).getOpCodes();
        for (OpCodes opCode : opCodes) {
          opCode.setMemoryLocation(memLocation);
          memLocation += opCode.numMemoryCells();
        }
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
    
    memoryLocationsSet = true;

  }  
}
