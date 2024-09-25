import java.util.HashMap;

/**
 * A symbol table that associates names with information needed
 * for Jack compilation: type, kind, and running index.
 * The symbol table has 2 nested scopes (class/subroutine). 
 *
 * @authors Yaheli and Erez
 */
public class SymbolTable {

    HashMap<String, String[]> symbolTable;
    int fieldOrArgumentCounter = 0;
    int staticOrLocalCounter = 0;

    /**
     * Creates a new symbol table.
     * 
     */
    public SymbolTable() {
        symbolTable = new HashMap<String, String[]>();
    }

    /**
     * Empties the symbol table, and resets the four indexes to 0.
     * Should be called when starting to compile a subroutine declaration.
     *
     */
    public void reset() {
        symbolTable.clear();
        fieldOrArgumentCounter = 0;
        staticOrLocalCounter = 0;
    }

    /**
     * Defines(adds to the table) a new variable of the given name, type and kind.
     * Assigns to it the index value of that kind, and adds 1 to the index.
     *
     * @param name (String)
     * @param type (String)
     * @param kind (STATIC, FIELD, ARG or VAR)
     */
    public void define(String name, String type, String kind) {
        if (kind.equals("field") || kind.equals("argument")) {
            symbolTable.put(name, new String[] {type, kind, "" + fieldOrArgumentCounter++});
        } else {
            symbolTable.put(name, new String[] {type, kind, "" + staticOrLocalCounter++});
        }
        
    }

    /**
     * Returns the number of variables of the given kind already defined in the table.
     *
     * @param kind (STATIC, FIELD, ARG or VAR)
     * @return the number of variables of the given kind already defined in the table
     */
    public int varCount(String kind) {
        if (kind.equals("field") || kind.equals("argument")) {
            return fieldOrArgumentCounter;
        } 
        return staticOrLocalCounter;
    }

    /**
     * Returns the kind of the named identifier.
     * If the identifier is not found, returns NONE.
     *
     * @param name (String)
     * @return STATIC, FIELD, ARG, VAR or NONE
     */
    public String kindOf(String name) {
        if (symbolTable.containsKey(name)) {
            if (symbolTable.get(name)[1].equals("field")) {
                return "this";
            }
            return symbolTable.get(name)[1];
        }
        return "NONE";
    }

    /**
     * Returns the type of the named variable.
     *
     * @param name (String)
     * @return the type of the named variable
     */
    public String typeOf(String name) {
        return symbolTable.get(name)[0];
    }

    /**
     * Returns the index of the named variable.
     *
     * @param name (String)
     * @return the index of the named variable
     */
    public int indexOf(String name) {
        return Integer.parseInt(symbolTable.get(name)[2]);
    }
}
