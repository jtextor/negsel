package visualization;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import util.Settings;
import algorithms.ContiguousCountingDAG;
import algorithms.PatternTrie;
import algorithms.RChunkPatterns;
import alphabets.Alphabet;
import alphabets.BinaryAlphabet;
import alphabets.BinaryLetterAlphabet;
import alphabets.TernaryAlphabet;


public class VisualizationTests implements WindowListener {
	PatternTrie pt;
	ContiguousCountingDAG pdag;
	boolean closed = false;
	
	@Before
	public void setUp() {
		Alphabet.set(new BinaryAlphabet());
		pt = new PatternTrie();
		Settings.DEBUG = true;
	}

	@Test
	public void contVizTest5(){
		Alphabet.set(new BinaryLetterAlphabet());
		String[] self = { "aaaaa" };
		int n=5,r=3;
		List<PatternTrie> rchunkd = RChunkPatterns.rChunkPatterns(self, n, r);		
		ContiguousCountingDAG matcher = new ContiguousCountingDAG( rchunkd , n, r );
		System.out.println("starting to wait");
		System.out.println(matcher.nrStrings());
		System.out.println(matcher.countStringsThatMatch("aaaaa",1));
		new PatternTrieView( matcher.asPatternTrie(), this );		
		while( !closed ){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		}
		// never arrives here because of EXIT_ON_CLOSE handler
		System.out.println("finished");
	}
	
	@Test
	public void contVizTest4(){
		Alphabet.set(new TernaryAlphabet());
		String[] self = { "abcab" };
		int n=5,r=3;
		List<PatternTrie> rchunkd = RChunkPatterns.rChunkPatterns(self, n, r);		
		ContiguousCountingDAG matcher = new ContiguousCountingDAG( rchunkd , n, r );
		System.out.println("starting to wait");
		System.out.println(matcher.nrStrings());
		System.out.println(matcher.countStringsThatMatch("aaaaa",r));
		new PatternTrieView( matcher.asPatternTrie(), this );		
		while( !closed ){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		}
		// never arrives here because of EXIT_ON_CLOSE handler
		System.out.println("finished");
	}
	
	@Test
	public void contVizTest3(){
		Alphabet.set(new BinaryLetterAlphabet());
		String[] self = { "ababb", "ababa", "abbab" };
		int n=5,r=3;
		List<PatternTrie> rchunkd = RChunkPatterns.rChunkPatterns(self, n, r);		
		ContiguousCountingDAG matcher = new ContiguousCountingDAG( rchunkd , n, r );
		System.out.println("starting to wait");
		System.out.println(matcher.nrStrings());
		System.out.println(matcher.countStringsThatMatch("abaab",r));
		new PatternTrieView( matcher.asPatternTrie(), this );		
		while( !closed ){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		}
		// never arrives here because of EXIT_ON_CLOSE handler
		System.out.println("finished");
	}
	
	@Test
	public void contVizTest(){
		//String[] self = { "001100","101101","100110","000110","111010","010010" };
		//String[] self = { "abbbb", "aabbb", "baaaa", "baaab", "babba", "baaba", "bbbbb" };
		int n = 5;
		int r = 3;
		
		String[] self = { "01111", "00111", "10000", "10001", "10010", "10110", "11111" };
		
		List<PatternTrie> rchunkd = RChunkPatterns.rChunkPatterns(self, n, r);		
		ContiguousCountingDAG matcher = new ContiguousCountingDAG( rchunkd , n, r );

		/*String m = "11100";
		System.out.println(m+": "+matcher.countStringsThatMatch(m,r)+"/"+
				matcher.nrStrings());*/
		System.out.println(matcher.nrStrings());
		
		new PatternTrieView( matcher.asPatternTrie(), this );

		/*for( String m : new String[] {"00000","00001","00010","00011",
				"00100","00101","00110","00111","01000","01001","01010",
				"01011","01100","01101","01110","01111",
				"10000","10001","10010","10011","10100",
				"10101","10110","10111","11000","11001","11010",
				"11011","11100","11101","11110","11111"} )
			System.out.println(m+": "+matcher.countStringsThatMatch(m,r)+"/"+
				matcher.nrStrings());*/		
		/*matcher = RChunkPatterns.rContiguousGraph(RChunkPatterns.rChunkPatterns(
				new String[] { "01111", "00111", "10000", "10001", "10010",
						"10110", "11111" }, n, r), n, r);*/
		
		// new PatternTrieView( matcher.asPatternTrie(), this );
		
		System.out.println("starting to wait");
		while( !closed ){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		}
		// never arrives here because of EXIT_ON_CLOSE handler
		System.out.println("finished");
	}
	
	@Test
	public void contVizTest2(){
		Alphabet.set(new BinaryAlphabet());
		String[] self = { "01011", "01010", "01101" };
		int n=5,r=3;
		List<PatternTrie> rchunkd = RChunkPatterns.rChunkPatterns(self, n, r);		
		ContiguousCountingDAG matcher = new ContiguousCountingDAG( rchunkd , n, r );		
		System.out.println("starting to wait");
		System.out.println(matcher.nrStrings());		
		System.out.println(matcher.countStringsThatMatch("00000",r));
		new PatternTrieView( matcher.asPatternTrie(), this );		
		while( !closed ){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		}
		// never arrives here because of EXIT_ON_CLOSE handler
		System.out.println("finished");
	}
	
	public static void main(String[] args) {
		org.junit.runner.JUnitCore.main("tests.VisualizationTests");
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
		this.closed = true;
	}

	public void windowClosing(WindowEvent e) {
		this.closed = true;
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub		
	}
}
