import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Generates assembly code from the parsed VM command.
 *
 * @authors Yaheli and Erez
 */
public class CodeWriter {

    String fileName;
    BufferedWriter bw;
    int count = 0;

    /**
     * Opens an output file, and gets ready to write into it.
     *
     * @param outputFile
     */
    public CodeWriter(File outputFile) {
        // Set the fileName field for the static segment (in the writePushPop method)
        fileName = outputFile.getName().substring(0, outputFile.getName().length() - 3);
        FileWriter fw;
        try {
        fw = new FileWriter(outputFile);
        bw = new BufferedWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes to the output file the assembly code that
     * implements the given arithmetic-logical command.
     *
     * @param command
     */
    public void writeArithmetic(String command) {
        try {
            bw.write("// " + command);
            bw.newLine();
            bw.write("@SP");
            bw.newLine();
            bw.write("A=M-1");
            bw.newLine();
            
            // not, neg commands
            if (command.equals("not")) {
                bw.write("M=!M");
				bw.newLine();
                return;
            
            } else if (command.equals("neg")) {
                bw.write("M=-M");
				bw.newLine();
                return;
            }

            bw.write("D=M");
            bw.newLine();
            bw.write("A=A-1");
            bw.newLine();
            
            // add, sub, or, and commands
            if (command.length() == 3 || command.equals("or")) {
                switch (command) {
                    case "add": bw.write("M=M+D"); break;
                    case "sub":	bw.write("M=M-D"); break;
                    case "or":	bw.write("M=M|D"); break;
                    case "and":	bw.write("M=M&D"); break;
                }

            } else {
                // eq, gt, lt commands
                bw.write("D=M-D");
                bw.newLine();
                bw.write("@TRUE" + count);
                bw.newLine();
                bw.write("D;J" + command.toUpperCase());
                bw.newLine();
                bw.write("@SP");
                bw.newLine();
                bw.write("A=M-1");
                bw.newLine();
                bw.write("A=A-1");
                bw.newLine();
                bw.write("M=0");
				bw.newLine();
                bw.write("@CONT" + count);
                bw.newLine();
                bw.write("0;JMP");
                bw.newLine();
                bw.write("(TRUE" + count + ")");
                bw.newLine();
                bw.write("@SP");
                bw.newLine();
                bw.write("A=M-1");
                bw.newLine();
                bw.write("A=A-1");
                bw.newLine();
                bw.write("M=-1");
                bw.newLine();
                bw.write("(CONT" + count + ")");
                count++;
            }

            bw.newLine();
            bw.write("@SP");
            bw.newLine();
            bw.write("M=M-1");
            bw.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes to the output file the assembly code that
     * implements the given push or pop command.
     *
     * @param command (C_PUSH or C_POP)
     * @param segment
     * @param index
     */
    public void writePushPop(String command, String segment, int index) {
        try {
            String commandOut = "";
            switch (command) {
                case "C_PUSH": commandOut = "push"; break;
                case "C_POP":  commandOut = "pop"; break;
            }
            bw.write("// " + commandOut + " " + segment + " " + index);
            bw.newLine();

            // constant segment
            if (segment.equals("constant")) {
                bw.write("@" + index);
                bw.newLine();
                bw.write("D=A");
                bw.newLine();
                bw.write("@SP");
                bw.newLine();
                bw.write("A=M");
                bw.newLine();
                bw.write("M=D");
                bw.newLine();
                bw.write("@SP");
                bw.newLine();
                bw.write("M=M+1");
                bw.newLine();

            } else {

                // static segment
                if (segment.equals("static")) {
                    bw.write("@" + fileName + index);
                    bw.newLine();
                    bw.write("D=A");
                    bw.newLine();

                // pointer segment
                } else if (segment.equals("pointer")) {
                    bw.write("@" + index);
                    bw.newLine();
                    bw.write("D=A");
                    bw.newLine();
                    bw.write("@3");
                    bw.newLine();
                    bw.write("D=D+A");
                    bw.newLine();

                // temp segment
                } else if (segment.equals("temp")) {
                    bw.write("@" + index);
                    bw.newLine();
                    bw.write("D=A");
                    bw.newLine();
                    bw.write("@5");
                    bw.newLine();
                    bw.write("D=D+A");
                    bw.newLine();

                // local, argument, this, that segments
                } else {
                    switch (segment) {
                        case "local":    segment = "LCL"; break;
                        case "argument": segment = "ARG"; break;
                        case "this":     segment = "THIS"; break;
                        case "that":     segment = "THAT"; break;
                    }
                    bw.write("@" + index);
                    bw.newLine();
                    bw.write("D=A");
                    bw.newLine();
                    bw.write("@" + segment);
                    bw.newLine();
                    bw.write("D=D+M");
                    bw.newLine();
                }

                // push command
                if (command.equals("C_PUSH")) {
                    bw.write("A=D");
                    bw.newLine();
                    bw.write("D=M");
                    bw.newLine();
                    bw.write("@SP");
                    bw.newLine();
                    bw.write("A=M");
                    bw.newLine();
                    bw.write("M=D");
                    bw.newLine();
                    bw.write("@SP");
                    bw.newLine();
                    bw.write("M=M+1");
                    bw.newLine();

                // pop command
                } else {
                    bw.write("@R13");
                    bw.newLine();
                    bw.write("M=D");
                    bw.newLine();
                    bw.write("@SP");
                    bw.newLine();
                    bw.write("M=M-1");
                    bw.newLine();
                    bw.write("A=M");
                    bw.newLine();
                    bw.write("D=M");
                    bw.newLine();
                    bw.write("@R13");
                    bw.newLine();
                    bw.write("A=M");
                    bw.newLine();
                    bw.write("M=D");
                    bw.newLine();
                }
            }   
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the output file.
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
