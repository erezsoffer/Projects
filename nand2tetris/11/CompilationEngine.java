import java.io.File;

/**
 * Gets its input from a JackTokenizer and writes its output using the VMWriter.
 * Organized as a series of compilexxx routines, xxx being a syntactic element in the Jack grammar:
 * • Each compilexxx routine should read xxx from the input, advance() the input exactly
 *   beyond xxx, and emit to the output VM code effecting the semantics of xxx.
 * • compilexxx is called only if xxx is the current syntactic element.
 * • If xxx is part of an expression and thus has a value, the emitted VM code should
 *   compute this value and leave it at the top of the VM’s stack.
 *
 * @authors Yaheli and Erez
 */
public class CompilationEngine {
    
    SymbolTable classSymbolTable;
    SymbolTable subroutineSymbolTable;
    VMWriter vmWriter;
    JackTokenizer jt;
    String currentToken;
    String tokenType;
    String className;
    int ifLabels;
    int whileLabels;
    
    /**
     * Creates a new compilation engine with the given input and output.
     * The next routine called (by the JackAnalyzer module) must be compileClass.
     * 
     * @param inputFile
     * @param outputFile
     */
    public CompilationEngine(File inputFile, File outputFile) {
        classSymbolTable = new SymbolTable();
        subroutineSymbolTable = new SymbolTable();
        vmWriter = new VMWriter(outputFile);
        jt = new JackTokenizer(inputFile);
        if (jt.hasMoreTokens()) {
            jt.advance();
            currentToken = jt.currentToken;
            tokenType = jt.tokenType();
        }
    }

    /** 
     * A helper method that handles the current token, and advances to get the next token.
     * 
     * @param expectedToken
     */
    public String process(String expectedToken) {
        if (!(((expectedToken.equals("identifier") && (tokenType.equals(expectedToken)))) ||
            (!expectedToken.equals("identifier") && (currentToken.equals(expectedToken))))) {
            System.out.print(expectedToken + currentToken + " Syntax Error\n");
        }
        String returnToken = currentToken;
        // advance jt
        if (jt.hasMoreTokens()) {
            jt.advance();
            currentToken = jt.currentToken;
            tokenType = jt.tokenType();
        } else {
            currentToken = "";
            tokenType = "";
        } 
        return returnToken;
    }

    /**
     * Compiles a complete class.
     * 
     */
    public void compileClass() {
        process("class");
        className = process("identifier");
        process("{");
        classSymbolTable = new SymbolTable();
        while (currentToken.matches("^static|field$")) {
            compileClassVarDec();
        }
        while (currentToken.matches("^constructor|function|method$")) {
            compileSubroutine();
        }
        process("}");
    }

    /**
     * Compiles a static variable declaration, or a field declaration.
     * 
     */
    public void compileClassVarDec() {
        String type, kind, name;
        if (currentToken.equals("static")) {
            kind = process("static");
        } else {
            kind = process("field");
        }

        if (currentToken.matches("^int|char|boolean$")) {
            type = process(currentToken);
        } else {
            type = process("identifier"); 
        }

        name = process("identifier"); //varName
        classSymbolTable.define(name, type, kind);

        while (currentToken.equals(",")) {
            process(",");
            name = process("identifier"); //varName
            classSymbolTable.define(name, type, kind);
        }
        process(";");
    }

    /**
     * Compiles a complete method, function, or constructor.
     * 
     */
    public void compileSubroutine() {
        subroutineSymbolTable.reset();
        ifLabels = -1;
        whileLabels = -1;
        String funcName, funcKind = currentToken;
        switch (currentToken) {
            case "constructor":
                process("constructor");
                break;
            case "method":
                process("method");
                subroutineSymbolTable.define("this", className, "argument");
                break;
            default:
                process("function");
                break;
        }

        if (currentToken.matches("^int|char|boolean|void$")) {
            process(currentToken);
        } else {
            process("identifier"); 
        }

        funcName = process("identifier"); 
        process("(");
        compileParameterList();
        process(")");
        process("{");
        while (currentToken.equals("var")) {
            compileVarDec();
        }

        switch (funcKind) {
            case "constructor":
                vmWriter.writeFunction(className + "." + funcName, 0);
                vmWriter.writePush("constant", classSymbolTable.varCount("field"));
                vmWriter.writeCall("Memory.alloc", 1);
                vmWriter.writePop("pointer", 0);
                break;
            case "method":
                vmWriter.writeFunction(className + "." + funcName, subroutineSymbolTable.varCount("local"));
                vmWriter.writePush("argument", 0);
                vmWriter.writePop("pointer", 0);
                break;
            case "function":
                vmWriter.writeFunction(className + "." + funcName, subroutineSymbolTable.varCount("local"));
        }
        compileStatements();
        process("}");
    }

