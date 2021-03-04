/*
 * Created on 08.10.2009 by Johannes Textor
 * This Code is licensed under the BSD license:
 * http://www.opensource.org/licenses/bsd-license.php
 */
package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import util.Settings;
import util.StringUtility;
import algorithms.ContiguousCountingDAG;
import algorithms.PatternTrie;
import algorithms.RChunkPatterns;
import alphabets.Alphabet;
import alphabets.AminoAcidAlphabet;
import alphabets.BinaryAlphabet;
import alphabets.BinaryLetterAlphabet;
import alphabets.TernaryAlphabet;

public class TrieTests {
	PatternTrie pt;
	ContiguousCountingDAG pdag;

	private static String[] spectSelf = {
		"0001000110001100000000",
		"0011000110001100000001",
		"1010100101001000000000",
		"0000000000100000000111",
		"0000000100001011000000",
		"0001000010001101000101",
		"1011000101011000000011",
		"0010000100001000000001",
		"0010001100001010000011",
		"0100001100100000000000",
		"1100101001111001111101",
		"1100111011110100101100",
		"1000100001001000000011",
		"0100001000010001010011",
		"1011001110111111011011",
		"0110011100011001110111",
		"0011000110001101000010",
		"0000001000000000000001",
		"1010101101011000100110",
		"1000100001001000000100",
		"0000001000111000000011",
		"1000110100100000001100",
		"1100111001110000001000",
		"0000000100001000000000",
		"0000010000100000101000",
		"0000001000010001000000",
		"0001001010010100000010",
		"0000000000000000000100",
		"0000000000000000000001",
		"1011100001001101000111",
		"1100111001110000101001",
		"0111001110111001110011",
		"1011100111001110000010",
		"1111110000111101111101",
		"1110101111011101000111",
		"1001000001100000000000",
		"1000100001000000001000",
		"1011000111001111001100"};
	
	@Before
	public void setUp() {
		Alphabet.set(new BinaryAlphabet());
		Settings.DEBUG = true;
		pt = new PatternTrie();
	}

	@Test
	public void testCompress() {
		pt.insert("00");
		pt.compress();
		assertTrue(pt.content().toString().equals("[00]"));
	}

	@Test
	public void testChunk(){
		String[] self = spectSelf;
		int n = self[0].length();
		int r = 2;
		List<PatternTrie> chunks = RChunkPatterns.rChunkPatterns(spectSelf, n, r);
		for( PatternTrie pt : chunks ){
			System.out.println(pt);
		}
	}
	
	@Test
	public void testInvert1() {
		pt.insert("110");
		pt.insert("011");
		pt.insert("101");
		pt.insert("001");
		System.out.println(pt.content().toString());
		pt.invert();
		System.out.println(pt.content().toString());
		assertTrue(pt.content().toString().equals("[000, 010, 100, 111]"));
	}

	@Test
	public void testInvert2() {
		pt.insert("10");
		pt.insert("11");
		pt.invert();
		assertTrue(pt.content().toString().equals("[0]"));
	}

	@Test
	public void testInvert3() {
		pt.insert("00");
		pt.insert("01");
		pt.insert("10");
		pt.insert("11");
		pt.invert();
		assertTrue(pt.content().toString().equals("[Eps]"));
		pt.invert();
		System.out.println(pt.content());
		assertTrue(pt.content().toString().equals("[0, 1]"));
		pt.invert();
		assertTrue(pt.content().toString().equals("[Eps]"));
	}

	@Test
	public void testFilterPostfix2() {
		pt.insert("110");
		pt.insert("011");
		pt.insert("101");
		pt.insert("001");
		pt.invert();

		PatternTrie pt2 = new PatternTrie();
		pt2.insert("111");
		pt2.insert("011");
		pt2.insert("00");

		pt.filterPostfix(pt2);

		assertEquals(pt.content().toString(), "[000, 100, 111]");
	}

