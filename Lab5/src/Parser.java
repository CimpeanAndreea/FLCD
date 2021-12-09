import java.util.*;

public class Parser {
    private Grammar grammar;
    private List<Production> numberedProductions;
    private List<String> actionsParsingTable = new ArrayList<>();
    private HashMap<Pair<Integer, String>, Integer> gotoParsingTable = new HashMap<>();
    private List<State> states;

    public Parser(String grammarFile) {
        this.grammar = new Grammar(grammarFile);
        this.numberedProductions = new ArrayList<>();

        this.numberedProductions.add(new Production("S_", new ArrayList<>(List.of(this.grammar.StartingSymbol))));

        if (this.grammar.checkContextFreeGrammar()) {
            Set<List<String>> leftHandSides = this.grammar.Productions.keySet();
            for (List<String> lhs : leftHandSides) {
                Set<List<String>> rightHandSides = this.grammar.Productions.get(lhs);
                for(List<String> rhs_1 : rightHandSides)
                this.numberedProductions.add(new Production(lhs.get(0), rhs_1));
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

    // Construct LR(0) table
    // -> one line for each state
    // -> 2 parts
    //      - action: one column
    //      - goto: one column for each symbol
    public void constructParsingTable() {
        System.out.println("CONSTRUCTING PARSER TABLE");
        System.out.println("NUMBERED PRODUCTIONS");
        for (int i = 1; i < numberedProductions.size(); i++) {
            System.out.println(i + ": " + numberedProductions.get(i));
        }

        List<String> listOfTerminalsAndNonTerminals = new ArrayList<>();
        listOfTerminalsAndNonTerminals.addAll(grammar.NonTerminals);
        listOfTerminalsAndNonTerminals.addAll(grammar.Terminals);

        this.states = this.canonicalCollection();
        for (int i = 0; i < this.states.size(); i++) {
            this.actionsParsingTable.add(null);
        }

        Item acceptItem = new Item("S_", new ArrayList<>(List.of(this.grammar.StartingSymbol, ".")), 1);
        for (int i = 0; i < this.states.size(); i++) {
            for(Item item : this.states.get(i).items) {
                if (item.equals(acceptItem)) {
                    this.actionsParsingTable.set(i, "acc");
                    continue;
                }
                if (item.dotIndex != item.rightHandSide.size() - 1) {
                    if (this.actionsParsingTable.get(i) != null && this.actionsParsingTable.get(i).contains("reduce")) {
                        System.out.println("Shift - reduce conflict at state: " + this.states.get(i));
                    }
                    this.actionsParsingTable.set(i, "shift");
                }
                else {
                    if (this.actionsParsingTable.get(i) != null && this.actionsParsingTable.get(i).contains("reduce")) {
                        System.out.println("Reduce - reduce conflict at state: " + this.states.get(i));
                    }

                    if (this.actionsParsingTable.get(i) != null && this.actionsParsingTable.get(i).contains("shift")) {
                        System.out.println("Shift - reduce conflict at state: " + this.states.get(i));
                    }

                    List<String> rhs = new ArrayList<>(item.rightHandSide);
                    rhs.remove(rhs.size() - 1);
                    Production production = new Production(item.leftHandSide, rhs);
                    int indexOfProduction = this.numberedProductions.indexOf(production);
                    this.actionsParsingTable.set(i, "reduce " + indexOfProduction);
                }
            }
        }

        System.out.println("\nACTIONS");
        System.out.println(this.actionsParsingTable);

        for (int i = 0; i < this.states.size(); i++) {
            for (String symbol : listOfTerminalsAndNonTerminals) {
                this.gotoParsingTable.put(new Pair<>(i, symbol), null);
            }
        }

        for(int i = 0; i < this.states.size(); i++) {
            for (String symbol : listOfTerminalsAndNonTerminals) {
                State followingState = this.goTo(this.states.get(i), symbol);
                if (followingState != null) {
                    this.gotoParsingTable.put(new Pair<>(i, symbol), this.states.indexOf(followingState));
                }
            }
        }

        System.out.println("\nGOTO");
        for (int i = 0; i < this.states.size(); i++) {
            for (String symbol : listOfTerminalsAndNonTerminals) {
                Integer gotoResult = this.gotoParsingTable.get(new Pair<>(i, symbol));
                if (gotoResult != null) {
                    System.out.println("(state " + i + ",symbol " + symbol + ") = production " + gotoResult);
                }
            }
        }
    }

    public void parseSequence (List<String> sequence) {
        System.out.println("PARSING...");
        List<String> workingStack = new ArrayList<>();
        workingStack.add("$");
        workingStack.add("0");

        List<Integer> outputStack = new ArrayList<>();

        int indexInSequence = 0;

        boolean end = false;
        do {
            int workingStackTopState = Integer.parseInt(workingStack.get(workingStack.size() - 1));
            String actionWorkingStackTopState = this.actionsParsingTable.get(workingStackTopState);
            System.out.println(workingStack);
            System.out.println(actionWorkingStackTopState);
            if (Objects.equals(actionWorkingStackTopState, "shift")) {
                if (indexInSequence >= sequence.size()) {
                    System.out.println("Error on symbol " + sequence.get(indexInSequence - 1));
                    end = true;
                }
                else {
                    workingStack.add(sequence.get(indexInSequence));
                    Integer gotoResult = this.gotoParsingTable.get(new Pair<>(workingStackTopState, sequence.get(indexInSequence)));
                    if (gotoResult.equals(null)) {
                        System.out.println("Error on symbol " + sequence.get(indexInSequence));
                        end = true;
                    }
                    else {
                        workingStack.add(gotoResult.toString());
                        indexInSequence += 1;
                    }
                }
            }
            else if (Objects.equals(actionWorkingStackTopState, "acc")){
                if (indexInSequence != sequence.size()) {
                    if (indexInSequence < sequence.size()) {
                        System.out.println("Error on symbol " + sequence.get(indexInSequence));
                    }
                    else {
                        System.out.println("Error on symbol " + sequence.get(sequence.size() - 1));
                    }
                }
                else {
                    System.out.println("\nACCEPT");
                    Collections.reverse(outputStack);
                    System.out.println(outputStack);

                    System.out.println("\nDERIVATIONS");
                    List<List<String>> derivations = new ArrayList<>();
                    derivations.add(new ArrayList<>(List.of(this.grammar.StartingSymbol)));
                    derivations.add(new ArrayList<>(this.numberedProductions.get(outputStack.get(0)).rightHandSide));
                    for (int i = 1; i < outputStack.size(); i++) {
                        List<String> previousDerivation = derivations.get(derivations.size() - 1);
                        Production production = this.numberedProductions.get(outputStack.get(i));
                        List<String> productionRhs = production.rightHandSide;
                        List<String> newDerivation = new ArrayList<>();
                        for (int j = previousDerivation.size() - 1; j >= 0; j--) {
                            if (Objects.equals(previousDerivation.get(j), production.leftHandSide)) {
                                for (int k = 0; k < j; k++) {
                                    newDerivation.add(previousDerivation.get(k));
                                }
                                newDerivation.addAll(productionRhs);
                                for (int k = j + 1; k < previousDerivation.size(); k++) {
                                    newDerivation.add(previousDerivation.get(k));
                                }
                                break;
                            }
                        }
                        derivations.add(newDerivation);
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    for(List<String> derivation : derivations) {
                        for (String symbol : derivation) {
                            stringBuilder.append(symbol).append(" ");
                        }
                        stringBuilder.append("=> ");
                    }
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    System.out.println(stringBuilder);

                    List<SyntaxTreeNode> syntaxTree = new ArrayList<>();
                    syntaxTree.add(new SyntaxTreeNode(1, this.grammar.StartingSymbol, 0, 0));

                    System.out.println("\nSYNTAX TREE");

                    for (int i = 0; i < outputStack.size(); i++) {
                        Production production = this.numberedProductions.get(outputStack.get(i));
                        for (int index = syntaxTree.size(); index >= 1; index--) {
                            boolean hasChildren = false;
                            for (int j = index - 1; j < syntaxTree.size(); j++) {
                                if (syntaxTree.get(j).parentIndex == index) {
                                    hasChildren = true;
                                    break;
                                }
                            }
                            if (!hasChildren && syntaxTree.get(index - 1).info.equals(production.leftHandSide)) {
                                syntaxTree.add(new SyntaxTreeNode(syntaxTree.size() + 1, production.rightHandSide.get(0), index, 0));
                                for (int j = 1; j < production.rightHandSide.size(); j++) {
                                    syntaxTree.add(new SyntaxTreeNode(syntaxTree.size() + 1, production.rightHandSide.get(j), index, syntaxTree.size()));
                                }
                                break;
                            }
                        }

                    }
                    for (SyntaxTreeNode node : syntaxTree) {
                        System.out.println(node);
                    }

                }
                end = true;
            }
            else if (actionWorkingStackTopState.startsWith("reduce")) {
                int reduceProductionIndex = Integer.parseInt(actionWorkingStackTopState.split(" ")[1]);
                Production reduceProduction = this.numberedProductions.get(reduceProductionIndex);
                for (int i = 0; i < reduceProduction.rightHandSide.size() * 2; i++) {
                    workingStack.remove(workingStack.size() - 1);
                }
                int currentWorkingStackTop = Integer.parseInt(workingStack.get(workingStack.size() - 1));
                workingStack.add(reduceProduction.leftHandSide);


                Integer gotoResult = this.gotoParsingTable.get(new Pair<>(currentWorkingStackTop, reduceProduction.leftHandSide));
                if (gotoResult.equals(null)) {
                    System.out.println("Error on symbol " + sequence.get(indexInSequence));
                    end = true;
                }
                else
                {
                    workingStack.add(gotoResult.toString());
                    outputStack.add(reduceProductionIndex);
                }
            }
            else {
                System.out.println("Error on symbol" + sequence.get(indexInSequence));
                end = true;
            }
        }
        while (!end);

    }

}
