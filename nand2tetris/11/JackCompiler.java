import java.io.File;

/**
 * Top-most ("main") module.
 * Usage: java JackCompiler input
 * Input:
 * • fileName.jack: name of a single source file.
 * • folderName: name of a folder containing one or more .jack source files.
 * Output:
 * • if the input is a single file: fileName.vm.
 * • if the input is a folder: one .vm file for every .jack file, stored in the same folder.
 * For each source .jack file, the compiler creates a JackTokenizer and an output .vm file.
 * Next, the compiler uses the CompilationEngine to write the VM code into the output .vm file.
 *
 * @authors Yaheli and Erez
 */
public class JackCompiler {
    public static void main(String[] args) throws Exception {
        String inputName = args[0];
        File inputFileOrFolder = new File(inputName);
        // If the input is a folder, compile each .jack file in it
        if (inputFileOrFolder.isDirectory()) {
            File[] fileList = inputFileOrFolder.listFiles();
            for (File file : fileList) {
                if (file.getName().endsWith(".jack")) {
                    compileFile(file);
                }
            }
        } else {
            compileFile(inputFileOrFolder);
        }
    }

    /**
     * Runs the CompilationEngine for the inputFile.
     * 
     * @param inputFile
     * @throws Exception
     */
    public static void compileFile(File inputFile) throws Exception {
        String outputFileName = inputFile.getPath().substring(0, inputFile.getPath().lastIndexOf(".jack")) + ".vm";
        File outputFile = new File(outputFileName);
        CompilationEngine ce = new CompilationEngine(inputFile, outputFile);
        ce.compileClass();
        ce.close();
    }
}
