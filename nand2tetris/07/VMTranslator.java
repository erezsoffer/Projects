import java.io.File;

/**
 * Input: fileName.vm
 * Output: fileName.asm
 * 
 * Constructs a Parser to handle the input file.
 * Constructs a CodeWriter to handle the output file.
 * Marches through the input file, parsing each line and generating code from it.
 *
 * @authors Yaheli and Erez
 */
public class VMTranslator {
    public static void main(String[] args) throws Exception {
        String inputFileName = args[0];
        File inputFile = new File(inputFileName);
        Parser parser = new Parser(inputFile);
        // Create the outputFileName
        String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf(".vm")) + ".asm";
        File outputFile = new File(outputFileName);
        CodeWriter codeWriter = new CodeWriter(outputFile);
        // While there are more lines in the input file
        while (parser.advance()) {
            if (parser.commandType().equals("C_ARITHMETIC")) {
                codeWriter.writeArithmetic(parser.arg1());
            } else {
                codeWriter.writePushPop(parser.commandType(),parser.arg1() ,parser.arg2());
            }
        }
        parser.close();
        codeWriter.close();
    }
}