    /**
     * Compiles a (possibly empty) parameter list.
     * Does not handle the enclosing parentheses tokens ( and ).
     * 
     */
    public void compileParameterList() {
        String type, kind = "argument", name;
        boolean moreThan1 = false;
        while (!currentToken.equals(")")) {
            if (moreThan1) {
                process(",");
            } else {
                moreThan1 = true;
            }
            if (currentToken.matches("^int|char|boolean$")) {
                type = process(currentToken);
            } else {
                type = process("identifier"); 
            }
            name = process("identifier"); //varName
            subroutineSymbolTable.define(name, type, kind);
        }
    }

    /**
     * Compiles a var declaration.
     * 
     */
    public void compileVarDec() {
        String type, kind = "local", name;
        process("var");
        if (currentToken.matches("^int|char|boolean$")) {
            type = process(currentToken);
        } else {
            type = process("identifier"); 
        }
        name = process("identifier"); //varName
        subroutineSymbolTable.define(name, type, kind);
        while (currentToken.equals(",")) {
            process(",");
            name = process("identifier"); //varName
            subroutineSymbolTable.define(name, type, kind);
        }
        process(";");
    }

    /**
     * Compiles a sequence of statements.
     * Does not handle the enclosing curly bracket tokens { and }.
     * 
     */
    public void compileStatements() {
        while (currentToken.matches("^let|if|while|do|return$")) {
            switch (currentToken) {
                case "let":
                    compileLet();
                    break;
                case "if":
                    compileIf();
                    break;
                case "while":
                    compileWhile();
                    break;
                case "do":
                    compileDo();
                    break;
                case "return":
                    compileReturn();
                    break;
            }
        }
    }

    /**
     * Compiles a let statement.
     * 
     */
    public void compileLet() {
        String kind, name;
        int index;
        process("let");
        name = process("identifier");
        if (subroutineSymbolTable.kindOf(name).equals("NONE")) {
            kind = classSymbolTable.kindOf(name);
            index = classSymbolTable.indexOf(name);
        } else {
            kind = subroutineSymbolTable.kindOf(name);
            index = subroutineSymbolTable.indexOf(name);
        }
        
        if (currentToken.equals("[")) { //arr
            process("[");
            compileExpression();
            vmWriter.writePush(kind, index);
            process("]");
            vmWriter.writeArithmetic("add");
            process("=");
            compileExpression();
            process(";");
            vmWriter.writePop("temp", 0);
            vmWriter.writePop("pointer", 1);
            vmWriter.writePush("temp", 0);
            vmWriter.writePop("that", 0);
        } else {
            process("=");
            compileExpression();
            process(";");
            vmWriter.writePop(kind, index);
        }
    }

    /**
     * Compiles an if statement, possibly with a trailing else clause.
     * 
     */
    public void compileIf() {
        ifLabels++;
        String L1 = "IF_TRUE" + ifLabels;
        String L2 = "IF_FALSE" + ifLabels;
        String L3 = "IF_END" + ifLabels;
        process("if");
        process("(");
        compileExpression();
        process(")");
        vmWriter.writeIf(L1);
        vmWriter.writeGoto(L2);
        vmWriter.writeLabel(L1);
        process("{");
        compileStatements();
        process("}");
        if (currentToken.equals("else")) {
            vmWriter.writeGoto(L3);
            vmWriter.writeLabel(L2);
            process("else");
            process("{");
            compileStatements();
            process("}");
            vmWriter.writeLabel(L3);
        } else {
            vmWriter.writeLabel(L2);
        }
    }

    /**
     * Compiles a while statement.
     * 
     */
    public void compileWhile() {
        whileLabels++;
        String L1 = "WHILE_EXP" + whileLabels;
        String L2 = "WHILE_END" + whileLabels;
        vmWriter.writeLabel(L1);
        process("while");
        process("(");
        compileExpression();
        process(")");
        vmWriter.writeArithmetic("not");
        vmWriter.writeIf(L2);
        process("{");
        compileStatements();
        process("}");
        vmWriter.writeGoto(L1);
        vmWriter.writeLabel(L2);
    }

    /**
     * Compiles a do statement.
     * 
     */
    public void compileDo() {
        process("do");
        compileExpression();
        process(";");
        vmWriter.writePop("temp", 0);
    }

    /**
     * Compiles a return statement.
     * 
     */
    public void compileReturn() {
        process("return");
        if (!currentToken.equals(";")) {
            compileExpression();
        } else {
            vmWriter.writePush("constant", 0);
        }
        process(";");
        vmWriter.writeReturn();
    }

    /**
     * Compiles an expression.
     * 
     */
    public void compileExpression() {
        String op;
        compileTerm();
        while (currentToken.matches("^[+\\-*/&|<>=]+$")) {
            op = process(currentToken);
            compileTerm();
            switch (op) {
                case "+":
                    vmWriter.writeArithmetic("add");
                    break;
                case "-":
                    vmWriter.writeArithmetic("sub");
                    break;
                case "<":
                    vmWriter.writeArithmetic("lt");
                    break;
                case ">":
                    vmWriter.writeArithmetic("gt");
                    break;
                case "&":
                    vmWriter.writeArithmetic("and");
                    break;
                case "|":
                    vmWriter.writeArithmetic("or");
                    break;
                case "=":
                    vmWriter.writeArithmetic("eq");
                    break;
                case "*":
                    vmWriter.writeCall("Math.multiply", 2);
                    break;
                case "/":
                    vmWriter.writeCall("Math.divide", 2);
                    break;
            }
            
        }
    }

