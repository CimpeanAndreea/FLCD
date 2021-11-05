import finiteAutomaton.FiniteAutomaton;
import finiteAutomaton.FiniteAutomatonRunner;
import scanner.LexicalError;
import scanner.Scanner;

public class Main {
    public static void main(String[] args) {
        FiniteAutomatonRunner testAutomaton = new FiniteAutomatonRunner("src/finiteAutomaton/FA.in");
        testAutomaton.run();

        String program = "p3";
        String programFile = "src/programs/" + program + "/" + program + ".txt";

        Scanner scanner = new Scanner(programFile);

        try {
            scanner.scan();
        }
        catch (LexicalError error) {
            System.out.println(error.getMessage());
        }
        scanner.writePIF("src/programs/" + program + "/" + "pif.csv");
        scanner.writeST("src/programs/" + program + "/" + "st.csv");

    }
}
