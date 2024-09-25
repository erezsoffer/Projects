import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A simple module that writes individual VM commands to the output .vm file.
 *
 * @authors Yaheli and Erez
 */
public class VMWriter {

    BufferedWriter bw;

    /**
     * Creates a new output .vm file, and prepares it for writing.
     * 
     * @param outputFile
     */
    public VMWriter(File outputFile) {
        try {
            bw = new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a VM push command.
     * 
     * @param segment (CONSTANT, ARGUMENT, LOCAL, STATIC, THIS, THAT, POINTER, TEMP)
     * @param index (int)
     */
    public void writePush(String segment, int index) {
        try {
            bw.write("push " + segment + " " + index);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a VM pop command.
     * 
     * @param segment (ARGUMENT, LOCAL, STATIC, THIS, THAT, POINTER, TEMP)
     * @param index (int)
     */
    public void writePop(String segment, int index) {
        try {
            bw.write("pop " + segment + " " + index);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a VM arithmetic-logical command.
     * 
     * @param command (ADD, SUB, NEG, EQ, GT, LT, AND, OR, NOT)
     */
    public void writeArithmetic(String command) {
        try {
            bw.write(command);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a VM label command.
     * 
     * @param label (String)
     */
    public void writeLabel(String label) {
        try {
            bw.write("label " + label);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a VM goto command.
     * 
     * @param label (String)
     */
    public void writeGoto(String label) {
        try {
            bw.write("goto " + label);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a VM if-goto command.
     * 
     * @param label (String)
     */
    public void writeIf(String label) {
        try {
            bw.write("if-goto " + label);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a VM call command.
     * 
     * @param name (String)
     * @param nArgs (int)
     */
    public void writeCall(String name, int nArgs) {
        try {
            bw.write("call " + name + " " + nArgs);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a VM function command.
     * 
     * @param name (String)
     * @param nVars (int)
     */
    public void writeFunction(String name, int nVars) {
        try {
            bw.write("function " + name + " " + nVars);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a VM return command.
     * 
     */
    public void writeReturn() {
        try {
            bw.write("return");
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the outputFile.
     * 
     */
    public void close() {
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
