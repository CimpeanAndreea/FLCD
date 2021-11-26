import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Grammar grammar = new Grammar("src/g2.txt");
        grammar.printTerminals();
        grammar.printNonTerminals();
        grammar.printStartingSymbol();
        grammar.printProductions();
        grammar.printProductionsForNonTerminal("program");
        System.out.println("\nIs CFG: " + grammar.checkContextFreeGrammar());
        Parser parser = new Parser("src/g1.txt");
        List<Item> items = parser.closure(new Item("S_", new ArrayList<>(Arrays.asList(".", "S")), 0));
        System.out.println(items);
    }
}
