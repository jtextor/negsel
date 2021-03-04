package visualization;
import java.util.HashMap;
import java.util.Map;

import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DirectedMultigraph;

import util.StringUtility;

import algorithms.PatternTrie;
import alphabets.Alphabet;



public class PatternTrieJgraphWrapper {
	private PatternTrie st;
	private DirectedGraph<LevelGraphLabel, LabeledEdge> graph;
	private JGraphModelAdapter<LevelGraphLabel, LabeledEdge> adapter;
	
	private int vertexCount = 0;
	
	private Map<PatternTrie,Integer> vertexNumber;
	private Map<PatternTrie,Integer> vertexLevel;

	private LevelGraphLabel traverse( PatternTrie st, PatternTrie parent, int level ){
		int myIndex = -1;
		int myLevel = level;
		String myPrefix = " ";
		if( st.isComplete() ){
			myPrefix = "(Leaf) ";
		}
		/*	if( parent != null && leafNumber.get(parent) == null ){
				myIndex = leafCount ++;
				leafNumber.put(parent,myIndex);
				myPrefix = "(Leaf) ";
			} else {
				if( parent != null ){
					myIndex = leafNumber.get(parent);
					myPrefix = "(Leaf) ";
				}
			}
		} else {*/
			if( vertexNumber.get(st) == null ){
				myIndex = vertexCount ++;
				vertexNumber.put(st,myIndex);
				vertexLevel.put(st,myLevel);
			} else {
				myIndex = vertexNumber.get(st);
				myLevel = vertexLevel.get(st);
			}
		// }
		LevelGraphLabel myLabel = new LevelGraphLabel( myLevel, "",/*myPrefix+myIndex,*/
				"["+StringUtility.join(st.weights,",")+"]" );
		graph.addVertex( myLabel );
		for( char c : st.getChildren().keySet() ){
			LevelGraphLabel childLabel = traverse( st.getChildren().get( c ), st, level+1 );
			graph.addEdge( myLabel, childLabel, new LabeledEdge(c, 
					st.getWeight(Alphabet.get().letters().indexOf(c))));
		}
		return myLabel;
	}
	
	public PatternTrieJgraphWrapper( PatternTrie _st ){
		vertexNumber = new HashMap<PatternTrie, Integer>();
		vertexLevel = new HashMap<PatternTrie, Integer>();
		st = _st;
		graph = new DirectedMultigraph<LevelGraphLabel, LabeledEdge>( LabeledEdge.class );
		if( st.getChildren() != null ) // traverse only nonempty graphs
			traverse( st, null, 1 );
		adapter = new JGraphModelAdapter<LevelGraphLabel, LabeledEdge>( graph );
	}

	public JGraphModelAdapter<LevelGraphLabel, LabeledEdge> getAdapter() {
		return adapter;
	}

	public DirectedGraph<LevelGraphLabel, LabeledEdge> getGraph() {
		return graph;
	}
}
