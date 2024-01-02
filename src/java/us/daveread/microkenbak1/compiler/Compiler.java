package us.daveread.microkenbak1.compiler;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;

import org.apache.log4j.Logger;

import us.daveread.microkenbak1.compiler.instruction.OpCodes;

/**
 * compiler to convert the high-level syntax to the microKenbek-1 operating
 * codes.
 * 
 * @author readda
 *
 */
public class Compiler {
  /**
   * The logger.
   */
  private final static Logger LOG;

  /**
   * Set up the logger instance.
   */
  static {
    LOG = Logger.getLogger(Compiler.class);
  }

  /**
   * perform the compilation process, reading the input source code and
   * returning a program object.
   * 
   * @param filename
   *          The source file
   * @return The program obtained by parsing and analyzing the source code
   */
  public Program compile(String filename) {
    LineNumberReader lnr = null;
    String statement = null;
    Parser parser = new Parser();

    LOG.info("Compile " + filename);

    try {
      lnr = new LineNumberReader(new FileReader(filename));
      while ((statement = lnr.readLine()) != null) {
        LOG.debug(
            "Statement read at line " + lnr.getLineNumber() + ": " + statement);
        parser.parse(statement);
      }
      return parser.getProgram();
    } catch (IllegalStateException ise) {
      System.out.println("Syntax Error at line " + lnr.getLineNumber());
      System.out.println("Statement: " + statement);
      System.out.println("Error: " + ise.getMessage());
      System.out.println();
      LOG.error("Syntax error at line " + lnr.getLineNumber() + ": " + statement
          + " [" + ise.getMessage() + "]", ise);
    } catch (FileNotFoundException fnfe) {
      System.out.println("File not found: " + filename);
      LOG.error("File not found: " + filename, fnfe);
    } catch (IOException ioe) {
      System.out.println("Error reading file: " + filename);
      System.out.println("Error: " + ioe.getMessage());
      LOG.error("Error reading file: " + filename, ioe);
    } finally {
      if (lnr != null) {
        try {
          lnr.close();
        } catch (IOException e) {

        }
      }
    }

    return null;
  }

  /**
   * Write the operating codes for a program to a file.
   * 
   * @param filename
   *          The output file for the operating codes
   * @param program
   *          The program whose operating codes are to be written to the output
   *          file
   */
  private void writeProgramFile(String filename, Program program) {
    FileWriter writer = null;

    try {
      writer = new FileWriter(filename);
      writer.write(program.getInstructions(true));
    } catch (IllegalStateException ise) {
      System.out.println("Error formatting instructions: " + ise.getMessage());
      LOG.error("Error formatting instructions", ise);
    } catch (IOException ioe) {
      System.out.println(
          "Error writing file: " + filename + " [" + ioe.getMessage() + "]");
      LOG.error("Error writing file: " + filename, ioe);
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException ioe) {
          LOG.error("Error closing output file: " + filename, ioe);
        }
      }
    }
  }

  /**
   * Write the operating codes as an HTML file.
   * 
   * @param sourceFilename
   *          The source file
   * @param destinationFilename
   *          The output file for the operating codes
   * @param program
   *          The program whose operating codes are to be written to the output
   *          file
   */
  private void writeHtmlFile(String sourceFilename,
      String destinationFilename, Program program) {
    FileWriter writer = null;

    try {
      writer = new FileWriter(destinationFilename);
      writer.write(getHtml(sourceFilename, program));
    } catch (IllegalStateException ise) {
      System.out.println("Error formatting instructions: " + ise.getMessage());
      LOG.error("Error formatting instructions", ise);
    } catch (IOException ioe) {
      System.out.println(
          "Error writing file: " + destinationFilename + " [" + ioe.getMessage()
              + "]");
      LOG.error("Error writing file: " + destinationFilename, ioe);
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException ioe) {
          LOG.error("Error closing output file: " + destinationFilename, ioe);
        }
      }
    }
  }

  public String getHtml(String name, Program program) {
    StringBuffer page = new StringBuffer();

    page.append("<html>\n");
    page.append("<head><title>" + name + "</title></head>\n");

    page.append("<body>\n");
    page.append("<h1>Operation Codes for " + name + "</h1>\n");
    page.append("  <table border=\"1\">\n");
    page.append(
        "    <tr><th>Statement Number</th><th>Statement</th><th>Memory Location, Op Codes, and Values</th></tr>\n");

    int statementCount = 1;
    for (Statement stmt : program.getStatements()) {
      page.append("    <tr>\n");
      page.append("      <td>" + statementCount + "</td>\n");
      page.append("      <td>" + stmt.getFormattedStatement() + "</td>\n");
      page.append("      <td>");
      page.append("        <table border=\"0\">\n");
      OpCodes[] opCodes = stmt.getOpCodes();
      for (OpCodes opCode : opCodes) {
        page.append("        <tr><td>"
            + String.format("%04o", opCode.getMemoryLocation()) + ": ");
        if (opCode.getFormattedOp() == null) {
          page.append(opCode.toString());
        } else {
          page.append(opCode.getFormattedOp());
        }
        page.append("</td></tr>\n");
      }
      page.append("        </table>\n");
      page.append("      </td>\n");
      page.append("    </tr>\n");
      ++statementCount;
    }
    page.append("  </table>\n");
    page.append("</body>\n");

    return page.toString();
  }

  /**
   * Run the compilation process. If an output file name is given, the resulting
   * program operating codes will be written to the file suitable for upload
   * into a microKenbek-1 computer.
   * 
   * @param args
   *          Command line arguments. The first argument is required, the source
   *          code file name. The second argument is optional, the output file
   *          name for the operating codes. If no output filename is provided,
   *          the resulting operating codes will be written to the standard
   *          output.
   */
  public static void main(String[] args) {
    if (args.length < 1 || args.length > 2) {
      System.out.println(
          "An input program file name must be provided. An optional output file name or --AsHTML are supported");
      System.exit(1);
    }

    Compiler compiler = new Compiler();
    Program program = compiler.compile(args[0]);

    if (program != null) {
      if (args.length == 2 && args[1].equalsIgnoreCase("--ASHTML")) {
        compiler.writeHtmlFile(args[0], args[0] + ".html", program);
      } else if (args.length == 2) {
        compiler.writeProgramFile(args[1], program);
      } else {
        System.out.println("Resulting operating codes:");
        System.out.println(program.getInstructions(false));
      }
    }

    System.exit(0);
  }
}