	@Test
	public void rChunkTest() {
		String[] self = { "001100", "101101", "100110", "000110", "111010",
				"010010" };
		int n = self[0].length();
		int r = 3;
		Vector<PatternTrie> rchunkd = RChunkPatterns.rChunkPatterns(self, n, r);
		assertEquals(rchunkd.toString(),
				"[[011, 110], [000, 010, 101, 111], [000, 010, 100, 111], [00, 011, 111]]");
	}

	@Test
	public void rContTest() {
		String[] self = { "001100", "101101", "100110", "000110", "111010",
				"010010" };
		int n = self[0].length();
		int r = 3;
		Vector<PatternTrie> rchunkd = RChunkPatterns.rChunkPatterns(self, n, r);
		RChunkPatterns.filterPostfixes(rchunkd, n, r);
		assertEquals(rchunkd.toString(),
				"[[011], [000, 010, 111], [000, 100, 111], [00, 011, 111]]");
		RChunkPatterns.filterPrefixes(rchunkd, n, r);
		assertEquals(rchunkd.toString(), "[[011], [111], [111], [111]]");
	}

	@Test
	public void matchTest() {
		String[] self = { "001100", "101101", "100110", "000110", "111010",
				"010010" };
		int n = self[0].length();
		int r = 3;
		PatternTrie matcher = RChunkPatterns.rContiguousGraph(RChunkPatterns
				.rChunkPatterns(self, n, r), n, r);
		// only 1 detector - 011111
		assertEquals(0, matcher.matches("100000", r));
		assertEquals(0, matcher.matches("000000", r));
		assertEquals(0, matcher.matches("010000", r));
		assertEquals(3, matcher.matches("011000", r));
		assertEquals(4, matcher.matches("011100", r));
		assertEquals(5, matcher.matches("011110", r));
		assertEquals(6, matcher.matches("011111", r));
		assertEquals(3, matcher.matches("010111", r));
		assertEquals(5, matcher.matches("111111", r));
	}

	@Test
	public void noDetectorsTest() {
		String[] self = { "001100", "101101", "100110", "000110", "111010",
				"010010", "011111" };
		int n = self[0].length();
		int r = 3;
		PatternTrie matcher = RChunkPatterns.rContiguousGraph(RChunkPatterns
				.rChunkPatterns(self, n, r), n, r);
		assertEquals(
				"[000Eps, 001Eps, 010Eps, 011Eps, 100Eps, 101Eps, 110Eps, 111Eps]",
				matcher.content().toString());
	}

	@Test
	public void countDetectorsTest1() {
		int n = 6, r = 3;
		pdag = new ContiguousCountingDAG(RChunkPatterns
				.rChunkPatterns(new String[] { "001100", "101101", "100110",
						"000110", "111010", "010010", "011111" }, n, r), n, r);
		assertEquals(0, pdag.nrStrings());
	}

	@Test
	public void countDetectorsTest2() {
		int n = 5, r = 3;
		pdag = new ContiguousCountingDAG(RChunkPatterns.rChunkPatterns(
				new String[] { "01111", "00111", "10000", "10001", "10010",
						"10110", "11111" }, n, r), n, r);
		assertEquals(2, pdag.nrStrings());
	}

	@Test
	public void countMatchingTest1() {
		int n = 5, r = 3;
		pdag = new ContiguousCountingDAG(RChunkPatterns.rChunkPatterns(
				new String[] { "01011", "01010", "01101" }, n, r), n, r);
		assertEquals(14,pdag.nrStrings());
		assertEquals(4, pdag.countStringsThatMatch("11100", r));
	}

	@Test
	public void countMatchingTest2() {
		int n = 5, r = 1;
		pdag = new ContiguousCountingDAG(RChunkPatterns.rChunkPatterns(
				new String[] { "01010", "10101" }, n, r), n, r);
		assertEquals(0, pdag.countStringsThatMatch("11100", r));
	}

