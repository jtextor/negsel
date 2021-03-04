package alphabets;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

/*
 * Created on 01.09.2009 by Johannes Textor
 * This Code is licensed under the BSD license:
 * http://www.opensource.org/licenses/bsd-license.php
 */

public class Alphabet {
	private static Alphabet a;
	
	private static boolean warned = false; 
	
	public static Alphabet get(){
		return a;
	}
	
	public static void set(Alphabet _a){
		a = _a;
	}
	
	protected List<Character> letters;
	
	/** 
	 * Construct alphabet from all letters that are contained in the
	 * text file f (not counting spaces) 
	 * 
	 * @param f - the input file
	 * @throws  
	 */
	public Alphabet( File f ){
		Scanner scan;
		try {
			scan = new Scanner(f);
		} catch (FileNotFoundException e) {
			return;
		}
		Set<Character> cs = new HashSet<Character>();
		while (scan.hasNextLine()) {
			String l = scan.nextLine().trim();
			for( char c : l.toCharArray() ){
				cs.add( c );
			}
		}
		letters = new Vector<Character>();		
		letters.addAll(cs);
		Collections.sort(letters);
		scan.close();
	}
	
	public Alphabet(){
		letters = new Vector<Character>();
	}
	
	public char wildcard(){
		return '*';
	}

	public List<Character> letters(){
		return letters;
	}
	
	public char c( int i ){
		return letters().get(i);
	}
	
	public int i( char c ){
		return Collections.binarySearch(letters(), c);
	}
	
	/**
	 * Convert input string to sequence of integers,
	 * representing the letter's indices, for faster
	 * processing in tries. 
	 * 
	 */
	
	public boolean translate( String s, int[] si ){
		int i = 0;
		for( char c : s.toCharArray() ){
			si[i] = i(c);
			if( si[i] < 0 ){
				warn(c);
				return false;
			}
			i++;
		}
		return true;
	}

   /**
    * Verify whether all the strings' letters exist
    * in the alphabet.
    *
    */ 

	public boolean isOk( String s ){
		for( char c : s.toCharArray() ){
			if( i(c) < 0 ){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Convert integer sequence representation to 
	 * a string
	 * 
	 * @param si the string integer sequence representation
	 * @return
	 */
	
	public String translate( int[] si ){
		String r = "";
		for( int i = 0 ; i < si.length ; i ++ ){
			r += c(si[i]);
		}
		return r;
	}
	
	public void warn(char c){
		if( !warned ){
			System.err.println("Warning: non-alphabet symbol '"+c+"' encountered.");
			System.err.println("All strings containing non-alphabet symbols will be silently ignored.");
		}
		warned = true;
	}
}
