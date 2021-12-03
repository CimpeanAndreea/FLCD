import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class State {
    public List<Item> items = new ArrayList<>();

    public State(List<Item> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        if (items.size() != state.items.size())
            return false;
        for (Item item : items) {
            if (!state.items.contains(item))
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Item item : this.items) {
            stringBuilder.append("[ ").append(item.leftHandSide).append(" -> ");
            for (String rhs : item.rightHandSide) {
                stringBuilder.append(rhs).append(" ");
            }
            stringBuilder.append("]\n");
        }
        return stringBuilder.toString();
    }
}
