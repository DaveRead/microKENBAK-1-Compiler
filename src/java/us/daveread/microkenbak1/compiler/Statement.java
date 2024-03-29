package us.daveread.microkenbak1.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import us.daveread.microkenbak1.compiler.instruction.JumpInstruction;
import us.daveread.microkenbak1.compiler.instruction.JumpType;
import us.daveread.microkenbak1.compiler.instruction.Label;
import us.daveread.microkenbak1.compiler.instruction.ByteContent;
import us.daveread.microkenbak1.compiler.instruction.OperationInstruction;

/**
 * Statements represent programmatic steps Each statement is converted to one or
 * more OpCodes.
 * 
 * @author readda
 *
 */
public class Statement {
  /**
   * The logger.
   */
  private final static Logger LOG;

  /**
   * The original lexemes for the statment.
   */
  private String[] originalLexemes;

  /**
   * The bytes representing the operations on the microKenbak-1
   */
  private List<ByteContent> programBytes;

  /**
   * Set up the logger instance.
   */
  static {
    LOG = Logger.getLogger(Statement.class);
  }

  /**
   * create a statement, parsing the supplied array of lexemes (parsed text line
   * from source code).
   * 
   * @param lexemes
   *          The lexemes making up one statement
   */
  public Statement(String[] lexemes) {
    originalLexemes = lexemes;

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
      case "AND": // fall through to OR
      case "OR":
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
      case "HALT":
        handleHalt();
        break;
      case "BITSHIFT":
        handleBitshift(lexemes);
        break;
      case "NOOP":
        handleNoOp();
        break;
      case "BYTES":
        handleRawByteValue(lexemes);
        break;
      default:
        throw new IllegalStateException("Undefined keyword: " + lexemes[0]);
    }
  }

  /**
   * Generate OpCodes for a label.
   * 
   * @param lexemes
   *          The parameters for the label statement
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
   *          The parameters of the assignment operation
   */
  public void handleAssignment(String[] lexemes) {
    if (lexemes.length != 4 && lexemes.length != 5) {
      throw new IllegalStateException(
          "LET requires variable, =, optional VALUEIN, and value");
    }

    if (!lexemes[2].equals("=")) {
      throw new IllegalStateException(
          "LET requires variable, =, optional VALUEIN, and value");
    }

    boolean isValueIn = false;
    int valuePosition = 3;
    String value;

    if (lexemes.length == 5) {
      isValueIn = true;
      valuePosition = 4;
    }

    if (isValueIn && !lexemes[3].toUpperCase().equals("VALUEIN")) {
      throw new IllegalStateException(
          "LET requires variable, =, optional VALUEIN, and value");
    }

    verifyVariableName(lexemes[1]);

    value = lexemes[valuePosition];

    if (isValueIn) {
      value = translateMemLocationName(value);
    }

    verifyByteValue(value);

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

    if (isValueIn) {
      opCode += 1;
    }

    ByteContent inst = new OperationInstruction(opCode);
    add(inst);

    inst = new OperationInstruction(Integer.decode(value));
    add(inst);
  }

  /**
   * Generate OpCodes for an unconditional jump (goto).
   * 
   * @param lexemes
   *          The parameters of the unconditional jump operation
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
   * Generate OpCodes for a copying a variable's value to a memory location.
   * 
   * @param lexemes
   *          The parameters of the memcopy operation
   */
  public void handleMemCopy(String[] lexemes) {
    boolean hasMemoryIndirection;
    int memoryLocationIndex;

    if (lexemes.length < 4 || lexemes.length > 5) {
      throw new IllegalStateException(
          "MEMCOPY requires variable, TO, optional ADDRESSIN, and memory_location");
    } else if (lexemes.length == 4 && !lexemes[2].equalsIgnoreCase("to")) {
      throw new IllegalStateException(
          "MEMCOPY requires variable, TO, optional ADDRESSIN, and memory_location");
    } else if (lexemes.length == 5 && (!lexemes[2].equalsIgnoreCase("to")
        || !lexemes[3].equalsIgnoreCase("addressin"))) {
      throw new IllegalStateException(
          "MEMCOPY requires variable, TO, optional ADDRESSIN, and memory_location");
    } else {
      memoryLocationIndex = lexemes.length - 1;
      hasMemoryIndirection = lexemes.length == 5;
    }

    lexemes[memoryLocationIndex] = translateMemLocationName(
        lexemes[memoryLocationIndex]);

    verifyVariableName(lexemes[1]);
    verifyByteValue(lexemes[memoryLocationIndex]);

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

    if (hasMemoryIndirection) {
      opCode |= 1;
    }

    ByteContent inst = new OperationInstruction(opCode);
    add(inst);

    inst = new OperationInstruction(
        Integer.decode(lexemes[memoryLocationIndex]));
    add(inst);
  }

  /**
   * Generate OpCodes for bitwise AND and OR.
   * 
   * @param lexemes
   *          The parameters of the logic operation
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
    add(new OperationInstruction(Integer.decode(lexemes[1])));
  }

  /**
   * Generate OpCodes for addition.
   * 
   * @param lexemes
   *          The parameters of the addition operation
   */
  public void handleAdd(String[] lexemes) {
    if (lexemes.length != 4 || !lexemes[2].equalsIgnoreCase("TO")) {
      throw new IllegalStateException("ADD requires source, TO, and variable");
    }

    boolean hasMemoryIndirection = false;
    int memoryLocation = 0;

    try {
      verifyVariableName(lexemes[1]);
      hasMemoryIndirection = true;
    } catch (IllegalStateException ise) {
      LOG.debug(
          "Expected exception in ADD, not a variable, now expecting literal");
    }

    if (hasMemoryIndirection) {
      switch (lexemes[1].toUpperCase()) {
        case "A":
          memoryLocation = 0;
          break;
        case "B":
          memoryLocation = 1;
          break;
        case "X":
          memoryLocation = 2;
          break;
        default:
          throw new IllegalStateException(
              "Unknown variable name in ADD: " + lexemes[1]);
      }
    } else {
      verifyByteValue(lexemes[1]);
    }

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

    if (hasMemoryIndirection) {
      opCode += 1;
    }

    ByteContent inst = new OperationInstruction(opCode);
    add(inst);

    if (hasMemoryIndirection) {
      inst = new OperationInstruction(memoryLocation);
    } else {
      inst = new OperationInstruction(Integer.decode(lexemes[1]));
    }
    add(inst);
  }

  /**
   * Generate OpCodes for subtraction.
   * 
   * @param lexemes
   *          The parameters of the subtraction operation
   */
  public void handleSubtract(String[] lexemes) {
    if (lexemes.length != 4 || !lexemes[2].equalsIgnoreCase("FROM")) {
      throw new IllegalStateException(
          "SUBTRACT requires value, FROM, and variable");
    }

    boolean hasMemoryIndirection = false;
    int memoryLocation = 0;

    try {
      verifyVariableName(lexemes[1]);
      hasMemoryIndirection = true;
    } catch (IllegalStateException ise) {
      LOG.debug(
          "Expected exception in SUBTRACT, not a variable, now expecting literal");
    }

    if (hasMemoryIndirection) {
      switch (lexemes[1].toUpperCase()) {
        case "A":
          memoryLocation = 0;
          break;
        case "B":
          memoryLocation = 1;
          break;
        case "X":
          memoryLocation = 2;
          break;
        default:
          throw new IllegalStateException(
              "Unknown variable name in SUBTRACT: " + lexemes[1]);
      }
    } else {
      verifyByteValue(lexemes[1]);
    }

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

    if (hasMemoryIndirection) {
      opCode += 1;
    }

    ByteContent inst = new OperationInstruction(opCode);
    add(inst);

    if (hasMemoryIndirection) {
      inst = new OperationInstruction(memoryLocation);
    } else {
      inst = new OperationInstruction(Integer.decode(lexemes[1]));
    }
    add(inst);
  }

  /**
   * Generate OpCodes for a decision.
   * 
   * @param lexemes
   *          The parameters of the if statement
   */
  public void handleIf(String[] lexemes) {
    if (lexemes.length != 5 || !lexemes[3].equalsIgnoreCase("GOTO")) {
      throw new IllegalStateException(
          "IF requires test, variable, GOTO, and label");
    }

    boolean isOverflowJump = lexemes[2].toUpperCase().equals("OVERFLOW");

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
      case "OVERFLOW":
        switch (lexemes[1].toUpperCase()) {
          case "A":
            jumpType = JumpType.A_OVERFLOW;
            break;
          case "B":
            jumpType = JumpType.B_OVERFLOW;
            break;
          case "X":
            jumpType = JumpType.X_OVERFLOW;
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

    if (isOverflowJump) {
      // Skip the jump if no overflow (e.g. carry flag is 0)
      add(new OperationInstruction(0212));
      add(new OperationInstruction(jumpType.getOpCode()));
      add(new JumpInstruction(JumpType.UNCONDITIONAL, lexemes[4]));
    } else {
      JumpInstruction jump = new JumpInstruction(jumpType, lexemes[4]);
      add(jump);
    }
  }

  /**
   * Generate OpCodes for a halt.
   */
  public void handleHalt() {
    add(new OperationInstruction(0000));
  }

  /**
   * Generate OpCodes for bitshifting.
   * 
   * @param lexemes
   *          The parameters for the bitshift operation
   */
  public void handleBitshift(String[] lexemes) {
    int bitCount = 1;
    String variable;
    String direction;

    if (lexemes.length < 3 || lexemes.length > 4) {
      throw new IllegalStateException(
          "BITSHIFT requires variable, direction, and optional bit_count (1 is the default)");
    }

    variable = lexemes[1].toUpperCase();
    direction = lexemes[2].toUpperCase();

    if (variable.length() != 1 || "AB".indexOf(variable) == -1) {
      throw new IllegalStateException(
          "Unsupported variable value [" + lexemes[1]
              + "] in BITSHIFT - only variables A and B are supported");
    }

    if (!direction.equals("LEFT") && !direction.equals("RIGHT")) {
      throw new IllegalStateException(
          "Unsupported direction [" + lexemes[2]
              + "] in BITSHIFT - must be LEFT or RIGHT");
    }

    if (lexemes.length == 4) {
      try {
        bitCount = Integer.decode(lexemes[3]);
        if (bitCount < 1 || bitCount > 4) {
          throw new IllegalStateException("Unsupported bit count [ " + bitCount
              + "] in BITSHIFT - limited to 1 to 4 bits");
        }
      } catch (NumberFormatException nfe) {
        throw new IllegalStateException(
            "Unsupported bit count [" + lexemes[3]
                + "] in BITSHIFT - must be a number in the range 1 to 4",
            nfe);
      }
    }

    int opCode = 1;

    if (direction.equals("LEFT")) {
      opCode += 0200;
    }

    if (variable.equals("B")) {
      opCode += 040;
    }

    if (bitCount < 4) {
      opCode += bitCount * 010;
    }

    add(new OperationInstruction(opCode));
  }

  /**
   * Generate OpCodes for No Operation
   */
  public void handleNoOp() {
    add(new OperationInstruction(0300));
  }

  /**
   * Place supplied byte value(s) directly in the program. More than one value may be supplied, they will be added to the program in the supplied order.
   * 
   * @param lexemes
   *          The byte value to insert
   */
  public void handleRawByteValue(String[] lexemes) {
    if (lexemes.length < 2) {
      throw new IllegalStateException("BYTE requires at least one numeric value (0-255)");
    }

    for (int i = 1;i < lexemes.length;i++) {
      verifyByteValue(lexemes[i]);

      add(new OperationInstruction(Integer.decode(lexemes[i])));
    }
  }

  /**
   * Verify that a string contains a legal octal byte value ("0" through "0377")
   * 
   * @param value
   */
  public void verifyByteValue(String value) {
    int intValue;

    try {
      intValue = Integer.decode(value);
    } catch (NumberFormatException nfe) {
      throw new IllegalStateException(
          "Value must be a decimal, octal (leading 0), or hexadecimal (leading 0x) integer (found: "
              + value + ")",
          nfe);
    }

    if (intValue < 0 || intValue > 255) {
      throw new IllegalStateException(
          "Byte value must be in range 0-255 (decimal), 0-377 (octal), 0xFF (hexadecimal)");
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
  public void add(ByteContent... opCodes) {
    programBytes.addAll(Arrays.asList(opCodes));
  }

  /**
   * Get the microKenbek-1 operating codes for this statement
   * 
   * @return The operating codes for the statement
   */
  public ByteContent[] getOpCodes() {
    return programBytes.toArray(new ByteContent[programBytes.size()]);
  }

  /**
   * Get the lexemes for the statement.
   * 
   * @return The lexemes used to create the statement
   */
  public String[] getLexemes() {
    return originalLexemes;
  }

  /**
   * Get the statement based on the original lexemes. This may differ from the
   * original source code due to removal of whitespace.
   * 
   * @return The formatted source statement
   */
  public String getFormattedStatement() {
    StringBuffer statement = new StringBuffer();

    for (String lexeme : originalLexemes) {
      statement.append(lexeme);
      statement.append(" ");
    }

    return statement.toString().trim();
  }
}
