import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SymbolTable {
    private int capacity; //capacity of the hash table
    private Node[] hashTable; //hash table with linked lists

    public SymbolTable(int capacity) {
        this.capacity = capacity;
        hashTable = new Node[this.capacity];
        for(int i = 0; i < this.capacity; i++) {
            hashTable[i] = null;
        }
    }

    public int hash(String symbol) {
        /**
         * Hash function used for a symbol to find the position of its corresponding linked list in the hash table
         *
         */
        int sum = 0; // sum of ASCII codes of the characters
        for(int i = 0; i < symbol.length(); i++) {
            char character = symbol.charAt(i);
            sum += (int) character;
        }
        return sum % this.capacity;
    }

    public Pair<Integer, Integer> getSymbolPosition (String symbol) {
        /**
         * Get the position of the symbol in the symbol table as a pair (firstIndex, secondIndex)
         * corresponding to (hash value, position in linked list)
         *
         * @param symbol
         *
         * @retrun a Pair if symbol exists in the symbol table or null otherwise
         *
         */
        int firstIndex = this.hash(symbol);
        int secondIndex = 0;

        Node currentNode = this.hashTable[firstIndex];
        while (currentNode != null) {
            if (Objects.equals(currentNode.getValue(), symbol)) {
                return new Pair<>(firstIndex, secondIndex);
            }
            secondIndex++;
            currentNode = currentNode.getNext();
        }
        return null;
    }

    public boolean insertSymbol(String symbol) {
        /**
         * Insert symbol in the symbol table
         *
         * @param symbol: symbol to be inserted
         *
         * @return true if symbol was inserted
         *         false if symbol was not inserted i.e. it already existed in the symbol table
         *
         */
        int hashValue = this.hash(symbol);
        if(this.hashTable[hashValue] == null) {
            Node newNode = new Node(symbol, null);
            this.hashTable[hashValue] = newNode;
            return true;
        }
        else {
            Node currentNode = this.hashTable[hashValue];
            while(currentNode.getNext() != null && !Objects.equals(currentNode.getValue(), symbol)) {
                currentNode = currentNode.getNext();
            }
            if(currentNode.getValue().equals(symbol)) {
                return false;
            }
            else {
                Node newNode = new Node(symbol, null);
                currentNode.setNext(newNode);
                return true;
            }
        }
    }

    public List<Pair<String, Pair<Integer, Integer>>> getAllSymbols() {
        List<Pair<String, Pair<Integer, Integer>>> symbols = new ArrayList<>();
        for(int i = 0; i < this.capacity; i++) {
            if(this.hashTable[i] != null) {
                Node currentNode = this.hashTable[i];
                int secondIndex = 0;
                while(currentNode != null) {
                    symbols.add(new Pair<>(currentNode.getValue(), new Pair<>(i, secondIndex)));
                    currentNode = currentNode.getNext();
                    secondIndex++;
                }
            }
        }
        return symbols;
    }

    @Override
    public String toString() {
        /**
         * Represent the symbol table as a string in a more readable form for printing
         *
         */
        StringBuilder str = new StringBuilder("SymbolTable{" + "capacity=" + capacity + "\n");
        for(int i = 0; i < this.capacity; i++) {
            str.append(i).append(": ");
            if(this.hashTable[i] == null)
                str.append("[]");
            else
                str.append(this.hashTable[i].toString());
            str.append("\n");
        }
        str.append("}");
        return str.toString();
    }
}
