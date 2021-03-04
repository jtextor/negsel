package visualization;
import org.jgrapht.graph.DefaultEdge;

public class LabeledEdge extends DefaultEdge {
	static final long serialVersionUID = 1;
	
	private char label;
	private int weight;

	public LabeledEdge(char label, int weight) {
		this.label = label;
		this.weight = weight;
	}

	public String toString() {
		return "" + label + ": " + weight;
	}
}