	@Test
	public void countMatchingTest3() {
		int n = 3, r = 1;
		Alphabet.set(new AminoAcidAlphabet());
		pdag = new ContiguousCountingDAG(RChunkPatterns
				.rChunkPatterns(new String[] { "AAA", "AAK", "AAL", "AAP",
						"AAS", "AAT", "AAV", "AEE", "AFV", "AGP", "AHD", "ALA",
						"ALL", "ANE", "APS", "AQR", "ATA", "ATG", "ATS", "AVA",
						"AWG", "AYL", "CHP", "CIC", "CSV", "CWL", "CWM", "DDK",
						"DIL", "DLL", "DVA", "EAE", "EAI", "EAV", "EGE", "EPT",
						"ETT", "EVT", "FAE", "FEF", "FFT", "FHR", "FKF", "FLV",
						"FPP", "FPS", "FYY", "GAG", "GAL", "GEG", "GEQ", "GFH",
						"GGT", "GLS", "GNL", "GPS", "GSH", "GSP", "GTA", "GVE",
						"GYD", "HAS", "HNS", "IAA", "IAG", "IAL", "IEV", "IFT",
						"ILF", "ILK", "IPA", "IPV", "IQE", "ISE", "ITF", "ITP",
						"IVY", "IYT", "KAL", "KQL", "KYK", "LAA", "LAC", "LAF",
						"LAH", "LAR", "LAS", "LAT", "LAV", "LDI", "LEN", "LEQ",
						"LFG", "LFS", "LFT", "LHN", "LIH", "LIN", "LIR", "LIV",
						"LKA", "LLA", "LLL", "LLS", "LLT", "LLV", "LPT", "LQA",
						"LRL", "LSH", "LSK", "LSP", "LSY", "LTA", "LTS", "LVE",
						"LWG", "LWL", "LYK", "MAA", "MFL", "MFS", "MLH", "MML",
						"MRT", "MTS", "MVR", "MVS", "NAQ", "NGA", "NII", "NLS",
						"NLY", "NMP", "NVQ", "NVS", "NWT", "PAI", "PAP", "PAQ",
						"PEG", "PFP", "PGS", "PLS", "PPA", "PPE", "PPL", "PPP",
						"PPT", "PTG", "PTL", "PVA", "PYV", "QAA", "QAC", "QAL",
						"QAQ", "QLE", "QNP", "QPA", "QPF", "QPM", "QPS", "QQM",
						"QQT", "QRL", "QTQ", "QWA", "QYS", "RAV", "RLS", "RNP",
						"RSP", "RVE", "RWL", "SAK", "SAM", "SAP", "SAS", "SCT",
						"SDV", "SFF", "SHR", "SLL", "SLM", "SPS", "SPY", "SSG",
						"SVA", "TAA", "TAP", "TAQ", "TAT", "TDQ", "TFS", "TGF",
						"THA", "TII", "TKE" }, n, r), n, r);
		assertEquals(0,pdag.countStringsThatMatch("AVL",r));
	}
	
