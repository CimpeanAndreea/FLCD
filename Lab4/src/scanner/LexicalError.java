package scanner;

public class LexicalError extends RuntimeException {
    private int line;
    private String token;
    private String diagnostic;

    public LexicalError(String token, int line, String diagnostic) {
        super("Lexical error at line: " + line + " at token: " + token + " " + diagnostic);
    }

}
