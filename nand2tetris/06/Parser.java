import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Encapsulates access to the input code.
 * Reads an assembly language command, parses it,
 * and provides convenient access to the command’s
 * components (fields and symbols).
 * In addition, removes all white space and comments.
 *
 * @authors Yaheli and Erez
 */
public class Parser {
    
    public static BufferedReader br;
    String currentCommand;
    public int lineNumber = -1;
    
    /**
     * Opens the input file and gets ready to parse it.
     *
     * @param inputFile
     */
    public Parser(File inputFile) {
        FileReader fr;
        try {
            // Create a FileReader
            fr = new FileReader(inputFile);
            // Create a BufferedReader
            br = new BufferedReader(fr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the next command from the input and makes it the current command.
     * Initially there is no current command.
     *
     * @return true if there are more commands in the input, or else otherwise.
     */
    public boolean advance() {
        try {
            // Read the next line
            String line = br.readLine();
            // Return false if the end of the file reached
            if (line == null) return false;
            // Remove all white space
            line = line.replace(" ", "");
            line = line.replace("\t", "");
            // Read the next line if there is no command in the current line
            if (line.length() == 0 || line.charAt(0) == '/') {
                return advance();
            }
            // Remove all comments
            if (line.indexOf('/') != -1) {
                line = line.substring(0, line.indexOf('/'));
            }
            // Set currentCommand to the current line
            currentCommand = line;
            // Increase by 1 the lineNumber counter if the current line isn't a label
            if (!commandType().equals("L_COMMAND")) {
                lineNumber++;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns the type of the current command:
     * - A_COMMAND for @Xxx where Xxx is either a symbol or a decimal number
     * - C_COMMAND for dest=comp;jump
     * - L_COMMAND (actually, pseudocommand) for (Xxx) where Xxx is a symbol.
     *
     * @return A_COMMAND, C_COMMAND, L_COMMAND
     */
    public String commandType() {
        if (currentCommand.charAt(0) == '@') {
            return "A_COMMAND";
        } else if (currentCommand.charAt(0) == '(') {
            return "L_COMMAND";
        } else {
            return "C_COMMAND";
        }
    }

    /**
     * Returns the symbol or decimal Xxx of the current command @Xxx or (Xxx).
     * Should be called only when commandType() is A_COMMAND or L_COMMAND.
     *
     * @return String
     */
    public String symbol() {
        if (this.commandType().equals("L_COMMAND")) {
            return currentCommand.substring(1, currentCommand.length() - 1);
        }
        return currentCommand.substring(1);
    }

    /**
     * Returns the dest mnemonic in the current C-command (8 possibilities).
     * Should be called only when commandType() is C_COMMAND.
     *
     * @return String
     */
    public String dest() {
        if (currentCommand.indexOf('=') == -1) return null;
        return currentCommand.substring(0, currentCommand.indexOf('='));
    }

    /**
     * Returns the comp mnemonic in the current C-command (28 possibilities).
     * Should be called only when commandType() is C_COMMAND.
     *
     * @return String
     */
    public String comp() {
        if (currentCommand.indexOf(';') == -1) {
            return currentCommand.substring(currentCommand.indexOf('=') + 1);
        }
        return currentCommand.substring(currentCommand.indexOf('=') + 1, currentCommand.indexOf(';'));
    }

    /**
     * Returns the jump mnemonic in the current C-command (8 possibilities).
     * Should be called only when commandType() is C_COMMAND.
     *
     * @return String
     */
    public String jump() {
        if (currentCommand.indexOf(';') == -1) return null;
        return currentCommand.substring(currentCommand.indexOf(';') + 1);
    }

    /**
     * Closes the BufferedReader
     *
     */
    public void close() {
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
