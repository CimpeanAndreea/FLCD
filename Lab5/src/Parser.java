import java.util.*;

public class Parser {
    private Grammar grammar;
    private List<Production> numberedProductions;

    public Parser(String grammarFile) {
        this.grammar = new Grammar(grammarFile);
        this.numberedProductions = new ArrayList<>();
        if (this.grammar.checkContextFreeGrammar()) {
            Set<List<String>> leftHandSides = this.grammar.Productions.keySet();
            for (List<String> lhs : leftHandSides) {
                Set<List<String>> rightHandSides = this.grammar.Productions.get(lhs);
                for(List<String> rhs : rightHandSides)
                this.numberedProductions.add(new Production(lhs.get(0), rhs));
            }
        }
        List<String> r = new ArrayList<>(List.of(this.grammar.StartingSymbol));
        Set<List<String>> rhs = new HashSet<>();
        rhs.add(r);
        this.grammar.Productions.put(new ArrayList<String>(List.of("S_")), rhs);
    }

    /*
    LR(0) parsing:
        1. Define item: [A->alpha.beta]
        2. Construct set of states
            -> what does a state contains: algorithm CLOSURE
            -> how to move from a state to another: function GOTO
            -> construct set of states: CANONICALCOLLECTION
        3. Construct table
        4. Parse sequence based on moves between configurations
     */

    public List<Item> closure(Item item) {
        List<Item> closure = new ArrayList<>();
        boolean modified;
        closure.add(item);
        do {
            modified = false;
            List<Item> productionsWithDotInFrontOfNonTerminal = new ArrayList<>();
            List<String> nonTerminalsWithDotInFront = new ArrayList<>();
            for(Item i : closure) {
                if(this.grammar.NonTerminals.contains(item.productionResult.get(item.dotIndex + 1))) {
                    productionsWithDotInFrontOfNonTerminal.add(i);
                    nonTerminalsWithDotInFront.add(item.productionResult.get(item.dotIndex + 1));
                }
            }
            for(int i = 0; i < productionsWithDotInFrontOfNonTerminal.size(); i++) {
                String nonTerminal = nonTerminalsWithDotInFront.get(i);
                Set<List<String>> productionsOfNonTerminal = this.grammar.getProductionsForNonTerminal(nonTerminal);
                for(List<String> p : productionsOfNonTerminal) {
                    List<String> productionResult = new ArrayList<>();
                    productionResult.add(".");
                    productionResult.addAll(p);
                    Item newItem = new Item(nonTerminal, productionResult, 0);
                    if(!closure.contains(newItem)) {
                        closure.add(newItem);
                        modified = true;
                    }
                }
            }
        }
        while (modified);
        return closure;
    }

    public void canonicalCollection() {

    }

    public void goTo() {

    }
}
