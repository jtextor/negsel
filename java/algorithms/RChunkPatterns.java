/*
 * Created on 09.09.2009 by Johannes Textor
 * This Code is licensed under the BSD license:
 * http://www.opensource.org/licenses/bsd-license.php
 */
package algorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import util.Settings;
import alphabets.Alphabet;

public class RChunkPatterns { 
	
	public static Vector<PatternTrie> substringTries( String[] s, int n, int r ){
		if( s.length > 0 ){
			Settings.first_self_string = s[0];
		}
		Vector<PatternTrie> tries = new Vector<PatternTrie>();
		for( int i = 0 ; i <= n-r ; i++ ){
			tries.add(new PatternTrie());
			for (int j = 0; j < s.length; j++)
				tries.get(i).insert(s[j].substring(i, i + r));
		}
		return tries;
	}
	
	public static Vector<PatternTrie> substringTries( String filename, int n, int r, int o ) throws FileNotFoundException{
		File f = new File( filename );
		Scanner s = new Scanner( f );
		Vector<PatternTrie> tries = new Vector<PatternTrie>();
		for( int i = 0 ; i <= n-r ; i++ )
			tries.add(new PatternTrie());
		//int j = 0;
		while( s.hasNextLine() ){
			String l = s.nextLine().trim().substring(o);
			if(l.length() >= n ){
				if( Settings.first_self_string == null ){
					Settings.first_self_string = l;
				}
				for( int i = 0 ; i <= n-r ; i ++ ){
					tries.get(i).insert(l.substring(i,i+r));
				}
			}
			/*if( j++ % 100000 == 0 ){
				System.err.println(j);
			}*/
		}
		return tries;
	}
	
	/**
	 * Constructs the Vector of all r-chunk patterns for the given self-set and 
	 * alphabet.
	 * 
	 * An r-chunk pattern (s,i) is a tuple of a string s and an index i such
	 * that for all string s2 in self, s2.substring(i,i+r).startsWith(s) == false. 	 
	 * 
	 * @param self The self set, which is assumed to consist of only strings of fixed
	 * 	           length N.
	 * @param r    The parameter r.
	 * @param a    The underlying alphabet (without which the complement of self would be 
	 * 			   undefined)
	 * @return	   A vector (s_1,...,s_{n-r}) where each s_i is the set of all r-chunk
	 *             patterns for position i.
	 */
	public static Vector<PatternTrie> rChunkPatterns( String[] self, int n, int r ){
		Vector<PatternTrie> tries = substringTries(self, n, r);
		for( PatternTrie t : tries ){
			t.invert();
		}
		return tries;
	}
	
	/**
	 * Constructs r-chunk patterns from a self set that is given on the lines
	 * of a text file (each line is one string, which is assumed to have length n).
	 * 
	 *  
	 * @see {@link RChunkPatterns#rChunkPatterns(String[], int, int, Alphabet)}
	 */
	public static Vector<PatternTrie> rChunkPatterns( String filename, int n, int r ) throws FileNotFoundException{
		return rChunkPatterns(filename, n, r, 0);
	}
	public static Vector<PatternTrie> rChunkPatterns( String filename, int n, int r, int o ) throws FileNotFoundException{
		Vector<PatternTrie> tries = substringTries(filename, n, r, o);
		for( PatternTrie t : tries )
			t.invert();
		return tries;
	}
	
