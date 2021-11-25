import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Production {
    public String leftHandSide;
    public List<String> rightHandSide;

    public Production(String leftHandSide, List<String> rightHandSide) {
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production that = (Production) o;
        return leftHandSide.equals(that.leftHandSide) && rightHandSide.equals(that.rightHandSide);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftHandSide, rightHandSide);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.leftHandSide).append("->");
        for(String element : this.rightHandSide) {
            stringBuilder.append(element);
        }
        return stringBuilder.toString();
    }
}
