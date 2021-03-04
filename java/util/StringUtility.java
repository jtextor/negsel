package util;

import alphabets.Alphabet;

public class StringUtility {
	/**
	 * implements the $r$-contiguous matching function
	 * 
	 * @param d detector
	 * @param x element
	 * @param r matching parameter
	 * @return true, if d and x are identical in at least r 
	 *         contiguous positions
	 */
	public static boolean contiguousMatch( String d, String x, int r ){
		int n = d.length(); if (x.length()<n) n = x.length();
		int partial_match_length = 0;
		for( int i = 0 ; i < n ; i ++ ){
			if( d.charAt(i) == x.charAt(i) ){
				partial_match_length ++;
			} else {
				partial_match_length = 0;
			}
			if( partial_match_length >= r ){
				return true;
			}
		}
		return false;
	}
	
	/** "increase" string given as array of indices into alphabet  
	 *  
	 * @param si the string given as array of indices into alphabet
	 * @return true if there was an overflow
	 */
	public static boolean incString( int[] si ){
		int nl = Alphabet.get().letters().size();
		int i = si.length-1;
		while( i >= 0 && si[i] == nl-1 ){
			si[i] = 0; i --;
		}
		if( i >= 0 ){
			si[i]++; 
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the number of contiguous detectors that match 
	 * a string of length n with parameter r.
	 * 
	 * @param n The string length.
	 * @param r The matching parameter.
	 * @param nl Size of the alphabet.
	 * 
	 * @return Number of contiguous detectors that match.
	 */
	public static double matchingContiguousDetectors( int n, int r, int prefixmatch, int nl ){
		return (double)Math.pow( nl, n ) - notMatchingContiguousDetectors( n, r, prefixmatch, nl );
	}
	
	/**
	 * Returns the number of contiguous detectors that do not match 
	 * a string of length n with parameter r.
	 * 
	 * @param n The string length.
	 * @param r The matching parameter.
	 * @param nl Size of the alphabet.
	 * 
	 * @return Number of contiguous detectors that match.
	 */
	public static double notMatchingContiguousDetectors( int n, int r, int prefixmatch, int nl ){
		long ret = 0; 
		for (int j = 1; j <= r-prefixmatch; j++) {
			ret += (long)((nl-1)*notMatchingContiguousDetectors(n-j, r, nl));
		}
		return ret;
	}
	
	public static double notMatchingContiguousDetectors( int n, int r, int nl ){
		if( n < -1 ){
			return 0;
		}
		if( n == -1 ){
			return 1.0/(nl-1);
		}
		if( n == 0 ){
			return 1;
		}
		long ret = 0; 
		for (int j = 1; j <= r; j++) {
			ret += (nl-1)*notMatchingContiguousDetectors(n-j, r,  nl);
		}
		return ret;
	}
	
	private static long[][] mcd_cache;
	
	public static long mcdCache( int n, int prefixmatch ){
		return mcd_cache[n][prefixmatch];
	}
	
	public static void fillMcdCache( int nmax, int r, int nl ){
		mcd_cache = new long[nmax+1][r+1];
		for( int n = 0; n <= nmax; n ++ ){
			for( int prefixmatch = 0 ; prefixmatch <= r ; prefixmatch ++ ){
				mcd_cache[n][prefixmatch] = Math.round(matchingContiguousDetectors(n, r, prefixmatch, nl));
			}
		}
	}
	
	public static String join(int[] ai, String delimiter) {
	    if (ai.length == 0) return "";
	    StringBuffer buffer = new StringBuffer(ai.length*2);
	    buffer.append(ai[0]);
	    for( int i = 1 ; i < ai.length ; i ++ ){
	    	buffer.append( delimiter );
	    	buffer.append(ai[i]);	    	
	    }
	    return buffer.toString();
	}

}