	/**
	 * Constructs the set of r-chunk patterns for the given self-set 
	 * (see there for the definition) with the additional restriction that 
	 * each r-chunk pattern (p,i) matches some r-contiguous detector d, 
	 * that is, d.substring(i,i+r).startsWith(p) == true.
	 * 
	 * An r-contiguous detector for a self set S \subseteq \Sigma^N is a 
	 * string d such that for no string s in S and no index i, 0 &lt;= i &lt; N-r, 
	 * the following holds: d.substring(i,i+r).equals(s.substring(i,i+r)).
	 * 
	 * @param rchunkd 
	 * 			   The prepared set of r-chunk detectors for the (no longer necessary) self set S
	 * @param n	   The length of all string in S. If some string in S is longer than n, the
	 *             tail is ingored; if some string is too short, a StringIndexOutOfBoundsExpception
	 *             will occur.
	 * @param r	   The parameter r.
	 * @param a    The alphabet \Sigma.  
	 * 
	 * @see {@link RChunkPatterns#rChunkPatterns(String[], int, int, Alphabet)}
	 */
	public static void filterPostfixes( List<PatternTrie> rchunkd, int n, int r ){ 
		// first pass from right to left: 
		// drop vertices with outdegree 0
		for( int i = n-r-1 ; i >= 0 ; i-- ){
			rchunkd.get(i).filterPostfix(rchunkd.get(i+1));
		}
		// now there is no vertex with outdegree 0
	}
	public static void filterPrefixes( List<PatternTrie> rchunkd, int n, int r ){ 
		// second pass from left to right:
		// drop vertices with indegree 0
		for( int i = 0 ; i < n-r ; i ++ ){
			rchunkd.get(i+1).filter(PatternTrie.postfixTrie(rchunkd.get(i))); 
		}
		// now we have only patterns that can be completed
		// to r-contiguous detectors
	}
	public static void rContiguousPatterns( List<PatternTrie> rchunkd, int n, int r ){
		filterPostfixes(rchunkd, n, r);
		filterPrefixes(rchunkd, n, r);
	}
	/*
	public static void rContiguousPatterns( Vector<PatternTrie> rchunkd, int n, int r, Alphabet a ){
		// first pass from right to left: 
		// drop vertices with outdegree 0
		//for( int i = 0 ; i < n-r ; i ++ ){
		//	System.out.print(" s("+i+")="+rchunkd.get(i).countLeaves());
		//}
		//System.out.println();		
		for( int i = n-r-1 ; i >= 0 ; i-- ){
			PatternTrie current = rchunkd.get(i);
			rchunkd.set(i,new PatternTrie(a));
			for( String s : current.content() ){
				if( rchunkd.get(i+1).matches(s.substring(1)) ){
					//System.out.println("ins: "+s);
					rchunkd.get(i).insert(s);
				}
			}
		}
		// now there is no vertex with outdegree 0
		//for( int i = 0 ; i < n-r ; i ++ ){
		//	System.out.print(" s("+i+")="+rchunkd.get(i).countLeaves());
		//}
		//System.out.println();		
		// second pass from left to right:
		// ignore vertices with indegree 0
		for( int i = 0 ; i < n-r ; i ++ ){ 
			PatternTrie current_post = new PatternTrie(a);
			for( String s : rchunkd.get(i).content() ){
				current_post.insert(s.substring(1));
			}
			PatternTrie next = rchunkd.get(i+1);
			rchunkd.set(i+1,new PatternTrie(a));
			for( String s : next.content() ){
				if( current_post.matches(s) )
					rchunkd.get(i+1).insert(s);
			}
		}
		//for( int i = 0 ; i < n-r ; i ++ ){
		//	System.out.print(" s("+i+")="+rchunkd.get(i).countLeaves());
		//}
		//System.out.println();
	}*/
	
	public static PatternTrie rContiguousGraphWithFailureLinks( List<PatternTrie> rchunkd, int n, int r ){
		filterPostfixes(rchunkd, n, r);
		filterPrefixes(rchunkd, n, r);
		for( int i = rchunkd.size()-2; i >= 0 ; i -- )
			rchunkd.get(i).augment(rchunkd.get(i+1),true);
		return rchunkd.get(0);
	}
	
	public static PatternTrie rContiguousGraphWithoutFailureLinks( List<PatternTrie> rchunkd, int n, int r ){
		filterPostfixes(rchunkd, n, r);
		filterPrefixes(rchunkd, n, r);
		for( int i = rchunkd.size()-2; i >= 0 ; i -- )
			rchunkd.get(i).augment(rchunkd.get(i+1),false);
		return rchunkd.get(0);
	}
	
	public static PatternTrie rContiguousGraph( List<PatternTrie> rchunkd, int n, int r ){
		return rContiguousGraphWithFailureLinks(rchunkd, n, r);
	}

}
