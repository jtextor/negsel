package algorithms;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import alphabets.Alphabet;

/*
 * Created on 09.09.2009 by Johannes Textor
 * This Code is licensed under the BSD license:
 * http://www.opensource.org/licenses/bsd-license.php
 */

public class PatternTrie {
	public PatternTrie[] children = null;
	private boolean complete = false;
	public long[] weights = null;
	
	public String extra_label = "";
	
	//protected Map<Integer,PatternTrie> children = null;
	//protected Map<Integer,Integer> weights = null;
	
	public PatternTrie(){
		this( false );
	}
	
	public PatternTrie( boolean complete ){
		children = new PatternTrie[Alphabet.get().letters().size()];
		this.complete = complete;
	}
		
	public PatternTrie(PatternTrie other){
		if( other.weights != null ){
			throw new IllegalArgumentException("Copy constructor only works for trees, not DAGs!");
		}
		children = new PatternTrie[Alphabet.get().letters().size()];
		complete = other.complete;
		for( int i = 0 ; i < children.length ; i ++ ){
			if( other.children[i] != null ){
				if( other.children[i] == COMPLETE_TRIE )
					children[i] = COMPLETE_TRIE;
				else
					children[i] = new PatternTrie( other.children[i] );
			}
		}
	}
	
	public long count( int depth ){
		if( this.isComplete() ){
			return (long) Math.pow(Alphabet.get().letters().size(), depth);
		}
		long r = 0;
		for( int i = 0 ; i < children.length ; i ++ ){
			if( children[i] != null ){
				r += children[i].count( depth - 1 );
			}
		}
		return r;
	}
	
	public long getWeight( int c ){
		if( weights == null )
			return 1;
		else
			return weights[c];
	}
	
	/** 
	 * Returns true iff this pattern trie contains 
	 * a pattern that matches the string s.
	 * 
	 * @param s The String to match
	 * @return true iff s matches some pattern in this trie
	 */ 
	public int matches( String s, int r ){
		int[] si = new int[s.length()];
		if( Alphabet.get().translate(s, si) ){
			PatternTrie cursor = this;
			int max_partial_match = 0;
			int partial_match = 0;
			int i = 0;
			while( true ){
				if( cursor.isComplete() ){
					partial_match += s.length()-i;
					max_partial_match = Math.max( partial_match, max_partial_match );
					break;
				}
				if( i == s.length() || cursor.children[si[i]] == null )
					break;
				partial_match += cursor.getWeight(si[i]);
				max_partial_match = Math.max( partial_match, max_partial_match );
				cursor = cursor.children[si[i]];
				i++;
			}
			if( max_partial_match < r ) return 0;
			else return max_partial_match;
		} else {
			return -1;
		}
	}

	/**
	 * Inserts a String into this pattern trie
	 * 
	 * @param s The String to insert
	 */
	public void insert( String s ){
		int[] si = new int[s.length()];
		if( s.length() == 0 ){
			throw new IllegalArgumentException("Empty string can't be inserted");
		}
		if( Alphabet.get().translate(s, si) ){
			insert(si,0);
		}
	}
	public void insert( int[] s, int i ){
		if( this.isComplete() ) return;

		if( s.length == i+1 ){
			children[s[i]] = COMPLETE_TRIE;
		} else {
			if( children[s[i]] == null )
				children[s[i]] = new PatternTrie();
			children[s[i]].insert(s,i+1);
		}
	}
	
	public void insert( PatternTrie other ){
		if( this.isComplete() ) return;
		for( int i = 0 ; i < Alphabet.get().letters().size(); i ++ ){
			if( other.children[i] != null ){
				if( other.children[i].isComplete() ){
					children[i] = COMPLETE_TRIE;
				}
				else {
					if( children[i] == null )
						children[i] = new PatternTrie();
					children[i].insert(other.children[i]);
				}
			}
		}
	}
	
	/**
	 * Returns true if there are no missing edges in this trie.
	 * 
	 * @return true iff this trie contains a prefix for every string
	 * 
	 */
	public boolean isComplete(){
		if( this == COMPLETE_TRIE ) return true;
		if( this.complete ) return true;
		for( int i = 0 ; i < children.length ; i ++ ){
			if( children[i] == null ){
				return false;
			} else if( !children[i].isComplete() ){
				return false;
			}
		}
		return true;
	}
	
