import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

/**
 * Handles the compiler’s input.
 * Provides services for:
 * • Ignoring white space.
 * • Getting the current token and advancing the input just beyond it.
 * • Getting the type of the current token.
 *
 * @authors Yaheli and Erez
 */
public class JackTokenizer {

    StreamTokenizer tokenizer;
    String currentToken;
    String tokenType;
    String keyWordsRegex = "^class|constructor|function|method|static|field|var|int|char|boolean|void|true|false|null|this|let|do|if|else|while|return$";

    /**
     * Opens the input .jack file / stream and gets ready to tokenize it.
     *
     * @param inputFile
     */
    public JackTokenizer(File inputFile) {
        try {
            // Create a StreamTokenizer
            tokenizer = new StreamTokenizer(new FileReader(inputFile));
            tokenizer.wordChars('_', '_');
            tokenizer.ordinaryChar('{');
            tokenizer.ordinaryChar('}');
            tokenizer.ordinaryChar('[');
            tokenizer.ordinaryChar(']');
            tokenizer.ordinaryChar('(');
            tokenizer.ordinaryChar(')');
            tokenizer.ordinaryChar('.');
            tokenizer.ordinaryChar(',');
            tokenizer.ordinaryChar(';');
            tokenizer.ordinaryChar('+');
            tokenizer.ordinaryChar('-');
            tokenizer.ordinaryChar('*');
            tokenizer.ordinaryChar('/');
            tokenizer.ordinaryChar('&');
            tokenizer.ordinaryChar('|');
            tokenizer.ordinaryChar('<');
            tokenizer.ordinaryChar('>');
            tokenizer.ordinaryChar('=');
            tokenizer.ordinaryChar('~');
            tokenizer.slashStarComments(true);
            tokenizer.slashSlashComments(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Are there more tokens in the input?
     *
     * @return true if there are more tokens in the input, or false otherwise.
     */
    public boolean hasMoreTokens() {
        return tokenizer.ttype != StreamTokenizer.TT_EOF;
    }

    /**
     * Gets the next token from the input, and makes it the current token.
     * This method should be called only if hasMoreTokens is true.
     * Initially there is no token.
     *
     */
    public void advance() {
        try {
            tokenizer.nextToken();
            switch (tokenizer.ttype) {
                case '"':
                    currentToken = tokenizer.sval;
                    tokenType = "stringConstant";
                    break;

                case StreamTokenizer.TT_WORD:
                    currentToken = tokenizer.sval;
                    if (currentToken.matches(keyWordsRegex)) {
                        tokenType = "keyword";
                    } else if (currentToken.matches("^[0-9]+.*")) {
                        tokenType = "syntaxError";
                    } else {
                        tokenType = "identifier";
                    }
                    break;

                case StreamTokenizer.TT_NUMBER:
                    currentToken = "" + (int)tokenizer.nval;
                    tokenType = "integerConstant";
                    break;

                default:
                    if ("{}()[].,;+-*/&|<>=~".contains("" + (char)tokenizer.ttype)) {
                        currentToken = "" + (char)tokenizer.ttype;
                        tokenType = "symbol";
                    } else {
                        tokenType = "syntaxError";
                    }
                    break;
            }   
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the type of the current token, as a constant.
     *
     * @return KEYWORD, SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST
     */
    public String tokenType() {
        return tokenType;
    }

    /**
     * Returns the keyword which is the current token, as a constant.
     * This method should be called only if tokenType is KEYWORD.
     *
     * @return CLASS, METHOD, FUNCTION, CONSTRUCTOR, INT, BOOLEAN, CHAR, VOID, VAR, STATIC,
     *         FIELD, LET, DO, IF, ELSE, WHILE, RETURN, TRUE, FALSE, NULL, THIS
     */
    public String keyWord() {
        return currentToken;
    }

    /**
     * Returns the character which is the current token.
     * This method should be called only if tokenType is SYMBOL.
     *
     * @return char which is the current token
     */
    public char symbol() {
        return currentToken.charAt(0);
    }

    /**
     * Returns the String which is the current token.
     * This method should be called only if tokenType is IDENTIFIER.
     *
     * @return String which is the current token
     */
    public String identifier() {
        return currentToken;
    }

    /**
     * Returns the integer value of the current token.
     * This method should be called only if tokenType is INT_CONST.
     *
     * @return Integer value of the current token
     */
    public int intVal() {
        return Integer.parseInt(currentToken);
    }

    /**
     * Returns the String value of the current token, without opening and closing double quotes.
     * This method should be called only if tokenType is STRING_CONST.
     *
     * @return String value of the current token
     */
    public String stringVal() {
        return currentToken;
    }
}
