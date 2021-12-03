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
        this.grammar.Productions.put(new ArrayList<>(List.of("S_")), rhs);
        this.grammar.Terminals.add("S_");
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

    public List<Item> closure(List<Item> items) {
        List<Item> closure = new ArrayList<>();
        boolean modified;

        for(Item item : items) {
            closure.add(item);
            do {
                modified = false;
                List<Item> itemsWithDotInFrontOfNonTerminal = new ArrayList<>();
                List<String> nonTerminalsWithDotInFront = new ArrayList<>();

                // get items in the closure that have dot in front of a nonterminal in the right hand side
                // i.e. [A -> alpha . B beta]
                for(Item closureItem : closure) {
                    if(closureItem.dotIndex + 1 < closureItem.rightHandSide.size()) {
                        String afterDot = closureItem.rightHandSide.get(closureItem.dotIndex + 1);

                        if (this.grammar.NonTerminals.contains(afterDot)) {
                            itemsWithDotInFrontOfNonTerminal.add(closureItem);
                            nonTerminalsWithDotInFront.add(afterDot);
                        }
                    }
                }

                // for every item in the closure that has a nonterminal in rhs with dot in front
                // i.e. for every [A -> alpha . B beta] belonging to C
                for(int i = 0; i < itemsWithDotInFrontOfNonTerminal.size(); i++) {
                    String nonTerminal = nonTerminalsWithDotInFront.get(i); // get that nonterminal

                    // get the productions that contain that nonterminal in lhs
                    Set<List<String>> productionsOfNonTerminal = this.grammar.getProductionsForNonTerminal(nonTerminal);

                    // for every B -> omega belonging to P
                    for(List<String> production : productionsOfNonTerminal) {
                        // create new item
                        List<String> rhs = new ArrayList<>();
                        rhs.add(".");
                        rhs.addAll(production);
                        Item newItem = new Item(nonTerminal, rhs, 0);

                        // if B -> . omega does not belong to C, add it to C
                        if(!closure.contains(newItem)) {
                            closure.add(newItem);
                            modified = true;
                        }
                    }
                }
            }
            while (modified);
        }
        return closure;
    }

    public State goTo(State state, String symbol) {
        List<Item> itemsWithDotInFrontOfSymbol = new ArrayList<>();
        for (Item item : state.items) {
            if (item.dotIndex + 1 < item.rightHandSide.size() && Objects.equals(item.rightHandSide.get(item.dotIndex + 1), symbol)) {
                List<String> rhs = new ArrayList<>(item.rightHandSide);
                Item newItem = new Item(item.leftHandSide, rhs, item.dotIndex);
                itemsWithDotInFrontOfSymbol.add(newItem);
            }
        }

        if (itemsWithDotInFrontOfSymbol.isEmpty()) {
            return null;
        }

        for(Item item : itemsWithDotInFrontOfSymbol) {
            Collections.swap(item.rightHandSide, item.dotIndex, item.dotIndex + 1);
            item.dotIndex++;
        }

        return new State(this.closure(itemsWithDotInFrontOfSymbol));
    }

    public List<State> canonicalCollection() {
        List<State> collection = new ArrayList<>();
        List<Item> initialItems = new ArrayList<>();
        List<String> listOfTerminalsAndNonTerminals = new ArrayList<>();
        listOfTerminalsAndNonTerminals.addAll(grammar.NonTerminals);
        listOfTerminalsAndNonTerminals.addAll(grammar.Terminals);

        Item initialItem = new Item("S_", new ArrayList<>(Arrays.asList(".", grammar.StartingSymbol)), 0);
        initialItems.add(initialItem);

        State initialState = new State(closure(initialItems));

        collection.add(initialState);

        boolean modified;
        do {
            modified = false;
            int collectionSize = collection.size();
            for(int i = 0 ; i < collectionSize; i++) {
                State state = collection.get(i);
                for (String symbol : listOfTerminalsAndNonTerminals) {
                    State goToResultState = this.goTo(state, symbol);
                    if (goToResultState != null && !collection.contains(goToResultState)) {
                        collection.add(goToResultState);
                        modified = true;
                    }
                }
            }
        }
        while (modified);
        return collection;
    }
}
