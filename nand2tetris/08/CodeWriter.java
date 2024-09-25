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

    String fileName = "";
    BufferedWriter bw;
    int count = 0;

    /**
     * Opens an output file, and gets ready to write into it.
     * Writes the assembly instructions that effect the bootstrap code that starts the program's execution.
     * 
     * @param outputFile
     */
    public CodeWriter(File outputFile, boolean isSysExist) {
        FileWriter fw;
        try {
        fw = new FileWriter(outputFile);
        bw = new BufferedWriter(fw);
        if (isSysExist) {
            // SP = 256
            bw.write("// SP = 256");
            bw.newLine();
            bw.write("@256");
            bw.newLine();
            bw.write("D=A");
            bw.newLine();
            bw.write("@SP");
            bw.newLine();
            bw.write("M=D");
            bw.newLine();
            // call Sys.init
            this.writeCall("Sys.init", 0);
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs that the translation of a new VM file has started (called by the VMTranslator).
     *
     * @param fileName (String)
     */
    public void setFileName(String fileName) {
        this.fileName = fileName.substring(0, fileName.length() - 3);
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
     * @param command (push or pop)
     * @param segment
     * @param index
     */
    public void writePushPop(String command, String segment, int index) {
        try {
            bw.write("// " + command + " " + segment + " " + index);
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

                // local, argument, this and that segments
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
                if (command.equals("push")) {
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
     * Writes assembly code that effects the label command.
     *
     * @param label (String)
     */
    public void writeLabel(String label) {
        try {
            bw.write("// label " + label);
            bw.newLine();
            bw.write("(" + label + ")");
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes assembly code that effects the goto command.
     *
     * @param label (String)
     */
    public void writeGoto(String label) {
        try {
            bw.write("// goto " + label);
            bw.newLine();
            bw.write("@" + label);
            bw.newLine();
            bw.write("0;JMP");
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes assembly code that effects the if-goto command.
     *
     * @param label (String)
     */
    public void writeIf(String label) {
        try {
            bw.write("// if-goto " + label);
            bw.newLine();
            bw.write("@SP");
            bw.newLine();
            bw.write("M=M-1");
            bw.newLine();
            bw.write("A=M");
            bw.newLine();
            bw.write("D=M");
            bw.newLine();
            bw.write("@" + label);
            bw.newLine();
            bw.write("D;JNE");
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes assembly code that effects the function command.
     *
     * @param functionName (String)
     * @param nVars (int)
     */
    public void writeFunction(String functionName, int nVars) {
        try {
            bw.write("// function " + functionName + " " + nVars);
            bw.newLine();
            // (functionName)
            bw.write("(" + functionName + ")");
            bw.newLine();
            // repeat nVars times: push 0
            for (int i = 0; i < nVars; i++) {
                this.writePushPop("push", "constant", 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes assembly code that effects the call command.
     *
     * @param functionName (String)
     * @param nArgs (int)
     */
    public void writeCall(String functionName, int nArgs) {
        try {
            bw.write("// call " + functionName + " " + nArgs);
            bw.newLine();
            // push retAddrLabel, LCL, ARG, THIS and THAT
            String[] arr = {"retAddrLabel" + count, "LCL", "ARG", "THIS", "THAT"};
            boolean isA = true;
            for (String lab : arr) {
                bw.write("@" + lab);
                bw.newLine();
                if (isA) {
                    bw.write("D=A");
                    isA = false;
                } else {
                    bw.write("D=M");
                }
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
            }
            // ARG = SP – 5 – nArgs
            bw.write("@SP");
            bw.newLine();
            bw.write("D=M");
            bw.newLine();
            bw.write("@5");
            bw.newLine();
            bw.write("D=D-A");
            bw.newLine();
            bw.write("@" + nArgs);
            bw.newLine();
            bw.write("D=D-A");
            bw.newLine();
            bw.write("@ARG");
            bw.newLine();
            bw.write("M=D");
            bw.newLine();
            // LCL = SP
            bw.write("@SP");
            bw.newLine();
            bw.write("D=M");
            bw.newLine();
            bw.write("@LCL");
            bw.newLine();
            bw.write("M=D");
            bw.newLine();
            // goto functionName
            bw.write("@" + functionName);
            bw.newLine();
            bw.write("0;JMP");
            bw.newLine();
            // (retAddrLabel)
            bw.write("(" + "retAddrLabel" + count++ + ")");
            bw.newLine();
                
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes assembly code that effects the return command.
     *
     */
    public void writeReturn() {
        try {
            bw.write("// return");
            bw.newLine();
            // endFrame = LCL
            bw.write("@LCL");
            bw.newLine();
            bw.write("D=M");
            bw.newLine();
            bw.write("@R13"); // endFrame
            bw.newLine();
            bw.write("M=D");
            bw.newLine();
            // retAddr = *(endFrame – 5)
            bw.write("@5");
            bw.newLine();
            bw.write("D=D-A");
            bw.newLine();
            bw.write("A=D");
            bw.newLine();
            bw.write("D=M");
            bw.newLine();
            bw.write("@R14"); // retAddr
            bw.newLine();
            bw.write("M=D");
            bw.newLine();
            // *ARG = pop()
            bw.write("@SP");
            bw.newLine();
            bw.write("M=M-1");
            bw.newLine();
            bw.write("A=M");
            bw.newLine();
            bw.write("D=M");
            bw.newLine();
            bw.write("@ARG");
            bw.newLine();
            bw.write("A=M");
            bw.newLine();
            bw.write("M=D");
            bw.newLine();
            // SP = ARG + 1
            bw.write("@ARG");
            bw.newLine();
            bw.write("D=M+1");
            bw.newLine();
            bw.write("@SP");
            bw.newLine();
            bw.write("M=D");
            bw.newLine();
            // Restore THAT, THIS, ARG, and LCL segments.
            String[] arr = {"THAT", "THIS", "ARG", "LCL"};
            for (String lab : arr) {
                bw.write("@R13"); // endFrame
                bw.newLine();
                bw.write("M=M-1");
                bw.newLine();
                bw.write("A=M");
                bw.newLine();
                bw.write("D=M");
                bw.newLine();
                bw.write("@" + lab);
                bw.newLine();
                bw.write("M=D");
                bw.newLine();
            }
            // goto retAddr
            bw.write("@R14"); // retAddr
            bw.newLine();
            bw.write("A=M");
            bw.newLine();
            bw.write("0;JMP");
            bw.newLine();
            
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
