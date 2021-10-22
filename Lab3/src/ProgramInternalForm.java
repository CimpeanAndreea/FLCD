import java.util.ArrayList;
import java.util.List;

public class ProgramInternalForm {
    List<PIFEntry> entries = new ArrayList<>();

    public void add(String token, Pair<Integer, Integer> positionInSymbolTable) {
        this.entries.add(new PIFEntry(token, positionInSymbolTable));
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for(PIFEntry entry : entries) {
            str.append(entry.toString()).append("\n");
        }
        return str.toString();
    }

    public List<PIFEntry> getEntries() {
        return entries;
    }
}