	@Test
	public void countMatchingTest4() {
		int n = 5, r = 3;
		Alphabet.set(new BinaryLetterAlphabet());
		pdag = new ContiguousCountingDAG( RChunkPatterns.rChunkPatterns(
				new String[] { "ababb", "ababa", "abbab" }, n, r), n, r );
		Settings.DEBUG = false;
		
		assertEquals(14, pdag.nrStrings() );
		
		Settings.DEBUG = true;
		assertEquals(4,pdag.countStringsThatMatch("bbbaa",r));
		
		Settings.DEBUG = true;
		assertEquals(6,pdag.countStringsThatMatch("aabbb",r));

		System.out.println(pdag.countStringsThatMatch("bbbaa",r)+" "
				+pdag.countStringsThatMatch("aabbb",r)+" "
				+pdag.countStringsThatMatch("ababa",r));

		System.out.println(pdag.countStringsThatMatch("bbbaa",r-1)+" "
				+pdag.countStringsThatMatch("aabbb",r-1)+" "
				+pdag.countStringsThatMatch("ababa",r-1));
		
		Alphabet.set(new TernaryAlphabet());
		String[] self = { "ababb", "ababa", "abbab" };
		pdag = new ContiguousCountingDAG( RChunkPatterns.rChunkPatterns(
				self , n, r), n, r );

		System.out.println(pdag.countStringsThatMatch("bbbaa",r)+" "
				+pdag.countStringsThatMatch("aabbb",r)+" "
				+pdag.countStringsThatMatch("ababa",r));

		long baseline = pdag.countStringsThatMatch(self[0], r-1);
		
		System.out.println(
				(pdag.countStringsThatMatch("bbbaa",r-1)-baseline)+" "
				+(pdag.countStringsThatMatch("aabbb",r-1)-baseline)+" "
				+(pdag.countStringsThatMatch("abbab",r-1)-baseline));	
	}
	
	@Test
	public void countMatchingTest5(){
		/** example from our TCS paper */
		int n=5,r=3;
		Alphabet.set(new BinaryLetterAlphabet());
		String[] self = { "abbbb", "aabbb", "baaaa", "baaab", "baaba", "babba", "bbbbb" };
		pdag = new ContiguousCountingDAG( RChunkPatterns.rChunkPatterns(self, n, r), n, r );		
		assertEquals(pdag.nrStrings(), 2);
		assertEquals(pdag.asPatternTrie().content().toString(), "[ababb, bbabb]");
		
		String[] all = { 
				"aaaaa", "aaaab", "aaaba", "aaabb", "aabaa", "aabab", "aabba", "aabbb",
				"abaaa", "abaab", "ababa", "ababb", "abbaa", "abbab", "abbba", "abbbb",
				"baaaa", "baaab", "baaba", "baabb", "babaa", "babab", "babba", "babbb",
				"bbaaa", "bbaab", "bbaba", "bbabb", "bbbaa", "bbbab", "bbbba", "bbbbb" 				
			};
		
		int[] results = {
			0, 0, 0, 2, 0, 0, 0, 0,
			1, 1, 2, 2, 0, 0, 0, 0,
			0, 0, 0, 2, 0, 0, 0, 0,
			1, 1, 2, 2, 0, 0, 0, 0,
			};
		
		for( int i = 0 ; i < all.length ; i ++ ){
			System.out.println(all[i]+" : "+results[i]);
			assertEquals(pdag.countStringsThatMatch(all[i],3),results[i]);
		}
		
	}

	@Test
	public void countMatchingTest6() {
		int n = 5, r = 3;
		Alphabet.set(new BinaryLetterAlphabet());
		Vector<PatternTrie> rchunkd  = RChunkPatterns.rChunkPatterns(
					new String[] { "ababb", "ababa", "abbab" }, n, r );
		RChunkPatterns.rContiguousPatterns(rchunkd, n, r);

		for( PatternTrie pt : rchunkd ){
			pt.leafify();
		}
		
		System.out.println(rchunkd.get(0).content().toString());		
		System.out.println(rchunkd.get(1).content().toString());
		System.out.println(rchunkd.get(2).content().toString());
		
		PatternTrie rcont = RChunkPatterns.rContiguousGraphWithoutFailureLinks(rchunkd, n, r);
		System.out.println(rcont.content().toString());
		
		List<Set<PatternTrie>> levels = rcont.levelOrder(n);
		
		for( int i = 0 ; i < levels.size() ; i ++ ){
			System.out.println("entry points @ level "+i+" : "+levels.get(i).size());
		}
	}
	
