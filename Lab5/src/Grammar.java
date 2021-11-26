import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar {
    public Set<String> NonTerminals = new HashSet<>();
    public Set<String> Terminals = new HashSet<>();
    public String StartingSymbol;
    public HashMap<List<String>, Set<List<String>>> Productions = new HashMap<>();
    private String fileName;

    public Grammar(String fileName) {
        this.fileName = fileName;
        this.readFromFile();
    }

    public void readFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.fileName))) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            this.NonTerminals = Arrays.stream(lines.get(0).split(" "))
                                    .collect(Collectors.toSet());

            //System.out.println(this.NonTerminals);

            this.Terminals = Arrays.stream(lines.get(1).split(" "))
                                .collect(Collectors.toSet());

            //System.out.println(this.Terminals);

            this.StartingSymbol = lines.get(2).strip();
            if(!this.NonTerminals.contains(this.StartingSymbol)) {
                throw new RuntimeException("Starting Symbol not in the set of non terminals!");
            }

            //System.out.println(this.StartingSymbol);

            for(int i = 3; i < lines.size(); i++) {
                String[] sides = lines.get(i).strip().split("::=");
                String[] leftHandSide = sides[0].strip().split(" ");
                String[] rightHandSide = sides[1].strip().split("\\|");

                List<String> lhs = new ArrayList<>();
                for (String l : leftHandSide) {
                    if (!this.NonTerminals.contains(l.strip()) && !this.Terminals.contains(l.strip())) {
                        throw new RuntimeException("Production lhs does not contain a valid element: " + l + " !");
                    }
                    lhs.add(l.strip());
                }
                if (!Productions.containsKey(lhs))
                    this.Productions.put(lhs, new HashSet<>());

                for(String resultOfProduction : rightHandSide) {
                    List<String> rhs = new ArrayList<>();
                    String[] result = resultOfProduction.strip().split(" ");
                    for(String r : result) {
                        if (!this.NonTerminals.contains(r) && !this.Terminals.contains(r) && !r.equals("epsilon")) {
                            throw new RuntimeException("Production rhs does not contain a valid element: " + r + " !");
                        }
                        rhs.add(r);
                    }
                    this.Productions.get(lhs).add(rhs);
                }

            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printNonTerminals() {
        System.out.println("\nNON TERMINALS");
        System.out.println(this.NonTerminals);
    }

    public void printTerminals() {
        System.out.println("\nTERMINALS");
        System.out.println(this.Terminals);
    }

    public void printStartingSymbol() {
        System.out.println("\nSTARTING SYMBOL");
        System.out.println(this.StartingSymbol);
    }

    public void printProductions() {
        System.out.println("\nPRODUCTIONS");
        StringBuilder stringBuilder = new StringBuilder();
        for(List<String> lhs : this.Productions.keySet()) {
            String left = String.join(" ", lhs);
            Set<List<String>> rhs = this.Productions.get(lhs);
            stringBuilder.append(left).append(" -> ");
            for(List<String> oneProduction : rhs) {
                for(String element : oneProduction) {
                    stringBuilder.append(element).append(" ");
                }
                stringBuilder.append(" | ");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("\n");
        }
        System.out.println(stringBuilder);
    }

    public void printProductionsForNonTerminal(String nonTerminal) {
        System.out.println("\nPRODUCTIONS FOR NON TERMINAL:" + nonTerminal);
        StringBuilder stringBuilder = new StringBuilder();
        for(List<String> lhs : this.Productions.keySet()) {
            if(lhs.contains(nonTerminal) && Objects.equals(lhs.get(0), nonTerminal)) {
                Set<List<String>> rhs = this.Productions.get(lhs);
                stringBuilder.append(nonTerminal).append(" -> ");
                for(List<String> oneProduction : rhs) {
                    for(String element : oneProduction) {
                        stringBuilder.append(element).append(" ");
                    }
                    stringBuilder.append(" | ");
                }
            }
        }
        if(stringBuilder.isEmpty()) {
            System.out.println("No production for the given nonterminal");
        }
        else {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            System.out.println(stringBuilder);
        }
    }

    public Set<List<String>> getProductionsForNonTerminal(String nonTerminal) {
        List<String> lhs = new ArrayList<>();
        lhs.add(nonTerminal);
        return this.Productions.get(lhs);
    }

    public boolean checkContextFreeGrammar() {
        boolean beginsWithStartingSymbol = false;
        for(List<String> lhs : this.Productions.keySet()) {
            if(lhs.size() > 1) {
                return false;
            }
            if(lhs.contains(this.StartingSymbol)) {
                beginsWithStartingSymbol = true;
            }
        }
        return beginsWithStartingSymbol;
    }

}
