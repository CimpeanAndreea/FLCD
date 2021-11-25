public class Main {
    public static void main(String[] args) {
        Grammar grammar = new Grammar("src/g2.txt");
        grammar.printTerminals();
        grammar.printNonTerminals();
        grammar.printStartingSymbol();
        grammar.printProductions();
        grammar.printProductionsForNonTerminal("program");
        System.out.println(grammar.checkContextFreeGrammar());
    }
}