	@Test
	public void ternaryTest() {
		// Alphabet.set(new BinaryAlphabet());
		// String[] self = { "10010", "10000", "10001", "01111", "00111",
		// "10110", "11111" };

		Alphabet.set(new BinaryLetterAlphabet());
		String[] self = { "baaba", "baaaa", "baaab", "abbbb", "aabbb", "babba",
				"bbbbb" };

		// Alphabet.set(new TernaryAlphabet());
		// String[] self = { "abcab" };
		int n = 5; // self[0].length();
		int r = 3;
		System.out.println();

		Vector<PatternTrie> rchunkd = RChunkPatterns.substringTries(self, n, r);

		//PatternTrieTikzer.tizkTries(rchunkd, r);

		for (PatternTrie pt : rchunkd) {
			pt.invert();
			System.out.println(pt.content().size() + " "
					+ pt.content().toString());
		}
		System.out.println();
		RChunkPatterns.filterPostfixes(rchunkd, n, r);
		for (PatternTrie pt : rchunkd) {
			System.out.println(pt.content().size() + " "
					+ pt.content().toString());
		}
		System.out.println();
		RChunkPatterns.filterPrefixes(rchunkd, n, r);
		for (PatternTrie pt : rchunkd) {
			System.out.println(pt.content().size() + " "
					+ pt.content().toString());
		}

		PatternTrie matcher = RChunkPatterns.rContiguousGraph(RChunkPatterns
				.rChunkPatterns(self, n, r), n, r);
		System.out.println(matcher.content().size());

		/*
		 * for( int i = 0 ; i < Math.pow( 2, n ) ; i ++ ) { Formatter f = new
		 * Formatter(); String m =
		 * f.format("%05d",Integer.parseInt(Integer.toBinaryString
		 * (i))).toString(); if( matcher.matches(m,r) >= r )
		 * System.out.println(m+" is nonself"); else
		 * System.out.println(m+" is self");
		 * //System.out.println(m+": "+(matcher.matches(m,r)>=r)); }
		 */

		/*
		 * System.out.println(rchunkd.get(0).content().size()+rchunkd.get(0).content
		 * ().toString());
		 * System.out.println(rchunkd.get(0).content().size()+rchunkd
		 * .get(1).content().toString());
		 * System.out.println(rchunkd.get(2).content().toString());
		 * RChunkPatterns.filterPostfixes(rchunkd, n, r);
		 * RChunkPatterns.filterPrefixes(rchunkd, n, r);
		 * System.out.println(rchunkd.get(0).content().toString());
		 * System.out.println(rchunkd.get(1).content().toString());
		 * System.out.println(rchunkd.get(2).content().toString());
		 */

		/*
		 * for( int j = 0 ; j < self.length ; j ++ ){ String[] self2 = new
		 * String[self.length]; for( int i = 0 ; i < self2.length ; i ++ ){ if(
		 * i != j ) self2[i] = self[i]; else self2[i] = self[(i+1)%self.length];
		 * } matcher =
		 * RChunkPatterns.rContiguousGraph(RChunkPatterns.rChunkPatterns(self2,
		 * n, r), n, r); System.out.println(matcher.content().size()); }
		 */

		// System.out.println(matcher.content().toString());
	}

