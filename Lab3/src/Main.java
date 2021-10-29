import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
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
