package algorithms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import util.Debug;
import util.Settings;
import util.StringUtility;
import alphabets.Alphabet;

public class ContiguousCountingDAG {
	private PatternTrie patternDAG = null;
	private int n = 1;
	private List<Set<PatternTrie>> levels;
	private long nr_strings = 0;
	
	public PatternTrie asPatternTrie(){
		return this.patternDAG;
	}
	
	public List<Set<PatternTrie>> getLevels(){
		return this.levels;
	}

	public ContiguousCountingDAG( List<PatternTrie> rchunkd, int n, int r ){
		Debug.log("Calling leafify on level tries");	
		rchunkd.get(n-r).leafify();
		Debug.log("Building r-contiguous graph");
		this.patternDAG = RChunkPatterns.
			rContiguousGraphWithoutFailureLinks( rchunkd, n, r );
		Debug.log("Extracting levels");
		this.levels = patternDAG.levelOrder(n+1);
		this.n = n;
		Debug.log("Counting all strings");
		this.nr_strings = countStringsRev();
	}
	
	public long nrStrings(){
		return this.nr_strings;
	}

	/*public List<List<Long>> sequenceLogo(){
		List<List<Long>> R = new Vector<List<Long>>();
		Map<PatternTrie,Long> P = this.pathsStartingAt();

		int nletters = Alphabet.get().letters().size();

		for( int i = 0 ; i < levels.size()-1 ; i ++ ){
			List<Long> Ri = new Vector<Long>();
			for( int i = 0 ; i < nletters ; i ++ ){
				Ri.add(0);
			}
			R.add( Ri );
		}

		for( int i = levels.size()-1 ; i >= 0 ; i -- ){
			List<Long> Ri = R.get(i);
			for( PatternTrie pt: levels.get(i) ){
				if( pt.isComplete() ){
					for( int j = i ; j < levels.size()-1 ; j++ ){
						List<Long> Rj = R.get(j);
						for( int k = 0 ; k < nletters ; k ++ ){
							Rj.set( k, Rj.get(k) + P.get(pt) );
						}
					}
				} else {
					for( int j = 0 ; j < pt.children.length ; j++ ){
						if( pt.children[j] != null ){
							Ri.set( j, Ri.get(j) + P.get(pt.children[j]) );
						}
					}
				}
			}
		}

		return R;
	}*/

	private Map<PatternTrie,Long> pathsStartingAt(){
		Map<PatternTrie,Long> P = new HashMap<PatternTrie, Long>();
		for( int i = levels.size()-1 ; i >= 0 ; i -- ){
			Debug.log("entry nodes at level " + i + ": " + levels.get(i).size() );
			for( PatternTrie pt: levels.get(i) ){
				if( pt.isComplete() ){
					P.put( pt, (long) Math.pow(Alphabet.get().letters().size(),n-i) );
				} else {
					long c = 0L;
					for( int j = 0 ; j < pt.children.length ; j++ ){
						if( pt.children[j] != null ){
							c += P.get(pt.children[j]);
						}
					}
					P.put( pt, c );
				}
			}
		}
		return P;
	}

	private long countStringsRev(){
		Map<PatternTrie,Long> P = this.pathsStartingAt();
		return P.get( levels.get(0).iterator().next() );
	}
	
	private long countStrings(){
		Map<PatternTrie,Long> P = new HashMap<PatternTrie, Long>();
		Map<PatternTrie,Integer> leaves = new HashMap<PatternTrie, Integer>();
		P.put(patternDAG,1L);
		Debug.log("levels : " + levels.size() );		
		for( int current_level = 0 ; current_level < levels.size() ; current_level ++ ){
			Debug.log("entry nodes at level " + current_level + ": " + levels.get(current_level).size() );
			for( PatternTrie pt: levels.get(current_level) ){
				if( P.containsKey(pt) ){
					if( pt.isComplete() ){
						leaves.put(pt, current_level);
					} else {
						for( int i = 0 ; i < pt.children.length ; i++ ){
							if( pt.children[i] != null ){
								if( !P.containsKey(pt.children[i]) ){
									P.put(pt.children[i], P.get(pt));
								} else {
									P.put(pt.children[i], P.get(pt)+P.get(pt.children[i]));
								}
							}
						}
					}
				}
			}
		}
		long res = 0;
		Debug.log("collecting from " + leaves.keySet().size() + " leaves ");
		for( PatternTrie pt: leaves.keySet() ){
			int i = leaves.get(pt);
			long paths_ending_here = (long)
					(P.get(pt)*Math.pow(Alphabet.get().letters().size(),n-i));
			Debug.log(i + " : "+ P.get(pt));
			res += paths_ending_here;
		}
		return res;
	}
	
	private void addToVectorMatch( long[] v, long[] v_dst ){
		for( int j = 0; j < v.length-1 ; j ++ ){
			v_dst[j+1]+=v[j];
		}
		v_dst[v_dst.length-1]+=v[v.length-1];		
	}
	private void addToVectorMisMatch( long[] v, long[] v_dst ){
		for( int j = 0; j < v.length-1 ; j ++ ){
			v_dst[0]+=v[j];
		}
		v_dst[v_dst.length-1]+=v[v.length-1];		
	}

	
	public long countStringsThatMatch( String m, int r ){
		if( r <= 0 ){
			return this.nr_strings;
		}
				
		patternDAG.weights = new long[r+1];
		patternDAG.weights[0] = 1;
		int nl = Alphabet.get().letters().size();
		StringUtility.fillMcdCache(n, r, nl);

		Vector<Integer> empty_v = new Vector<Integer>();
		for( int j = 0 ; j <= r ; j++ ){ empty_v.add(0); }
		
		int cc=0;
		long ret = 0;		
		for( int i = 0 ; i <= n ; i ++ ){
			if( i<n ){ 
				cc = Alphabet.get().i(m.charAt(i));
				for( PatternTrie pt : levels.get(i+1) ){
					pt.weights = new long[r+1];
				}
			}
			for( PatternTrie pt : levels.get(i) ){
				if( pt.isComplete() ){
					/* this is a leaf */
					for( int j = 0 ; j <= r ; j ++ ){
						ret += pt.weights[j] * 
							StringUtility.mcdCache(n-i, j); 
							//StringUtility.matchingContiguousDetectors(n-i, r, j, nl);
					}
				} else {
					for( int j = 0 ; j < pt.children.length ; j++ ){
						if( pt.children[j] != null ){
							if( j == cc ){
								addToVectorMatch(pt.weights, pt.children[j].weights);
							} else {
								addToVectorMisMatch(pt.weights, pt.children[j].weights);
							}
						}
					}
				}
			}
		}
		return ret;
	}
}

/*
 * 
/*
 * this works for sure! 
 * */
/*for( int j = i; j < n ; j ++ ){
	addToVec(pt.weights, pt.weights, nl);
}
ret += pt.weights[r];
 private void addToVec( int[] v_src, int[] v_dst, int nl ){
int xn = (nl-1)*v_src[0];
v_dst[v_dst.length-1] = nl*v_src[v_src.length-1] + v_src[v_src.length-2];							
for( int k = v_src.length-2 ; k > 0 ; k -- ){
	xn += (nl-1)*v_src[k];
	v_dst[k] = v_src[k-1];
}
v_dst[0] = xn;
}*/
