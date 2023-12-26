package us.daveread.microkenbak1.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parse a program for the KENBAK-1 and output file for serial upload.
 * 
 * Sample Syntax:
 * 
 * label top
 * let a = 021
 * syscall
 * memcopy b to display
 * syscall
 * memcopy b to a
 * and 17
 * memcopy a to b
 * let a = 0220
 * syscall
 * let a = 0222
 * let b = 0200
 * syscall
 * syscall
 * goto top
 * 
 * @author readda
 *
 */
public class Parser {
  /**
   * The program created by the parser.
   */
  private Program program;

  /**
   * Initialize an empty program.
   */
  public Parser() {
    program = new Program();
  }

  /**
   * Parse a statement from the input and add it to the end of the program
   * 
   * @param statement
   *          A single statement (source code line)
   */
  public void parse(String statement) {
    statement = statement.trim();
    String[] splitStatement = statement.split(" ");

    // Remove any empty lexemes
    List<String> packedStatement = new ArrayList<>();

    // Remove blank lexemes
    for (String s : splitStatement) {
      // Comments start with # (can be anywhere on the statement line
      if (s.trim().startsWith("#")) {
        break;
      }

      if (s.trim().length() > 0) {
        packedStatement.add(s);
      }
    }

    if (packedStatement.size() > 0) {
      Statement stmt = makeStatement(
          packedStatement.toArray(new String[packedStatement.size()]));
      program.addStatement(stmt);
    }
  }

  /**
   * Create a statement from the lexemes found in the input line.
   * 
   * @param lexemes
   *          The lexemes taken from the input statement
   * @return The statement
   */
  public Statement makeStatement(String[] lexemes) {
    Statement stmt = new Statement(lexemes);
    return stmt;
  }

  /**
   * Get the program created from the parsed input.
   * 
   * @return The program
   */
  public Program getProgram() {
    return program;
  }
}
