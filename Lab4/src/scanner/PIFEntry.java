package scanner;

import symbolTable.Pair;

public class PIFEntry {
    private String token;
    private Pair<Integer, Integer> positionInSymbolTable;

    public PIFEntry(String token, Pair<Integer, Integer> positionInSymbolTable) {
        this.token = token;
        this.positionInSymbolTable = positionInSymbolTable;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Pair<Integer, Integer> getPositionInSymbolTable() {
        return positionInSymbolTable;
    }

    public void setPositionInSymbolTable(Pair<Integer, Integer> positionInSymbolTable) {
        this.positionInSymbolTable = positionInSymbolTable;
    }

    @Override
    public String toString() {
        return "token:'" + token  + "'positionInSymbolTable:'" + positionInSymbolTable+"'";
    }
}
