public class Main {
    public static void main(String[] args) {
        SymbolTable symbolTable = new SymbolTable(10);

        System.out.println(symbolTable);
        symbolTable.insertSymbol("1");
        symbolTable.insertSymbol("var");
        System.out.println(symbolTable);

    }
}
