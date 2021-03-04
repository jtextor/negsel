package visualization;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import algorithms.PatternTrie;


public class PatternDAG extends ListenableDirectedGraph<LevelVertex, DefaultEdge>{
	private static final long serialVersionUID = 1L;
	
	private Vector<Set<String>> rchunkd;
	
	private void removeDeadEnds( Set<String> leftmostLevel ){
		// create new vertex, link to all vertices on level 0
		LevelVertex source = new LevelVertex(-1, "source"); 
		addVertex( source );
		for( String s : leftmostLevel )
			addEdge( source, new LevelVertex(0, s) );
		
		// visit all vertices reachable from source by DFS
		// save all seen vertices in rchunkd
		DepthFirstIterator<LevelVertex, DefaultEdge> it = new DepthFirstIterator<LevelVertex, DefaultEdge>( this, source );
		it.next(); // skip source vertex
		while( it.hasNext() ){
			LevelVertex v = it.next();
			if( rchunkd.size() == v.getLevel() ){
				rchunkd.add(new HashSet<String>()); 
			}
			rchunkd.get(v.getLevel()).add(v.getLabel());
		}
		removeVertex( source );
		
		// remove all vertices from this graph that are not 
		// in rchunkd
		Set<LevelVertex> vdrop = new HashSet<LevelVertex>();
		for( LevelVertex v : this.vertexSet() ){
			if( rchunkd.size() <= v.getLevel() ){
				// rightmost level is not reachable from leftmost level 
				// -> no detectors can be generated
				vdrop.addAll( vertexSet() );
				break;
			}
			if( !rchunkd.get(v.getLevel()).contains(v.getLabel()) ){
				System.out.println("drop: "+v);
				vdrop.add(v);
			}
		}
		removeAllVertices(vdrop);
	}
	
	public PatternDAG( Vector<PatternTrie> rchunkd ){
		super(DefaultEdge.class);
		int l = rchunkd.size()-1;
		Set<String> leftmostLevel = new HashSet<String>(rchunkd.get(l).content());		
		for( String p : rchunkd.get(l).content() ){
			addVertex( new LevelVertex(l,p) );
		}
		
		for( l-- ; l >= 0 ; l -- ){
			Set<String> nextLevel = leftmostLevel;
			leftmostLevel = new HashSet<String>();
			for( String p1 : rchunkd.get(l).content() ){				
				for( String p2: nextLevel ){
					if( p2.startsWith(p1.substring(1)) ){
						leftmostLevel.add( p1 );
						addVertex( new LevelVertex(l,p1) );
						addEdge( new LevelVertex(l,p1), new LevelVertex(l+1,p2) );
					}
				}
			}
		}

		this.rchunkd = new Vector<Set<String>>();
		if( leftmostLevel.size() > 0 ){
			removeDeadEnds( leftmostLevel );
		}
		else{
			Set<LevelVertex> V = new HashSet<LevelVertex>(vertexSet());
			removeAllVertices(V);
		}
	}

	public Vector<Set<String>> getRChunkD() {
		return rchunkd;
	}
}
