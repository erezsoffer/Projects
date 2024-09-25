import java.io.File;

/**
 * Input:  fileName.vm: name of a single source file, or
 *         folderName: name of a folder cntaining one or more .vm source files
 * Output: fileName.asm or
 *         folderName.asm
 * 
 * Constructs a CodeWriter
 * If the input is a .vm file:
 * - Constructs a Parser to handle the input file
 * - For each VM command in the input file:
 *   uses the Parser to parse the command,
 *   uses the CodeWriter to generate assembly code from it
 * If the input is a folder:
 * - Handles every .vm file in the folder in the manner described above. 
 *
 * @authors Yaheli and Erez
 */
public class VMTranslator {
    public static void main(String[] args) throws Exception {
        String inputName = args[0];
        // Create the outputFileName
        String outputFileName = "";
        if (inputName.endsWith(".vm")) {
            outputFileName = inputName.substring(0, inputName.lastIndexOf(".vm")) + ".asm";
        } else {
            outputFileName = inputName + "/" + inputName.substring(inputName.lastIndexOf('/') + 1, inputName.length()) + ".asm";

        }

        File outputFile = new File(outputFileName);
        File inputFileOrFolder = new File(inputName);
        // Call the CodeWriter with true if there is a Sys.vm file in the folder, otherwise with false
        CodeWriter codeWriter = new CodeWriter(outputFile, new File(inputFileOrFolder, "Sys.vm").exists());
        // If the input is a folder, translate each .vm file in it
        if (inputFileOrFolder.isDirectory()) {
            File[] fileList = inputFileOrFolder.listFiles();
            for (File file : fileList) {
                if (file.getName().endsWith(".vm")) {
                    translateFile(file, codeWriter);
                }
            }
        } else {
            translateFile(inputFileOrFolder, codeWriter);
        }
        codeWriter.close();
    }

    /**
     * Constructs a Parser to handle the input file.
     * For each VM command in the input file:
     * - Uses the Parser to parse the command.
     * - Uses the CodeWriter to generate assembly code from it.
     *
     * @param inputFile
     * @param codeWriter
     */
    public static void translateFile(File inputFile, CodeWriter codeWriter) {
        Parser parser = new Parser(inputFile);
        
        // While there are more lines in the input file, translate the next command.
        while (parser.advance()) {
            codeWriter.setFileName(inputFile.getName());
            switch (parser.commandType()) {
                case "C_ARITHMETIC":
                    codeWriter.writeArithmetic(parser.arg1());
                    break;
                case "push":
                    codeWriter.writePushPop(parser.commandType(), parser.arg1() ,parser.arg2());
                    break;
                case "pop":
                    codeWriter.writePushPop(parser.commandType(), parser.arg1() ,parser.arg2());
                    break;
                case "label":
                    codeWriter.writeLabel(parser.arg1());
                    break;
                case "goto":
                    codeWriter.writeGoto(parser.arg1());
                    break;
                case "if-goto":
                    codeWriter.writeIf(parser.arg1());
                    break;
                case "function":
                    codeWriter.writeFunction(parser.arg1() ,parser.arg2());
                    break;
                case "return":
                    codeWriter.writeReturn();
                    break;
                case "call":
                    codeWriter.writeCall(parser.arg1() ,parser.arg2());
                    break;
            }
        }
        parser.close();
    }
}
