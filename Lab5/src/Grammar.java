import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar {
    private Set<String> NonTerminals = new HashSet<>();
    private Set<String> Terminals = new HashSet<>();
    private String StartingSymbol;
    private HashMap<List<String>, Set<List<String>>> Productions = new HashMap<>();
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

           this.Terminals = Arrays.stream(lines.get(1).split(" "))
                                .collect(Collectors.toSet());

           this.StartingSymbol = lines.get(2).strip();
           if(!this.NonTerminals.contains(this.StartingSymbol)) {
               throw new RuntimeException("Starting Symbol not in the set of non terminals!");
           }

            for(int i = 3; i < lines.size(); i++) {
                String[] sides = lines.get(i).split("::=");
                String[] leftHandSide = sides[0].split(" ");
                String[] rightHandSide = sides[1].split("|");

                List<String> lhs = new ArrayList<>();
                for (String l : leftHandSide) {
                    if (!this.NonTerminals.contains(l) && !this.Terminals.contains(l)) {
                        throw new RuntimeException("Production lhs does not contain a valid element!");
                    }
                    lhs.add(l);
                }
                if (!Productions.containsKey(lhs))
                    this.Productions.put(lhs, new HashSet<>());

                for(String resultOfProduction : rightHandSide) {
                    List<String> rhs = new ArrayList<>();
                    String[] result = resultOfProduction.split(" ");
                    for(String r : result) {
                        if (!this.NonTerminals.contains(r) && !this.Terminals.contains(r) && !r.equals("epsilon")) {
                            throw new RuntimeException("Production rhs does not contain a valid element!");
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
    }

    public void printProductionsForNonTerminal(String NonTerminal) {
        StringBuilder stringBuilder = new StringBuilder();
        for(List<String> lhs : this.Productions.keySet()) {
            if(lhs.contains(NonTerminal)) {
                Set<List<String>> rhs = this.Productions.get(lhs);
                stringBuilder.append(NonTerminal).append("->");
                for(List<String> oneProduction : rhs) {
                    for(String element : oneProduction) {
                        stringBuilder.append(element);
                    }
                    stringBuilder.append("|");
                }
            }
        }
        if(stringBuilder.isEmpty()) {
            System.out.println("No production for the given terminal");
        }
        else {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            System.out.println(stringBuilder);
        }
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
