import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Grammar grammar = new Grammar("src/g1.txt");
        /*grammar.printTerminals();
        grammar.printNonTerminals();
        grammar.printStartingSymbol();
        grammar.printProductions();
        grammar.printProductionsForNonTerminal("A");
        System.out.println("\nIs CFG: " + grammar.checkContextFreeGrammar());
         */

        Parser parser = new Parser("src/g1.txt");
        List<State> states = parser.canonicalCollection();
        for (int i = 0; i < states.size(); i++) {
            System.out.println("State " + i);
            System.out.println(states.get(i));
        }
        parser.constructParsingTable();

        //parser.parseSequence(new ArrayList<>(List.of("(","x",",","(","x",")",")")));
        parser.parseSequence(parser.readSequence());
    }
}
