import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Parser {
    private Grammar grammar;
    private List<Production> numberedProductions;

    public Parser(String grammarFile) {
        this.grammar = new Grammar(grammarFile);
        this.numberedProductions = new ArrayList<>();
        if (this.grammar.checkContextFreeGrammar()) {
            Set<List<String>> leftHandSides = this.grammar.Productions.keySet();
            for (List<String> lhs : leftHandSides) {
                Set<List<String>> rightHandSides = this.grammar.Productions.get(lhs);
                for(List<String> rhs : rightHandSides)
                this.numberedProductions.add(new Production(lhs.get(0), rhs));
            }
        }
    }
}
