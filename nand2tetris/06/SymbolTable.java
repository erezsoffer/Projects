import java.util.Hashtable;

/**
 * Keeps a correspondence between symbolic labels and numeric addresses.
 *
 * @authors Yaheli and Erez
 */
public class SymbolTable {
    
    static Hashtable <String, Integer> symbolTable;
    static int count = 16;
    
    /**
     * Creates a new empty symbol table,
     * and adds the predefined symbols to it.
     * 
     */
    public SymbolTable() {
        symbolTable = new Hashtable<>();
        for (int i = 0; i < 16; i++) {
            addEntry("R" + i, i);
        }
        addEntry("SCREEN", 16384);
        addEntry("KBD", 24576);
        addEntry("SP", 0);
        addEntry("LCL", 1);
        addEntry("ARG", 2);
        addEntry("THIS", 3);
        addEntry("THAT", 4);
    }

    /**
     * Adds the pair (symbol, address) to the table.
     *
     * @param symbol (String)
     * @param address (int)
     */
    public void addEntry(String symbol, int address) {
        symbolTable.put(symbol, address);
    }

    /**
     * Does the symbol table contain the given symbol?
     *
     * @param symbol (String)
     * @return true if the table contains the given symbol, or else otherwise.
     */
    public boolean contains(String symbol) {
        return symbolTable.containsKey(symbol);
    }

    /**
     * Returns the address associated with the symbol.
     *
     * @param symbol (String)
     * @return the address associated with the symbol
     */
    public int getAddress(String symbol) {
        return symbolTable.get(symbol);
    }
}
