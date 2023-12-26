package us.daveread.microkenbak1.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Statements represent programmatic steps Each statement is converted to one or
 * more OpCodes.
 * 
 * @author readda
 *
 */
public class Statement {
  /**
   * The bytes representing the operations on the microKenbak-1
   */
  private List<OpCodes> programBytes;

  /**
   * create a statement, parsing the supplied array of lexemes (parsed text line
   * from source code).
   * 
   * @param lexemes
   *          The lexemes making up one statement
   */
  public Statement(String[] lexemes) {
    programBytes = new ArrayList<>();

    convertToInstructions(lexemes);
  }

  /**
   * Identify the type of statement and create OpCodes as appropriate.
   * 
   * @param lexemes
   *          The lexemes making up one statement
   */
  public void convertToInstructions(String[] lexemes) {
    switch (lexemes[0].toUpperCase()) {
      case "LABEL":
        handleLabel(lexemes);
        break;
      case "LET":
        handleAssignment(lexemes);
        break;
      case "GOTO":
        handleJump(lexemes);
        break;
      case "SYSCALL":
        handleSysCall();
        break;
      case "MEMCOPY":
        handleMemCopy(lexemes);
        break;
      case "AND":
        handleLogical(lexemes);
        break;
      case "ADD":
        handleAdd(lexemes);
        break;
      case "SUBTRACT":
        handleSubtract(lexemes);
        break;
      case "IF":
        handleIf(lexemes);
        break;
      default:
        throw new IllegalStateException("Undefined keyword: " + lexemes[0]);
    }
  }

  /**
   * Generate OpCodes for a label.
   * 
   * @param lexemes
   *          The lexemes making up one statement
   */
  public void handleLabel(String[] lexemes) {
    if (lexemes.length == 1) {
      throw new IllegalStateException("LABEL requires a label name");
    } else if (lexemes.length > 2) {
      throw new IllegalStateException("Label names may not contain spaces");
    }

    Label label = new Label(lexemes[1]);

    add(label);
  }

  /**
   * Generate OpCodes for an assignment.
   * 
   * @param lexemes
   *          The lexemes making up one statement
   */
  public void handleAssignment(String[] lexemes) {
    if (lexemes.length != 4 || !lexemes[2].equals("=")) {
      throw new IllegalStateException("LET requires variable, =, and value");
    }

    verifyVariableName(lexemes[1]);
    verifyByteValue(lexemes[3]);

    int opCode;

    switch (lexemes[1].toUpperCase()) {
      case "A":
        opCode = 023;
        break;
      case "B":
        opCode = 0123;
        break;
      case "X":
        opCode = 0223;
        break;
      default:
        throw new IllegalStateException(
            "Undefined variable name: " + lexemes[1]);
    }

    OpCodes inst = new OperationInstruction(opCode);
    add(inst);

    inst = new OperationInstruction(Integer.parseInt(lexemes[3], 8));
    add(inst);
  }

  /**
   * Generate OpCodes for an unconditional jump (goto).
   * 
   * @param lexemes
   *          The lexemes making up one statement
   */
  public void handleJump(String[] lexemes) {
    if (lexemes.length == 1) {
      throw new IllegalStateException("GOTO requires a label name");
    } else if (lexemes.length > 2) {
      throw new IllegalStateException("Label names may not contain spaces");
    }

    JumpInstruction jump = new JumpInstruction(JumpType.UNCONDITIONAL,
        lexemes[1]);
    add(jump);
  }

  /**
   * Generate OpCodes for a system call.
   */
  public void handleSysCall() {
    add(new OperationInstruction(0360));
  }

  /**
   * Generate OpCodes for a copying a byte between memory locations.
   * 
   * @param lexemes
   *          The lexemes making up one statement
   */
  public void handleMemCopy(String[] lexemes) {
    if (lexemes.length != 4 || !lexemes[2].equalsIgnoreCase("to")) {
      throw new IllegalStateException(
          "MEMCOPY requires variable, TO, and value");
    }

    lexemes[3] = translateMemLocationName(lexemes[3]);

    verifyVariableName(lexemes[1]);
    verifyByteValue(lexemes[3]);

    int opCode;

    switch (lexemes[1].toUpperCase()) {
      case "A":
        opCode = 034;
        break;
      case "B":
        opCode = 0134;
        break;
      case "X":
        opCode = 0234;
        break;
      default:
        throw new IllegalStateException(
            "Undefined variable name for MEMCOPY: " + lexemes[1]);
    }

    OpCodes inst = new OperationInstruction(opCode);
    add(inst);

    inst = new OperationInstruction(Integer.parseInt(lexemes[3], 8));
    add(inst);
  }

  /**
   * Generate OpCodes for bitwise AND and OR.
   * 
   * @param lexemes
   *          The lexemes making up one statement
   */
  public void handleLogical(String[] lexemes) {
    if (lexemes.length != 2) {
      throw new IllegalStateException(
          lexemes[0].toUpperCase() + " requires a value");
    }

    verifyByteValue(lexemes[1]);

    int opCode;

    switch (lexemes[0].toUpperCase()) {
      case "AND":
        opCode = 0323;
        break;
      case "OR":
        opCode = 0303;
        break;
      default:
        throw new IllegalStateException(
            "Undefined logical operator: " + lexemes[0]);
    }

    add(new OperationInstruction(opCode));
    add(new OperationInstruction(Integer.parseInt(lexemes[1], 8)));
  }

