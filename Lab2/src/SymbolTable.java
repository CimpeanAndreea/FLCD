import java.util.Arrays;
import java.util.Objects;

public class SymbolTable {
    private int capacity;

    private Node[] hashTable;

    public SymbolTable(int capacity) {
        this.capacity = capacity;
        hashTable = new Node[this.capacity];
        for(int i = 0; i < this.capacity; i++) {
            hashTable[i] = null;
        }
    }

    public int hash(String symbol) {
        int sum = 0; // sum of ASCII codes of the characters
        for(int i = 0; i < symbol.length(); i++) {
            char character = symbol.charAt(i);
            sum += (int) character;
        }
        return sum % this.capacity;
    }

    public Pair<Integer, Integer> getSymbolPosition (String symbol) {
        int firstIndex = this.hash(symbol);
        int secondIndex = 0;

        Node currentNode = this.hashTable[firstIndex];
        if (currentNode == null) {
            return null;
        }
        else{
            while(currentNode != null) {
                if(Objects.equals(currentNode.getValue(), symbol)) {
                    return new Pair<>(firstIndex, secondIndex);
                }
                secondIndex++;
                currentNode = currentNode.getNext();
            }
            return null;
        }
    }

    public boolean insertSymbol(String symbol) {
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

    @Override
    public String toString() {
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
