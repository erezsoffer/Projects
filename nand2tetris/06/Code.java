/**
 * Translates Hack assembly language mnemonics into binary codes.
 *
 * @authors Yaheli and Erez
 */
public class Code {
    /**
     * Returns the binary code of the dest mnemonic.
     *
     * @param mnemonic (String)
     * @return 3 bits (String)
     */
    public static String dest(String mnemonic) {
        if (mnemonic == null) return "000";
        String s = "";
        if (mnemonic.indexOf("A") != -1) s += "1";
        else s += "0";
        if (mnemonic.indexOf("D") != -1) s += "1";
        else s += "0";
        if (mnemonic.indexOf("M") != -1) s += "1";
        else s += "0";
        return s;
    }

    /**
     * Returns the binary code of the comp mnemonic.
     *
     * @param mnemonic (String)
     * @return 7 bits (String)
     */
    public static String comp(String mnemonic) {
        switch (mnemonic) {
            case "0": return "0101010";
            case "1": return "0111111";
            case "-1": return "0111010";
            case "D": return "0001100";
            case "A": return "0110000";
            case "M": return "1110000";
            case "!D": return "0001101";
            case "!A": return "0110001";
            case "!M": return "1110001";
            case "-D": return "0001111";
            case "-A": return "0110011";
            case "-M": return "1110011";
            case "D+1": return "0011111";
            case "1+D": return "0011111";
            case "A+1": return "0110111";
            case "1+A": return "0110111";
            case "M+1": return "1110111";
            case "1+M": return "1110111";
            case "D-1": return "0001110";
            case "A-1": return "0110010";
            case "M-1": return "1110010";
            case "D+A": return "0000010";
            case "A+D": return "0000010";
            case "D+M": return "1000010";
            case "M+D": return "1000010";
            case "D-A": return "0010011";
            case "D-M": return "1010011";
            case "A-D": return "0000111";
            case "M-D": return "1000111";
            case "D&A": return "0000000";
            case "A&D": return "0000000";
            case "D&M": return "1000000";
            case "M&D": return "1000000";
            case "D|A": return "0010101";
            case "A|D": return "0010101";
            case "D|M": return "1010101";
            case "M|D": return "1010101";
        }
        return null;
    }

    /**
     * Returns the binary code of the jump mnemonic.
     *
     * @param mnemonic (String)
     * @return 3 bits (String)
     */
    public static String jump(String mnemonic) {
        if (mnemonic == null) return "000";
        switch (mnemonic) {
            case "JGT": return "001";
            case "JEQ": return "010";
            case "JGE": return "011";
            case "JLT": return "100";
            case "JNE": return "101";
            case "JLE": return "110";
            case "JMP": return "111";
        }
        return "000";
    }
}