	@Test
	public void detectorCountTest() {
		String[] self = { "01011", "01010", "01101" };
		int n = self[0].length();
		int r = 3;

		Vector<PatternTrie> rchunkd = RChunkPatterns.rChunkPatterns(self, n, r);
		RChunkPatterns.filterPostfixes(rchunkd, n, r);
		RChunkPatterns.filterPrefixes(rchunkd, n, r);
		System.out.println(rchunkd.get(0).content().toString());
		System.out.println(rchunkd.get(1).content().toString());
		System.out.println(rchunkd.get(2).content().toString());

		PatternTrie matcher = RChunkPatterns.rContiguousGraph(RChunkPatterns
				.rChunkPatterns(self, n, r), n, r);
		System.out.println(matcher.toString());
	}

	
	private static int[] simulateNegativeSelection( String[] self, int n, int r, int r2 ){
		Set<String> detectors = new HashSet<String>();
		int nl = Alphabet.get().letters().size();
		int[] ds = new int[n];
		do{
			String d = Alphabet.get().translate(ds);
			boolean matches_self = false;
			for( String s : self ){
				if( StringUtility.contiguousMatch(d, s, r) ){
					matches_self = true; 
					break;
				}
			}
			if( !matches_self ){
				detectors.add( d );
			}
		} while( !StringUtility.incString(ds) );
		int[] res = new int[(int)Math.pow(nl,n)];
		int i = 0;
		do{
			String m = Alphabet.get().translate(ds);
			int matches_mon = 0;
			for( String d : detectors ){
				if( StringUtility.contiguousMatch(d, m, r2 ) ){
					matches_mon ++;
				}
			}
			res[i++] = matches_mon;
		} while( !StringUtility.incString(ds) );
		return res;
	}

	public static void simulateNegSelTest( String[] self, int n, int r, int r2 ){
		//String[] self = { "01011", "01010", "01101" };
		ContiguousCountingDAG matcher = new ContiguousCountingDAG(RChunkPatterns
				.rChunkPatterns(self, n, r), n, r);		
		int[] res = simulateNegativeSelection( self, n, r, r2 );
		int[] xs = new int[n];
		int i = 0;
		do{
			String x = Alphabet.get().translate(xs);
			System.out.println(x+" "+res[i]+" ");			
			assertEquals(res[i], matcher.countStringsThatMatch(x, r2 ));
			i++;
		} while( !StringUtility.incString(xs) );
	}
	
	@Test
	public void simulateNegSelTest1(){
		Alphabet.set(new BinaryLetterAlphabet());		
		String[] self = { "abbbb", "aabbb", "baaaa", "baaab", "baaba", "babba", "bbbbb" };
		int n=5,r=3;
		Settings.DEBUG = false;
		for( int r2 = 0 ; r2 <= n ; r2 ++ ){
			System.out.println("r="+r+", r2 = "+r2);
			simulateNegSelTest( self, n, r, r2 );
		}
	}

	@Test
	public void simulateNegSelTest2(){
		Alphabet.set(new BinaryAlphabet());		
		String[] self = { "01011", "01010", "01101" };
		int n=5,r=3;
		Settings.DEBUG = false;
		for( int r2 = 0 ; r2 <= n ; r2 ++ ){
			simulateNegSelTest(self, n, r, r2 );
		}
	}
	
	@Test
	public void simulateNegSelTest3(){
		Alphabet.set(new TernaryAlphabet());
		String[] self = { "abcab" };
		int n = 5, r = 3;
		for( int r2 = 0 ; r2 <= n ; r2 ++ ){
			simulateNegSelTest(self, n, r, r2 );
		}
	}

	@Test
	public void simulateNegSelTest4(){
		Alphabet.set(new BinaryLetterAlphabet());
		String[] self = { "ababb", "ababa", "abbab" };
		int n = 5, r = 3;
		for( int r2 = 0 ; r2 <= n ; r2 ++ ){
			simulateNegSelTest(self, n, r, r2 );
		}
	}

	@Test
	public void simulateNegSelTest5(){
		Alphabet.set(new BinaryLetterAlphabet());
		String[] self = { "aaaaa" };
		int n = 5, r = 3;
		for( int r2 = 0 ; r2 <= r ; r2 ++ ){
			simulateNegSelTest(self, n, r, r2 );
		}
	}
	
	@Test
	public void countContTest(){
		
		for( int n = 1 ; n < 10 ; n ++ ){
			System.out.print( StringUtility.notMatchingContiguousDetectors(n, 3, 3)+", " );
		}
	}
	
	public static void main(String[] args) {
		org.junit.runner.JUnitCore.main("tests.TrieTests");
	}

}
