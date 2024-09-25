import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Input (Prog.asm): a text file containing a
 * sequence of lines, each being a comment,
 * an A instruction, or a C-instruction.
 * 
 * Output (Prog.hack): a text file containing
 * a sequence of lines, each being a string
 * of sixteen 0 and 1 characters.
 *
 * @authors Yaheli and Erez
 */
public class HackAssembler {
    public static void main(String[] args) {
        String inputFileName = args[0];
        File inputFile = new File(inputFileName);
        // Create the symbolTable
        SymbolTable symbolTable = new SymbolTable();
        // Create the Parser for the firstPass
        Parser firstPass = new Parser(inputFile);
        while (firstPass.advance()) {
            // Add the found labels to the symbol table
            if (firstPass.commandType().equals("L_COMMAND")) {
                symbolTable.addEntry(firstPass.symbol(), firstPass.lineNumber + 1);
            }
        }
        // Close the firstPass Parser
        firstPass.close();
        // Create the outputFileName
        String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf(".asm")) + ".hack";
        FileWriter fw;
        try {
        // Create a FileWriter
        fw = new FileWriter(outputFileName);
        // Create a BufferedWriter
        BufferedWriter bw = new BufferedWriter(fw);
        // Create the Parser for the secondPass
        Parser secondPass = new Parser(inputFile);
        int count = 16, address = 0;
        String binaryCommand = "";
        while (secondPass.advance()) {
            // If the instruction is (label) continue to the next line
            if (secondPass.commandType().equals("L_COMMAND")) continue;
            // If the instruction is @symbol
            if (secondPass.commandType().equals("A_COMMAND")) {
                try {
                    address = Integer.parseInt(secondPass.symbol());
                } catch (NumberFormatException e) {
                    // If symbol is not in the symbol table, adds it
                    if (!symbolTable.contains(secondPass.symbol())) {
                        symbolTable.addEntry(secondPass.symbol(), count++);
                    }
                    address = symbolTable.getAddress(secondPass.symbol());
                }
                // Translate the symbol to its binary value
                binaryCommand = Integer.toBinaryString(address);
                binaryCommand = String.format("%16s", binaryCommand).replaceAll(" ", "0");
            // If the instruction is dest=comp;jump
            } else if (secondPass.commandType().equals("C_COMMAND")) {
                // Translate each of the three fields into its binary value
                binaryCommand = "111" + Code.comp(secondPass.comp()) + Code.dest(secondPass.dest()) + Code.jump(secondPass.jump());
            }
            // Write the binaryCommand to the output file
            bw.write(binaryCommand);
            bw.newLine();
        }
        // Close the BufferedWriter
        bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
