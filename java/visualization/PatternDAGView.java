package visualization;


import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;



/*
 * Created on 27.08.2009 by Johannes Textor
 * This Code is licensed under the BSD license:
 * http://www.opensource.org/licenses/bsd-license.php
 */

public class PatternDAGView {
	JGraph jgraph;
	JGraphModelAdapter<LevelVertex, DefaultEdge> adapter;
	
	public PatternDAGView( PatternDAG g ) {
		// use a JGraphT listenable graph
		// create the view, then add data to the model
		System.out.println("constructing adapter ...");
		adapter = new JGraphModelAdapter<LevelVertex,DefaultEdge>( g );
		System.out.println("constructing jgraph ...");
		jgraph = new JGraph(adapter);

		int current_level = 50;
		int current_y = 0;
		System.out.println("# vertices: "+g.vertexSet().size());
		for ( LevelVertex v : g.vertexSet() ) {
			if( current_level == v.getLevel() )
				current_y += 100;
			else
				current_y = 50;
			current_level = v.getLevel();
			positionVertexAt( v, current_level*150, current_y );		
		}
		
		CellView[] cells = jgraph.getGraphLayoutCache().getCellViews();
		for( CellView cell : cells ){
			if( cell instanceof EdgeView ){
				EdgeView ev = (EdgeView) cell;
				org.jgraph.graph.DefaultEdge eval = (org.jgraph.graph.DefaultEdge) ev.getCell(); 
				eval.setUserObject( "" );
			}
		}
		
		JScrollPane scroller = new JScrollPane(jgraph);
		JFrame frame = new JFrame("r-contiguous graph");
		frame.setSize(1200,800);
		frame.add(scroller);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
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
