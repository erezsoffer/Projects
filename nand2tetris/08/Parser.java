import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Handles the parsing of a single .vm file
 * Reads a VM command, parses the command into its lexical components, and
 * provides convenient access to these components
 * Skips white space and comments. 
 *
 * @authors Yaheli and Erez
 */
public class Parser {

    public static BufferedReader br;
    String[] currentCommand;
    
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
     * This method should be called only if hasMoreLines is true.
     * Initially there is no current command.
     *
     * @return true if there are more lines in the input file, or flase otherwise.
     */
    public boolean advance() {
        try {
            // Read the next line
            String line = br.readLine();
            // Return false if the end of the file reached
            if (line == null) return false;
            // Read the next line if there is no command in the current line
            if (line.length() == 0 || line.charAt(0) == '/') {
                return advance();
            }
            // Remove all comments
            if (line.indexOf('/') != -1) {
                line = line.substring(0, line.indexOf('/'));
            }
            // Set currentCommand to the current line
            currentCommand = line.split("\\s+");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns a String representing the type of the current command.
     * If the current command is an arithmetic-logical command,
     * returns C_ARITHMETIC.
     *
     * @return C_ARITHMETIC, push, pop, label, goto, if-goto, function, return, call
     */
    public String commandType() {
        if (currentCommand.length == 1 && !currentCommand[0].equals("return")) {
                return "C_ARITHMETIC";
        }
        return currentCommand[0];
    }

    /**
     * Returns the first argument of the current command.
     * In the case of C_ARITHMETIC, the command itself (add, sub, etc.) is returned.
     * Should not be called if the current command is C_RETURN.
     *
     * @return the first argument of the current command (String)
     */
    public String arg1() {
        if (commandType().equals("C_ARITHMETIC")) {
			return currentCommand[0];
        }
        return currentCommand[1];
    }

    /**
     * Returns the second argument of the current command.
     * Should be called only if the current command is
     * C_PUSH, C_POP, C_FUNCTION or C_CALL.
     *
     * @return the second argument of the current command
     */
    public int arg2() {
        return Integer.parseInt(currentCommand[2]);
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
