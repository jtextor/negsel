package visualization;
import java.awt.Rectangle;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.traverse.TopologicalOrderIterator;

import algorithms.PatternTrie;



public class PatternTrieView {
	JGraph jgraph;
	JGraphModelAdapter adapter;
	
	public PatternTrieView( PatternTrie st ){
		this( new PatternTrieJgraphWrapper(st), "PatternTrieView", null ); 
	}
	
	public PatternTrieView( PatternTrie st, WindowListener listener ){
		this( new PatternTrieJgraphWrapper(st), "PatternTrieView", listener ); 
	}
	
	public PatternTrieView( PatternTrie st, String title ){
		this( new PatternTrieJgraphWrapper(st), title, null ); 
	}
	
	public PatternTrieView( PatternTrieJgraphWrapper st, String title, WindowListener listener ) {
		// use a JGraphT listenable graph
		// create the view, then add data to the model
		adapter = st.getAdapter();
		jgraph = new JGraph(adapter);

		TopologicalOrderIterator<LevelGraphLabel, LabeledEdge> orderIterator = 
				new TopologicalOrderIterator<LevelGraphLabel, LabeledEdge>(
				st.getGraph());
		Map<Integer,Integer> current_y = new HashMap<Integer, Integer>();
		while (orderIterator.hasNext()) {
			LevelGraphLabel v = orderIterator.next();
			if( current_y.get(v.getLevel()) == null )
				current_y.put(v.getLevel(), 0);
			else
				current_y.put(v.getLevel(),current_y.get(v.getLevel())+100);
			positionVertexAt( v, (v.getLevel())*150, current_y.get(v.getLevel()) );		
		}
		
		/*
		// Hide all edge labels
		CellView[] cells = jgraph.getGraphLayoutCache().getCellViews();
		for( CellView cell : cells ){
			if( cell instanceof EdgeView ){
				EdgeView ev = (EdgeView) cell;
				org.jgraph.graph.DefaultEdge eval = (org.jgraph.graph.DefaultEdge) ev.getCell(); 
				eval.setUserObject( "" );
			}
		}*/
		
		JScrollPane scroller = new JScrollPane(jgraph);
		JFrame frame = new JFrame(title);
		frame.setSize(1200,800);
		frame.add(scroller);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		if( listener != null ){
			frame.addWindowListener(listener);
		}
		
	}
	
   private void positionVertexAt( Object vertex, int x, int y ) {
        DefaultGraphCell cell = adapter.getVertexCell( vertex );
        Map              attr = cell.getAttributes(  );
        Rectangle2D        b    = GraphConstants.getBounds( attr );

        GraphConstants.setBounds( attr, new Rectangle( x, y, (int)b.getWidth(), (int)b.getHeight() ) );

        Map<DefaultGraphCell,Map> cellAttr = new HashMap<DefaultGraphCell,Map>(  );
        cellAttr.put( cell, attr );
        adapter.edit( cellAttr, null, null, null );
    }
}
