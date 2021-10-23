import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tokens {
    public static final List<Character> simpleOperators = new ArrayList<>(
            Arrays.asList('+','-','*','/','%','<','>'));
    public static final List<String> compoundOperators = new ArrayList<>(
            Arrays.asList(">>","<<","<-","->","<=",">=","==","!="));
    public static final List<String> operators = new ArrayList<>(
            Arrays.asList(">>","<<","<-","->","<=",">=","==","!=","+","-","*","/","%","<",">"));

    public static final String beginningOfOperator = "+-*/%<>=!";
    public static final String separators = ";:,{}()[]";
    public static final List<String> reservedWords = new ArrayList<>(
            Arrays.asList("program", "main", "const", "declarations", "statements", "integer", "character",
                    "boolean", "string", "array", "in", "out", "while", "for", "if", "else", "and", "or"));

    public static final String IDENTIFIER_PATTERN = "(^[a-zA-Z]([a-zA-Z0-9]){0,255}$)|(^[a-zA-Z]([a-zA-Z0-9_]){0,254}[a-zA-Z0-9]$)";
    public static final String INTEGER_CONSTANT_PATTERN = "^[+-]?([1-9][0-9]*)|0$";
    public static final String CHARACTER_CONSTANT_PATTERN = "^'[a-zA-Z0-9]'$";
    public static final String STRING_CONSTANT_PATTERN = "^\"[a-zA-Z0-9 :!?.]*\"$";
    public static final String BOOLEAN_CONSTANT_PATTERN = "^true|false$";

    public static boolean isIdentifier(String token) {
        return token.matches(Tokens.IDENTIFIER_PATTERN);
    }


    public static boolean isConstant(String token) {
        return isIntegerConstant(token) || isBooleanConstant(token) || isStringConstant(token) || isCharacterConstant(token);
    }

    public static boolean isIntegerConstant(String token) {
        return token.matches(Tokens.INTEGER_CONSTANT_PATTERN);
    }

    public static boolean isCharacterConstant(String token) {
        return token.matches(Tokens.CHARACTER_CONSTANT_PATTERN);
    }

    public static boolean isStringConstant(String token) {
        return token.matches(Tokens.STRING_CONSTANT_PATTERN);
    }

    public static boolean isBooleanConstant(String token) {
        return token.matches(Tokens.BOOLEAN_CONSTANT_PATTERN);
    }

    public static boolean isReservedWord(String token) {
        return Tokens.reservedWords.contains(token.toLowerCase());
    }

    public static boolean isOperator(String token) {
        return Tokens.operators.contains(token);
    }

    public static boolean isBeginningOfOperator(Character character) {
        return Tokens.beginningOfOperator.indexOf(character) != -1;
    }

    public static boolean isSeparator(String token) {
        return Tokens.separators.contains(token);
    }
}
