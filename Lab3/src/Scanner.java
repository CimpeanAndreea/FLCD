import org.w3c.dom.css.CSSValue;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Scanner {
    public final String IDENTIFIER_PATTERN = "(^[a-zA-Z]([a-zA-Z0-9]){0,255}$)|(^[a-zA-Z]([a-zA-Z0-9_]){0,254}[a-zA-Z0-9]$)";
    public final String CONST_IDENTIFIER_PATTERN = "(^[A-Z]{1,256}$)|(^[A-Z][A-Z_]{0,254}[A-Z]$)";
    public final String INTEGER_CONSTANT_PATTERN = "^[+-]?([1-9][0-9]*)|0$";
    public final String CHARACTER_CONSTANT_PATTERN = "^'[a-zA-Z0-9]'$";
    public final String STRING_CONSTANT_PATTERN = "^\"[a-zA-Z0-9 :!?.]*\"$";
    public final String BOOLEAN_CONSTANT_PATTERN = "^true|false$";

    private String programFileName;

    private SymbolTable symbolTable = new SymbolTable(30);
    private ProgramInternalForm PIF = new ProgramInternalForm();

    private List<String> programLines = new ArrayList<>();
    private List<String> tokens = new ArrayList<>();

    private String beginningOfOperator = "+-*/%<>=!";

    private List<Character> simpleOperators = new ArrayList<>(
            Arrays.asList('+','-','*','/','%','<','>'));
    private List<String> compoundOperators = new ArrayList<>(
            Arrays.asList(">>","<<","<-","->","<=",">=","==","!="));
    private List<String> operators = new ArrayList<>(
            Arrays.asList(">>","<<","<-","->","<=",">=","==","!=","+","-","*","/","%","<",">"));

    public final String separators = ";:,{}()[]";
    public final List<String> reservedWords = new ArrayList<>(
            Arrays.asList("program", "main", "const", "declarations", "statements", "integer", "character",
                    "boolean", "string", "array", "in", "out", "while", "for", "if", "else", "and", "or"));

    public Scanner(String programFileName)
    {
        this.programFileName = programFileName;
        this.readTokens();
    }

    public void scan() throws LexicalError
    {
        this.readProgram(programFileName);
        for(int i = 0; i < programLines.size(); i++)
        {
            String line = this.programLines.get(i);
            List<String> lineTokens = getTokensFromLine(line.strip(), i + 1);
            //System.out.println("line " + (i+1) + ":" + line) ;
            //System.out.println(lineTokens);
            for(String token : lineTokens) {
                if(isReservedWord(token) || isSeparator(token) || isOperator(token)) {
                    this.PIF.add(token, new Pair<>(-1, -1));
                }
                else {
                    //System.out.println(token);
                    if(isStringConstant(token) || isCharacterConstant(token) || isBooleanConstant(token) || isIntegerConstant(token)) {
                        this.symbolTable.insertSymbol(token);
                        this.PIF.add(token, this.symbolTable.getSymbolPosition(token));
                    }
                    else if(isIdentifier(token)) {
                        this.symbolTable.insertSymbol(token);
                        this.PIF.add(token, this.symbolTable.getSymbolPosition(token));
                    }
                    else {
                        throw new LexicalError(token, i + 1, "Unidentified token");
                    }
                }
            }
        }
    }

    public List<String> getTokensFromLine(String line, int indexLine) {
        List<String> lineTokens = new ArrayList<>();
        int lineLength = line.length();
        int index = 0;

        StringBuilder currentToken = new StringBuilder();

        while(index < lineLength) {
            char currentCharacter = line.charAt(index);

            if(currentCharacter == ' ' && !currentToken.isEmpty()) {
                lineTokens.add(currentToken.toString());
                currentToken.setLength(0);
            }

            while(currentCharacter == ' '){
                index += 1;
                currentCharacter = line.charAt(index);
            }

            if(isBeginningOfOperator(currentCharacter)) {
                if(!currentToken.isEmpty()) {
                    lineTokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }

                String operator = String.valueOf(currentCharacter);
                if(index < lineLength - 1) {
                    String compoundOperator = operator + line.charAt(index + 1);
                    if(compoundOperators.contains(compoundOperator)) {
                        lineTokens.add(compoundOperator);
                        index += 2;
                    }
                    else {
                        lineTokens.add(operator);
                        index += 1;
                    }
                }
                else {
                    lineTokens.add(operator);
                    index += 1;
                }
            }

            else if(isSeparator(String.valueOf(currentCharacter))) {
                if(!currentToken.isEmpty()) {
                    lineTokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                lineTokens.add(String.valueOf(currentCharacter));
                index += 1;
            }

            else if(currentCharacter == '\'') {
                if(!currentToken.isEmpty()) {
                    lineTokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }

                currentToken.append(currentCharacter);
                index += 1;
                while(index < lineLength && line.charAt(index) != '\'') {
                    currentToken.append(line.charAt(index));
                    index += 1;
                }
                if(index == lineLength) {
                    throw new LexicalError(currentToken.toString(), indexLine, "missing closing '");
                }
                else {
                    currentToken.append(line.charAt(index));
                    index += 1;
                    lineTokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
            }

            else if(currentCharacter == '\"') {
                if(!currentToken.isEmpty()) {
                    lineTokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }

                currentToken.append(currentCharacter);
                index += 1;
                while(index < lineLength && line.charAt(index) != '\"') {
                    currentToken.append(line.charAt(index));
                    index += 1;
                }
                if(index == lineLength) {
                    throw new LexicalError(currentToken.toString(), indexLine, "missing closing \"");
                }
                else {
                    currentToken.append(line.charAt(index));
                    index += 1;
                    lineTokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
            }

            else {
                currentToken.append(currentCharacter);
                index += 1;
            }
        }
        if(!currentToken.isEmpty()) {
            lineTokens.add(currentToken.toString());
        }
        return lineTokens;
    }

    public boolean isBeginningOfOperator(Character character) {
        return beginningOfOperator.indexOf(character) != -1;
    }

    public boolean isSeparator(String token) {
        return separators.contains(token);
    }


    public void readTokens() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/token.in"));
            this.tokens = reader.lines()
                                .map(String::trim)
                                .collect(Collectors.toList());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readProgram(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            this.programLines = reader.lines().collect(Collectors.toList());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isIdentifier(String token) {
        return token.matches(IDENTIFIER_PATTERN);
    }

    public boolean isConstantIdentifier(String token) {
        return token.matches(CONST_IDENTIFIER_PATTERN);
    }

    public boolean isIntegerConstant(String token) {
        return token.matches(INTEGER_CONSTANT_PATTERN);
    }

    public boolean isCharacterConstant(String token) {
        return token.matches(CHARACTER_CONSTANT_PATTERN);
    }

    public boolean isStringConstant(String token) {
        return token.matches(STRING_CONSTANT_PATTERN);
    }

    public boolean isBooleanConstant(String token) {
        return token.matches(BOOLEAN_CONSTANT_PATTERN);
    }

    public boolean isReservedWord(String token) {
        return reservedWords.contains(token.toLowerCase());
    }

    public boolean isOperator(String token) {
        return operators.contains(token);
    }

    public ProgramInternalForm getPIF() {
        return this.PIF;
    }

    public void writePIF(String fileName) {
        List<PIFEntry> pifEntries = this.PIF.getEntries();
        try {

            PrintWriter writer = new PrintWriter(fileName);

            for(PIFEntry entry: pifEntries) {
                String[] data = {entry.getToken(), entry.getPositionInSymbolTable().toString()};
                writer.println(this.convertToCSV(data));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeTxtPIF(String fileName) {
        List<PIFEntry> pifEntries = this.PIF.getEntries();
        try {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
            for(PIFEntry entry: pifEntries) {
                System.out.println(entry);
                writer.println(entry);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeTxtST(String fileName) {
        System.out.println(this.symbolTable);
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data;
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

}
