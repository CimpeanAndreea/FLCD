public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner("src/programs/p3.txt");
        scanner.scan();
        //System.out.println(scanner.getPIF());
        //scanner.writePIF("src/pif.csv");
        scanner.writeTxtPIF("src/pif.txt");
        scanner.writeTxtST("src/st.txt");
    }
}
