import java.util.List;
import java.util.Objects;

public class Item {
    public String leftHandSide;
    public List<String> rightHandSide;
    public int dotIndex;

    public Item(String leftHandSide, List<String> rightHandSide, int dotIndex) {
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
        this.dotIndex = dotIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        if (rightHandSide.size() != item.rightHandSide.size()) {
            return false;
        }
        for (int i = 0; i < rightHandSide.size(); i++) {
            if (!Objects.equals(rightHandSide.get(i), item.rightHandSide.get(i)))
                return false;
        }
        return dotIndex == item.dotIndex && leftHandSide.equals(item.leftHandSide);
    }

    @Override
    public String toString() {
        return "Item{" +
                "leftHandSide='" + leftHandSide + '\'' +
                ", rightHandSide=" + rightHandSide +
                ", dotIndex=" + dotIndex +
                '}';
    }
}
