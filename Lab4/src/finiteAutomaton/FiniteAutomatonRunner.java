package finiteAutomaton;

import symbolTable.Pair;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FiniteAutomatonRunner {
    private FiniteAutomaton finiteAutomaton;
    private String fileName;

    public FiniteAutomatonRunner(String fileName) {
        this.fileName = fileName;
        this.finiteAutomaton = new FiniteAutomaton(this.fileName);
    }

    public void printMenu() {
        System.out.println("\n-------------MENU-------------");
        System.out.println("1. Print the set of states");
        System.out.println("2. Print the alphabet");
        System.out.println("3. Print the initial state");
        System.out.println("4. Print the the set of final states");
        System.out.println("5. Print transitions");
        System.out.println("6. Check if DFA");
        System.out.println("7. Check if a sequence is accepted");
        System.out.println("0. Exit");
        System.out.println("Your option: ");
    }

    public void run() {
        Scanner consoleReader = new Scanner(System.in);
        while (true) {
            this.printMenu();
            String command = consoleReader.nextLine();
            switch (command) {
                case "0" -> {
                    System.out.println("Program closed!");
                    return;
                }
                case "1" -> printStates();
                case "2" -> printAlphabet();
                case "3" -> printInitialState();
                case "4" -> printFinalStates();
                case "5" -> printTransitions();
                case "6" -> printCheckDFA();
                case "7" -> printCheckAcceptedSequence();
                default -> System.out.println("Invalid command!");
            }
        }

    }

    public void printStates() {
        System.out.println("\nSTATES");
        System.out.println(this.finiteAutomaton.getStates());
    }

    public void printAlphabet() {
        System.out.println("\nALPHABET");
        System.out.println(this.finiteAutomaton.getAlphabet());
    }

    public void printInitialState() {
        System.out.println("\nINITIAL STATE");
        System.out.println(this.finiteAutomaton.getInitialState());
    }

    public void printFinalStates() {
        System.out.println("\nFINAL STATES");
        System.out.println(this.finiteAutomaton.getFinalStates());
    }

    public void printTransitions() {
        System.out.println("\nTRANSITIONS");
        Map<Pair<String, String>, List<String>> transitions = this.finiteAutomaton.getTransitions();
        transitions.forEach((key, value) -> value.forEach(value1 -> {
            System.out.println("δ(" + key.getFirst() + "," + key.getSecond() + ")=" + value1);
        }));
    }

    public void printCheckDFA() {
        System.out.println("IS DFA");
        System.out.println(this.finiteAutomaton.isDFA());
    }

    public void printCheckAcceptedSequence() {
        System.out.println("CHECK ACCEPTED SEQUENCE");
        System.out.println("Give sequence: ");
        Scanner consoleReader = new Scanner(System.in);
        String sequence = consoleReader.nextLine();
        try {
            System.out.println(this.finiteAutomaton.isAcceptedSequence(sequence));
        }
        catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}