  /**
   * Generate OpCodes for addition.
   * 
   * @param lexemes
   *          The lexemes making up one statement
   */
  public void handleAdd(String[] lexemes) {
    if (lexemes.length != 4 || !lexemes[2].equalsIgnoreCase("TO")) {
      throw new IllegalStateException("ADD requires value, TO, and variable");
    }

    verifyByteValue(lexemes[1]);
    verifyVariableName(lexemes[3]);

    int opCode;

    switch (lexemes[3].toUpperCase()) {
      case "A":
        opCode = 03;
        break;
      case "B":
        opCode = 0103;
        break;
      case "X":
        opCode = 0203;
        break;
      default:
        throw new IllegalStateException(
            "Undefined variable name for ADD: " + lexemes[1]);
    }

    OpCodes inst = new OperationInstruction(opCode);
    add(inst);

    inst = new OperationInstruction(Integer.parseInt(lexemes[1], 8));
    add(inst);

  }

  /**
   * Generate OpCodes for subtraction.
   * 
   * @param lexemes
   *          The lexemes making up one statement
   */
  public void handleSubtract(String[] lexemes) {
    if (lexemes.length != 4 || !lexemes[2].equalsIgnoreCase("FROM")) {
      throw new IllegalStateException(
          "SUBTRACT requires value, FROM, and variable");
    }

    verifyByteValue(lexemes[1]);
    verifyVariableName(lexemes[3]);

    int opCode;

    switch (lexemes[3].toUpperCase()) {
      case "A":
        opCode = 013;
        break;
      case "B":
        opCode = 0113;
        break;
      case "X":
        opCode = 0213;
        break;
      default:
        throw new IllegalStateException(
            "Undefined variable name for SUBTRACT: " + lexemes[1]);
    }

    OpCodes inst = new OperationInstruction(opCode);
    add(inst);

    inst = new OperationInstruction(Integer.parseInt(lexemes[1], 8));
    add(inst);
  }

  /**
   * Generate OpCodes for a decision.
   * 
   * @param lexemes
   *          The lexemes making up one statement
   */
  public void handleIf(String[] lexemes) {
    if (lexemes.length != 5 || !lexemes[3].equalsIgnoreCase("GOTO")) {
      throw new IllegalStateException(
          "IF requires test, variable, GOTO, and label");
    }

    verifyVariableName(lexemes[1]);

    JumpType jumpType;

    switch (lexemes[2].toUpperCase()) {
      case "NOTZERO":
        switch (lexemes[1].toUpperCase()) {
          case "A":
            jumpType = JumpType.A_NON_0;
            break;
          case "B":
            jumpType = JumpType.B_NON_0;
            break;
          case "X":
            jumpType = JumpType.X_NON_0;
            break;
          default:
            throw new IllegalStateException(
                "Undefined variable name for IF: " + lexemes[2]);
        }
        break;
      case "ISZERO":
        switch (lexemes[1].toUpperCase()) {
          case "A":
            jumpType = JumpType.A_0;
            break;
          case "B":
            jumpType = JumpType.B_0;
            break;
          case "X":
            jumpType = JumpType.X_0;
            break;
          default:
            throw new IllegalStateException(
                "Undefined variable name for IF: " + lexemes[2]);
        }
        break;
      default:
        throw new IllegalStateException(
            "Undefined IF test (expected NOTZERO ir ISZERO): " + lexemes[1]);
    }

    JumpInstruction jump = new JumpInstruction(jumpType, lexemes[4]);
    add(jump);
  }

  /**
   * Verify that a string contains a legal octal byte value ("0" through "0377")
   * 
   * @param value
   */
  public void verifyByteValue(String value) {
    int fromOctal;

    try {
      fromOctal = Integer.parseInt(value, 8);
    } catch (NumberFormatException nfe) {
      throw new IllegalStateException(
          "Value must be an octal integer (found: " + value + ")", nfe);
    }

    if (fromOctal < 0 || fromOctal > 255) {
      throw new IllegalStateException(
          "Byte value must be in range 0-377 (octal)");
    }
  }

  /**
   * Check that a variable name is A, B, or X (the Kenbek-1 "registers")
   * 
   * @param name
   *          A variable name
   */
  public void verifyVariableName(String name) {
    if (name.length() != 1) {
      throw new IllegalStateException("Variable name must be A, B, or X");
    }

    String ucName = name.toUpperCase();

    if ("ABX".indexOf(ucName) == -1) {
      throw new IllegalStateException("Variable name must be A, B, or X");
    }
  }

  /**
   * Translates convenience names to memory locations. The value is returned
   * unchanged if it does not match a predefined memory location name.
   * 
   * Supports A, B, X, P (program counter, 04), DISPLAY (0200), and
   * INPUT (0377)
   * 
   * @param memLocation
   *          A memory location of convenience name
   * @return A memory location
   */
  public String translateMemLocationName(String memLocation) {
    switch (memLocation.toUpperCase()) {
      case "A":
        return "0000";
      case "B":
        return "0001";
      case "X":
        return "0002";
      case "P":
        return "0003";
      case "DISPLAY":
        return "0200";
      case "INPUT":
        return "0377";
      default:
        return memLocation;
    }
  }

  /**
   * Add one or more OpCodes to the statement.
   * 
   * @param opCodes
   *          microKenbek-1 operating codes (bytes)
   */
  public void add(OpCodes... opCodes) {
    programBytes.addAll(Arrays.asList(opCodes));
  }

  /**
   * Get the microKenbek-1 operating codes for this statement
   * 
   * @return The operating codes for the statement
   */
  public OpCodes[] getOpCodes() {
    return programBytes.toArray(new OpCodes[programBytes.size()]);
  }
}