	/** 
	 * Compress this trie by removing complete subtrees
	 * 
	 * @return true if it turned out that this trie is a
	 * 		   complete one, and thus can be replaced by
	 * 		   the singleton complete trie at the parent level
	 */
	public boolean compress(){
		if( this == COMPLETE_TRIE || this.complete ) return true;		
		boolean is_complete = true;
		for( int i = 0 ; i < children.length ; i ++ ){
			if( children[i] == null ){
				is_complete = false;
			}
			else{
				if( children[i].compress() ){
					children[i] = COMPLETE_TRIE;
				} else {
					is_complete = false;
				}
			}
		}
		return is_complete;
	}
	
	/**
	 * Invert this trie by generating all non-existing edges
	 * and removing the existing ones.
	 * 
	 * Returns true if the inversion resulted in a complete trie.
	 */
	public boolean invert(){
		int empty_children = 0;
		for( int i = 0 ; i < children.length ; i ++ ){
			if( children[i] != null ){
				if( children[i].isComplete() ){
					children[i] = null;
					empty_children ++;
				} else {
					children[i].invert();
				}
			}
			else{
				children[i] = COMPLETE_TRIE;
			}
		}
		return empty_children == children.length;
	}
	
	/**
	 * Remove from this trie every string s that is not a prefix
	 * of some string in the given trie.
	 * 
	 * Method requires that the trie "other" is not empty. 
	 * 
	 * @param other
	 * @return false if at least one string is left in this trie after
	 *              the operation
	 */
	public boolean filter( PatternTrie other ){
		if( this.isComplete() || other.isComplete() ){
			return true;
		}
		boolean has_nonempty_child = false;
		/*
		 * Iterate through all child nodes and see whether they are
		 * present in the other trie. If not, delete them in this
		 * trie, too.
		 * 
		 * Then recursively filter all remaining children. This may
		 * cause the removal of paths deeper down in the tree, such
		 * that all child nodes are set to zero. If this occurs,
		 * return "false". 
		 */
		for( int i = 0 ; i < children.length ; i ++ ){
			if( other.children[i] == null ){
				children[i] = null;
			} else{
				if( children[i] != null ){
					if( children[i].filter(other.children[i]) )
						has_nonempty_child = true;
					else
						children[i] = null;
				} 
			}
		}
		return has_nonempty_child;
	}
	
	/**
	 * Remove all strings from this trie every string s
	 * for which s.substring(1) is not a prefix of some 
	 * string in the given trie. 
	 * 
	 * @param postfixes
	 */
	public void filterPostfix( PatternTrie postfixes ){
		if( this.isComplete() ) return;
		for( int i = 0 ; i < children.length ; i ++ ){
			if( children[i] != null ){
				if( !children[i].filter(postfixes) )
					children[i] = null;
			}
		}
	}
	
	private void linkLeaves( PatternTrie other, long sum_on_this_path, 
			long sum_on_other_path, boolean insert_failure_links ){
		if( this == COMPLETE_TRIE ){
			throw new IllegalArgumentException("Can't link leaves for the complete trie!");
		}
		weights = new long[children.length];
		for( int i = 0 ; i < children.length ; i ++ ){
			if( children[i] != null ){
				weights[i] = 1;
				if( children[i].isComplete() ){
					// this is a leaf
					children[i] = other.children[i];
				}
				else{
					// this is an existing edge
					children[i].linkLeaves(other.children[i],sum_on_this_path+1,
							sum_on_other_path+other.getWeight(i),insert_failure_links);
				}
			} else {
				// this is a missing edge -> failure link
				if( insert_failure_links && other.children[i] != null ){
					children[i] = other.children[i];
					weights[i] = sum_on_other_path+other.getWeight(i)-sum_on_this_path;
				}
			}
		}
	}

	public void augment( PatternTrie other )
	{
		this.augment( other, true );
	}
	
	public void augment( PatternTrie other, 
			boolean insert_failure_links ){
		weights = new long[children.length];
		for( int i = 0 ; i < children.length ; i ++ ){
			if( children[i] != null ){
				weights[i] = 1;
				if( children[i].isComplete() ){
					// this edge leads to a leaf on the first level
					children[i] = other;
				}
				else{
					// this edge leads to a subtree
					children[i].linkLeaves(other,1,0,insert_failure_links);
				}
			} else {
				// this edge does not exist
				if( insert_failure_links ){
					weights[i] = 0;
					children[i] = other;
				}
			}
		}
	}
	
