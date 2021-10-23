import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String program = "p2";
        String programFile = "src/programs/" + program + "/" + program + ".txt";

        Scanner scanner = new Scanner(programFile);
        scanner.scan();
        scanner.writePIF("src/programs/" + program + "/" + "pif.csv");
        scanner.writeST("src/programs/" + program + "/" + "st.csv");

    }
}
