import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Scanner {
    private final String programFileName;

    private SymbolTable symbolTable = new SymbolTable(30);
    private ProgramInternalForm PIF = new ProgramInternalForm();

    private List<String> programLines = new ArrayList<>();

    public Scanner(String programFileName)
    {
        this.programFileName = programFileName;
        this.readProgram(this.programFileName);
    }

    /**
     * Classify each token from each line
     * Add each token in PIF (and ST if the case)
     *
     * @throws LexicalError if token can not be identified
     */
    public void scan() throws LexicalError
    {
        for(int i = 0; i < programLines.size(); i++)
        {
            String line = this.programLines.get(i);
            List<String> lineTokens = getTokensFromLine(line.strip(), i + 1);
            for(String token : lineTokens) {
                if(Tokens.isReservedWord(token) || Tokens.isSeparator(token) || Tokens.isOperator(token)) {
                    this.PIF.add(token, new Pair<>(-1, -1));
                }
                else if(Tokens.isConstant(token) || Tokens.isIdentifier(token)) {
                    this.symbolTable.insertSymbol(token);
                    this.PIF.add(token, this.symbolTable.getSymbolPosition(token));
                }
                else {
                    throw new LexicalError(token, i + 1, "Unidentified token");
                }
            }
        }
        System.out.println("Lexically correct");
    }

    /**
     * Take one line of the program character by character and construct tokens
     *
     * @param line - a line of the input program
     * @param indexLine - the number of the line in the program
     * @return - a list of tokens as strings
     */
    public List<String> getTokensFromLine(String line, int indexLine) {
        List<String> lineTokens = new ArrayList<>();
        int lineLength = line.length();
        int index = 0;

        StringBuilder currentToken = new StringBuilder(); //construct a token

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

            if(Tokens.isBeginningOfOperator(currentCharacter)) {
                if(!currentToken.isEmpty()) {
                    lineTokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }

                String operator = String.valueOf(currentCharacter);
                if(index < lineLength - 1) {
                    String compoundOperator = operator + line.charAt(index + 1);
                    if(Tokens.compoundOperators.contains(compoundOperator)) {
                        lineTokens.add(compoundOperator);
                        index += 2;
                    }
                    else if (Character.isDigit(line.charAt(index + 1)) && !Tokens.isIdentifier(lineTokens.get(lineTokens.size() - 1)) && !Tokens.isIntegerConstant(lineTokens.get(lineTokens.size() - 1))) {
                        currentToken.append(operator);
                        index+=1;
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

            else if(Tokens.isSeparator(String.valueOf(currentCharacter))) {
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

    /**
     * Read input program and split it in lines
     *
     * @param fileName - the file containing the program
     */
    public void readProgram(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            this.programLines = reader.lines().collect(Collectors.toList());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ProgramInternalForm getPIF() {
        return this.PIF;
    }

    /**
     * Write PIF in a csv file
     *
     * @param fileName - the output file for PIF
     */
    public void writePIF(String fileName) {
        List<PIFEntry> pifEntries = this.PIF.getEntries();
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(fileName));
            writer.println("Token,Position in ST");
            for(PIFEntry entry: pifEntries) {
                String[] data = {entry.getToken(), entry.getPositionInSymbolTable().toString()};
                writer.println(this.convertToCSV(data));
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write the Symbol Table in a file
     *
     * @param fileName - the output file for ST
     */
    public void writeST(String fileName) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(fileName));
            writer.println("Token,Position");
            for(Pair<String, Pair<Integer, Integer>> symbol : this.symbolTable.getAllSymbols()) {
                String[] data = {symbol.getFirst(), symbol.getSecond().toString()};
                writer.println(this.convertToCSV(data));
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert a list of strings in desired format for writing in a csv file
     *
     * @param data - list of strings
     * @return - data in a format to write in csv
     */
    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    /**
     * Helper function for converting data in a csv format
     */
    public String escapeSpecialCharacters(String data) {
        String escapedData = data;
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

}
