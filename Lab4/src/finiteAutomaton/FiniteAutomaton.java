package finiteAutomaton;

/*
    A finite automaton (FA) is a 5-tuple M = (Q,Sigma,delta,q0,F) where
        -> Q = finite set of states
        -> Sigma = finite alphabet
        -> delta = transition function delta:QxSigma->P(Q)
        -> q0 = initial state (belongs to Q)
        -> F = set of final states (included or equal to Q)

    DFA(Deterministic Finite Automaton) => |delta(q,a)|<=1 at most one state obtained as a result of a transition
 */


import symbolTable.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FiniteAutomaton {
    private String fileName;

    private List<String> states;
    private List<String> alphabet;
    private String initialState;
    private List<String> finalStates;
    private Map<Pair<String, String>, List<String>> transitions = new HashMap<>();

    public FiniteAutomaton(String fileName) {
        this.fileName = fileName;
        this.readFAFromFile();
    }

    public void readFAFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.fileName))) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            this.states = Arrays.stream(lines.get(0).split(","))
                                .map(String::strip)
                                .collect(Collectors.toList());
            //System.out.println(Q);

            this.alphabet = Arrays.stream(lines.get(1).split(","))
                                .map(String::strip)
                                .collect(Collectors.toList());
            //System.out.println(Sigma);

            this.initialState = lines.get(2).strip();
            //System.out.println(q0);

            this.finalStates = Arrays.stream(lines.get(3).split(","))
                                .map(String::strip)
                                .collect(Collectors.toList());

            //System.out.println(F);

            for(int i = 4; i < lines.size(); i++) {
                String[] sides = lines.get(i).split("\\|-");
                String[] leftHandSide = sides[0].strip().split("->");
                String rightHandSide = sides[1].strip();

                String startingState = leftHandSide[0].strip();
                List<String> alphabetLetters = Arrays.stream(leftHandSide[1].split(","))
                                                            .map(String::strip)
                                                            .collect(Collectors.toList());

                List<String> resultStates = Arrays.stream(rightHandSide.split(","))
                                                        .map(String::strip)
                                                        .collect(Collectors.toList());

                for (String alphabetLetter : alphabetLetters) {
                    Pair<String, String> key = new Pair<>(startingState, alphabetLetter);
                    if (this.transitions.containsKey(key)) {
                        List<String> result = new ArrayList<>();
                        result.addAll(this.transitions.get(key));
                        result.addAll(resultStates);
                        this.transitions.put(key, result);
                    }
                    else {
                        this.transitions.put(new Pair<>(startingState, alphabetLetter), resultStates);
                    }
                }
            }
            //System.out.println(this.delta);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isDFA() {
        return this.transitions.values().stream().noneMatch(s -> s.size() > 1);
    }

    public boolean isAcceptedSequence(String sequence) {
        if (!this.isDFA()) {
            throw new RuntimeException("It is not a deterministic finite automaton.");
        }
        String current = this.initialState;
        for (int i = 0; i < sequence.length(); i++) {
            Pair<String, String> key = new Pair<>(current, String.valueOf(sequence.charAt(i)));
            if (this.transitions.containsKey(key)) {
                current = this.transitions.get(key).get(0);
            }
            else {
                return false;
            }
        }
        return this.finalStates.contains(current);
    }

    public List<String> getStates() {
        return this.states;
    }

    public List<String> getFinalStates() {
        return this.finalStates;
    }

    public List<String> getAlphabet() {
        return this.alphabet;
    }

    public String getInitialState() {
        return this.initialState;
    }

    public Map<Pair<String, String>, List<String>> getTransitions() {
        return this.transitions;
    }
}