	public List<String> content(){
		List<String> r = new ArrayList<String>();
		if( this == COMPLETE_TRIE || this.complete ){
			r.add("");
			return r;
		}
		for( int i = 0 ; i < children.length ; i ++ ){
			if( children[i] != null ){
				for( String s : children[i].content() )
					r.add( Alphabet.get().letters().get(i)+s );
			}
		}
		if(r.size() == 0)
			r.add("Eps");
		return r;
	}
	
	public String toString(){
		return content().toString();
	}
	
	/**
	 * Return a Trie that contains all postfixes starting at the
	 * second letter from all strings contained in the given Trie.
	 * 
	 * @param t The trie to scan for postfixes.
	 * @return The postfix trie.
	 */
	public static PatternTrie postfixTrie( PatternTrie t ){
		PatternTrie tr = new PatternTrie();
		for( PatternTrie tc : t.children ){
			if( tc != null ){
				if( tc.isComplete() ) return COMPLETE_TRIE;
				else tr.insert(tc);
			}
		}
		tr.compress();
		return tr;
	}

	public Map<Character, PatternTrie> getChildren() {
		Map<Character,PatternTrie> r = new HashMap<Character, PatternTrie>();
		if( children != null ){
			for( int i = 0 ; i < children.length ; i ++ )
				if( children[i] != null ) r.put(Alphabet.get().letters().get(i),children[i]);
		}
		return r;
	}
	
	/**
	 * This method turns all leaves that are equal to the
	 * internal constant "COMPLETE_TRIE" into proper objects
	 * that are no longer all equal to each other. 
	 * 
	 * This is required for methods that need to count the
	 * paths in the graph to work properly. It should not 
	 * be called unless needed since of course the new graph
	 * will require more space.  
	 */
	public void leafify(){
		this.leafify( new HashSet<PatternTrie>() );
	}
	private void leafify( Set<PatternTrie> visited ){
		for( int i = 0 ; i < children.length ; i++ ){
			if( children[i] != null ){
				if( children[i] == COMPLETE_TRIE ){
					children[i] = new PatternTrie(true);
				} else {
					if( !visited.contains(children[i]) ){
						visited.add(children[i]);
						children[i].leafify( visited );
					}
				}
			}
		}
	}
	
	/**
	 * Iterates in breadth-first order along the
	 * levels of this graph and returns a list making
	 * the vertices on each level directly accessible
	 * 
	 * @param n The size of the returned list (there may 
	 *           be fewer nonempty levels if the rightmost
	 *           underlying prefix tree is saturated) 
	 * @return
	 */
	public List<Set<PatternTrie>> levelOrder( int n ){
		List<Set<PatternTrie>> levels = new Vector<Set<PatternTrie>>();
		levels.add(new HashSet<PatternTrie>());
		levels.get(0).add(this);
		for( int current_level = 0 ; current_level < n-1 ; current_level ++ ){
			levels.add(new HashSet<PatternTrie>());
			for( PatternTrie pt: levels.get(current_level) ){
				for( int i = 0 ; i < pt.children.length ; i++ ){
					if( pt.children[i] != null &&
							!levels.get(current_level+1).contains(pt.children[i]) ){
						levels.get(current_level+1).add(pt.children[i]);
					}
				}
			}
		}
		return levels;
	}
	
	/******** 
	 * NEW METHODS FROM THE PAPER
	 */
	private void insertContiguousLinkLeaves( PatternTrie other ){
		if( this == COMPLETE_TRIE ){
			throw new IllegalArgumentException("Can't link leaves for the complete trie!");
		} 
		for( int i = 0 ; i < children.length ; i ++ ){
			if( other.children[i] != null ){
				if( children[i] == null )
					children[i] = other.children[i];
				else
					if( !children[i].isComplete() )
						children[i].insertContiguousLinkLeaves(other.children[i]); 
			}
		}
	}
	
	public void insertContiguousLinks( PatternTrie other ){
		for( int i = 0 ; i < children.length ; i ++ ){
			if( children[i] == null )
				children[i] = other;
			else
				children[i].insertContiguousLinkLeaves( other );
		}
	}

	public PatternTrie destinationNode( String s ){
		if( s.length() == 0 ) return this;
		int i = Alphabet.get().i(s.charAt(0));
		if( children[i] == null ) return null;
		return children[i].destinationNode(s.substring(1));
	}
	
	private static PatternTrie COMPLETE_TRIE = new PatternTrie( true );
}
