import java.io.File;

/**
 * The top-most / main module.
 * Input: a single fileName.jack, or a folder containing 0 or more such files.
 * For each file:
 * 1. Creates a JackTokenizer from fileName.jack.
 * 2. Creates an output file named fileName.xml.
 * 3. Creates a CompilationEngine, and calls the compileClass method.
 *
 * @authors Yaheli and Erez
 */
public class JackAnalyzer {
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
        String outputFileName = inputFile.getPath().substring(0, inputFile.getPath().lastIndexOf(".jack")) + ".xml";
        File outputFile = new File(outputFileName);
        CompilationEngine ce = new CompilationEngine(inputFile, outputFile);
        ce.compileClass();
        ce.close();
    }
}