    /**
     * Compiles a term.
     * If the current token is an identifier, the routine must resolve it into a variable, an array entry, or a subroutine call.
     * A single lookahead token, which may be [, (, or ., suffices to distinguish between the possibilities.
     * Any other token is not part of this term and should not be advanced over.
     * 
     */
    public void compileTerm() {
        String op, name, subName, type, kind;
        int index, nArgs = 0;
        if (tokenType.matches("^integerConstant|stringConstant|keyword$")) {
            type = tokenType;
            name = process(currentToken);
            switch (type) {
                case "integerConstant":
                    vmWriter.writePush("constant",Integer.parseInt(name));
                    break;
                case "stringConstant":
                    vmWriter.writePush("constant", name.length());
                    vmWriter.writeCall("String.new", 1);
                    for (int i = 0; i < name.length(); i++) {
                        vmWriter.writePush("constant", (int) name.charAt(i));
                        vmWriter.writeCall("String.appendChar", 2);
                    }
                    break;
                default:
                    if (name.matches("^false|true|null$")) {
                        kind = "constant";
                        index = 0;
                    } else if (name.equals("this")) {
                        kind = "pointer";
                        index = 0;
                    } else if (subroutineSymbolTable.kindOf(name).equals("NONE")) {
                        kind = classSymbolTable.kindOf(name);
                        index = classSymbolTable.indexOf(name);
                    } else {
                        kind = subroutineSymbolTable.kindOf(name);
                        index = subroutineSymbolTable.indexOf(name);
                    }
                    vmWriter.writePush(kind, index);
                    if (name.equals("true")) {
                        vmWriter.writeArithmetic("not");
                    }
                    break;
            }
        } else if (currentToken.matches("^[-~]$")) {
            op = process(currentToken);
            compileTerm();
            switch (op) {
                case "~":
                    vmWriter.writeArithmetic("not");
                    break;
                case "-":
                    vmWriter.writeArithmetic("neg");
                    break;
            }
        } else if (currentToken.equals("(")) {
            process("(");
            compileExpression();
            process(")");
        } else {
            name = process("identifier");
            switch (currentToken) {
                case "[": //arr
                    if (subroutineSymbolTable.kindOf(name).equals("NONE")) {
                        kind = classSymbolTable.kindOf(name);
                        index = classSymbolTable.indexOf(name);
                    } else {
                        kind = subroutineSymbolTable.kindOf(name);
                        index = subroutineSymbolTable.indexOf(name);
                    }
                    process("[");
                    compileExpression();
                    vmWriter.writePush(kind, index);
                    process("]");
                    vmWriter.writeArithmetic("add");
                    vmWriter.writePop("pointer", 1);
                    vmWriter.writePush("that", 0);
                    break;
                case "(": // method
                    process("(");
                    vmWriter.writePush("pointer", 0);
                    nArgs = 1 + compileExpressionList();
                    process(")");
                    vmWriter.writeCall(className + "." + name, nArgs);
                    break;
                case ".":  // func, method
                    process(".");
                    subName = process("identifier");
                    process("(");
                    if (!(subroutineSymbolTable.kindOf(name).equals("NONE") && classSymbolTable.kindOf(name).equals("NONE"))) {
                        if (subroutineSymbolTable.kindOf(name).equals("NONE")) {
                            kind = classSymbolTable.kindOf(name);
                            index = classSymbolTable.indexOf(name);
                            name = classSymbolTable.typeOf(name);
                        } else {
                            kind = subroutineSymbolTable.kindOf(name);
                            index = subroutineSymbolTable.indexOf(name);
                            name = subroutineSymbolTable.typeOf(name);
                        }
                        vmWriter.writePush(kind, index);
                        nArgs = 1;
                    }
                    nArgs += compileExpressionList();
                    process(")");
                    vmWriter.writeCall(name + "." + subName, nArgs);
                    break;
                default:
                    if (subroutineSymbolTable.kindOf(name).equals("NONE")) {
                        kind = classSymbolTable.kindOf(name);
                        index = classSymbolTable.indexOf(name);
                    } else {
                        kind = subroutineSymbolTable.kindOf(name);
                        index = subroutineSymbolTable.indexOf(name);
                    }
                    vmWriter.writePush(kind, index);
                    break;
            }
        }
    }

    /**
     * Compiles a (possibly empty) comma-separated list of expressions.
     * 
     * @return the number of expressions in the list.
     */
    public int compileExpressionList() {
        int i;
        for (i = 0; !currentToken.equals(")"); i++) {
            if (i > 0) {
                process(",");
            }
            compileExpression();
        }
        return i;
    }

    /**
     * Closes the vmWriter.
     * 
     */
    public void close() {
        vmWriter.close();
    }
}