import java.util.List;
import java.util.Objects;

public class Item {
    public String nonTerminal;
    public List<String> productionResult;
    public int dotIndex;

    public Item(String nonTerminal, List<String> productionResult, int dotIndex) {
        this.nonTerminal = nonTerminal;
        this.productionResult = productionResult;
        this.dotIndex = dotIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return dotIndex == item.dotIndex && nonTerminal.equals(item.nonTerminal) && productionResult.equals(item.productionResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nonTerminal, productionResult, dotIndex);
    }

    @Override
    public String toString() {
        return "Item{" +
                "nonTerminal='" + nonTerminal + '\'' +
                ", productionResult=" + productionResult +
                ", dotIndex=" + dotIndex +
                '}';
    }
}
