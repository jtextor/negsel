/*                                                                                                              
 * Created on 23.10.2009 by Johannes Textor                                                                     
 * This Code is licensed under the BSD license:                                                                 
 * http://www.opensource.org/licenses/bsd-license.php                                                           
 */
package visualization;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import algorithms.PatternTrie;
import algorithms.RChunkPatterns;
import alphabets.Alphabet;

public class PatternTrieTikzer {

	private final static boolean DEBUG_NODE_NR = false;
	
	private static Map<PatternTrie, Integer> nodeNr;

	private static int lastNr;

	private static Vector<PatternTrie> stringForest;

	private static Vector<PatternTrie> stringForestWithContLinks;

	private static Vector<String> chunkLinks;

	private static Vector<String> chunkFailureLinks;

	private static Vector<String> contiguousLinksNoPathToSink;

	private static Vector<String> contiguousLinksNoPathFromRoot;
	
	private static Vector<String> contiguousLinks;

	private static Vector<String> contiguousFailureLinks;

	private static Set<PatternTrie> onPathToSink;
	
	private static Set<PatternTrie> onPathFromRoot;

	private static int r;

	public static void tizkTries(Vector<PatternTrie> rchunkd, int _r) {
		stringForest = new Vector<PatternTrie>();
		stringForestWithContLinks = new Vector<PatternTrie>();
		chunkLinks = new Vector<String>();
		chunkFailureLinks = new Vector<String>();

		contiguousLinks = new Vector<String>();
		contiguousFailureLinks = new Vector<String>();
		contiguousLinksNoPathToSink = new Vector<String>();
		contiguousLinksNoPathFromRoot = new Vector<String>();

		nodeNr = new HashMap<PatternTrie, Integer>();
		onPathToSink = new HashSet<PatternTrie>();
		onPathFromRoot = new HashSet<PatternTrie>();
		lastNr = 0;
		r = _r;
		for (PatternTrie pt : rchunkd)
			stringForest.add(new PatternTrie(pt));

		FileOutputStream fout;
		try {
			fout = new FileOutputStream("trieexamples.tex");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		PrintWriter o = new PrintWriter(fout);

		for (PatternTrie pt : stringForest) {
			stringForestWithContLinks.add(new PatternTrie(pt));
		}

		o.println("\\negselsinks{");
		PatternTrie sink = new PatternTrie();
		for (int i = r; i > 0; i--) {
			sink.weights = new int[Alphabet.get().letters().size()];
			o.println("\\node [target] (n" + (i) + ") at (" + (i+11) + ",0) {$\\tau_" + (i) + "$};");
			nodeNr.put(sink, i);
			//onPathToSink.add(sink);
			PatternTrie sink_new = new PatternTrie();
			for (int j = 0; j < Alphabet.get().letters().size(); j++) {
				sink_new.children[j] = sink;
			}
			sink = sink_new;
		}
		sink = sink.children[0];
		for (int i = r; i > 1; i--) {
			int angle = -50;
			for (Character c : Alphabet.get().letters()) {
				o.println("\\draw [->] (n" + (i - 1) + ") edge [bend left="
						+ angle + "] node [label] {" + c
						+ "\\negselfailurelinkweight{1} } (n" + i + ");");
				angle += (100 / (Alphabet.get().letters().size() - 1));
			}
		}
		o.println("}");
		lastNr = r + 1;

		stringForest.add(sink);
		stringForestWithContLinks.add(sink);

		// determine node numbers for TikZ
		for (int i = stringForest.size() - 2; i >= 0; i--) {
			traverseNumber(stringForestWithContLinks.get(i), 0);
		}

		// determine r-chunk detection failure links
		for (int i = stringForest.size() - 2; i >= 0; i--) {
			traverseChunkFailureLinks(stringForestWithContLinks.get(i), i, "");
		}

		System.out.println(chunkFailureLinks);

		/**
		 * From here on, insert contiguous links and do r-contiguous detector
		 * stuff
		 */

		for (int i = stringForest.size() - 2; i >= 0; i--) {
			stringForestWithContLinks.get(i).insertContiguousLinks(
					stringForestWithContLinks.get(i + 1));
		}

		traverseFromRoot(stringForestWithContLinks.get(0));
		
		for (int i = stringForest.size() - 2; i >= 0; i--) {
			traverseToSink(stringForestWithContLinks.get(i));
			o.println("\\node [root] (n"
					+ nodeNr.get(stringForestWithContLinks.get(i)) + ") at ("
					+ (4 * i) + ",0) {$\\rho_" + (i + 1) + "$}");
			traverseFirst(stringForest.get(i),
					stringForestWithContLinks.get(i), stringForestWithContLinks
							.get(i + 1), o);
			o.println(";");
		}

		stringForest.remove(sink);
		for (PatternTrie pt : stringForest) {
			pt.invert();
		}
		RChunkPatterns.filterPostfixes(stringForest, stringForest.size() + r
				- 1, r);
		RChunkPatterns.filterPrefixes(stringForest,
				stringForest.size() + r - 1, r);
		stringForest.add(sink);
		for (int i = stringForest.size() - 2; i >= 0; i--) {
			traverseContFailureLinksFirst(stringForest.get(i),
					stringForestWithContLinks.get(i), stringForestWithContLinks
							.get(i + 1));
		}
		// System.out.println(contiguousFailureLinks);

		o.println("\\negselchunklinks{");
		for (String s : chunkLinks) {
			String[] sarr = s.split(" ");
			o.println("\\draw [->,chunklink] (n" + sarr[0]
					+ ") .. controls +(3,-3) .. node[label] {" + sarr[1]
					+ "\\negselfailurelinkweight{1} } (n" + sarr[2] + ");");
		}
		o.println("}");

		o.println("\\negselchunkfaillinks{");
		for (String s : chunkFailureLinks) 
			printFailureLink(o, s);
		o.println("}");

		o.println("\\negselcontlinksnopathtosink{");
		for (String s : contiguousLinksNoPathToSink) {
			String[] sarr = s.split(" ");
			o.println("\\draw [->,contlink] (n" + sarr[0]
					+ ") .. controls +(3,-3) .. node[label] {" + sarr[1]
					+ "} (n" + sarr[2] + ");");
		}
		o.println("}");
		
		o.println("\\negselcontlinksnopathfromroot{");
		for (String s : contiguousLinksNoPathFromRoot) {
			String[] sarr = s.split(" ");
			o.println("\\draw [->,contlink] (n" + sarr[0]
					+ ") .. controls +(3,-3) .. node[label] {" + sarr[1]
					+ "} (n" + sarr[2] + ");");
		}
		o.println("}");

		o.println("\\negselcontlinks{");
		for (String s : contiguousLinks) {
			String[] sarr = s.split(" ");
			o.println("\\draw [->,contlink] (n" + sarr[0]
					+ ") .. controls +(3,-3) .. node[label] {" + sarr[1]
					+ "\\negselfailurelinkweight{1} } (n" + sarr[2] + ");");
		}
		o.println("}");

		o.println("\\negselcontfaillinks{");
		for (String s : contiguousFailureLinks) 
			printFailureLink(o, s);			
		o.println("}");

		o.close();
		
		for( PatternTrie ptwl: onPathToSink ){
			System.out.println(nodeNr.get(ptwl));	
		}

		try {
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void printFailureLink(PrintWriter o, String s) {
		String[] sarr = s.split(" ");
		if( Integer.parseInt(sarr[2]) <= r )
			o.println("\\negselfailurelinktosink{");
		o.println("\\draw [->,faillink] (n" + sarr[0]
			+ ") .. controls +(2,-1) .. node[label] {" + sarr[1]
			+ "\\negselfailurelinkweight{" + sarr[3] + "} } (n" + sarr[2]
			+ ");");
		if( Integer.parseInt(sarr[2]) <= r )
			o.println("}");
	}

	private static void traverseFirst(PatternTrie pt, PatternTrie ptwl,
			PatternTrie ptwl_nl, PrintWriter o) {
		if (pt.getChildren().size() == 0)
			return;
		for (Character c : Alphabet.get().letters()) {
			if (pt.getChildren().containsKey(c)) {
				printBeforeNode(pt, ptwl, o, c);
				traverse(pt.getChildren().get(c), ptwl.getChildren().get(c),
						ptwl_nl, o);
				printAfterNode(o, c);
			} else {
				handleNonExistingSigmaEdge(o, ptwl, ptwl_nl, c);
			}
		}
	}

	private static void traverse(PatternTrie pt, PatternTrie ptwl,
			PatternTrie ptwl_nl, PrintWriter o) {
		if (pt.getChildren().size() == 0)
			return;
		for (Character c : Alphabet.get().letters()) {
			if (pt.getChildren().containsKey(c)) {
				printBeforeNode(pt, ptwl, o, c);
				traverse(pt.getChildren().get(c), ptwl.getChildren().get(c),
						ptwl_nl.getChildren().get(c), o);
				printAfterNode(o, c);
			} else {
				handleNonExistingSigmaEdge(o, ptwl, ptwl_nl, c);
			}
		}
	}

	private static void handleNonExistingSigmaEdge(PrintWriter o, PatternTrie ptwl,
			PatternTrie ptwl_nl, Character sigma) {
		printComplementEdge(o, sigma);
		String clink = nodeNr.get(ptwl) + " " + sigma + " " + nodeNr.get(ptwl_nl.getChildren().get(sigma));
		if (!onPathToSink.contains(ptwl)){
			contiguousLinksNoPathToSink.add(clink);				
		} else if(!reachesPath(ptwl)){
			contiguousLinksNoPathFromRoot.add(clink);					
		} else {
			contiguousLinks.add(clink);
		}
	}

	private static void traverseChunkFailureLinks(PatternTrie pt,
			int root_index, String string_from_root) {
		if (pt.getChildren().size() == 0)
			return;
		for (int i = 0; i < pt.children.length; i++) {
			if (pt.children[i] != null) {
				if (pt.children[i].isComplete()) {
					int dst_index = root_index + 1;
					String dst_string = string_from_root.substring(1)
							+ Alphabet.get().c(i);
					while (stringForestWithContLinks.get(dst_index)
							.destinationNode(dst_string) == null
							|| stringForestWithContLinks.get(dst_index)
									.destinationNode(dst_string).isComplete()) {
						dst_index++;
						dst_string = dst_string.substring(1);
					}
					chunkFailureLinks.add(nodeNr.get(pt)
							+ " "
							+ Alphabet.get().c(i)
							+ " "
							+ nodeNr.get(stringForestWithContLinks.get(
									dst_index).destinationNode(dst_string))
							+ " " + (root_index - dst_index + 1));
				} else {
					traverseChunkFailureLinks(pt.children[i], root_index,
							string_from_root + Alphabet.get().c(i));
				}
			}
		}
	}

	private static void traverseContFailureLinksFirst(PatternTrie pt,
			PatternTrie ptwl, PatternTrie ptwl_nl) {
		if (pt.getChildren().size() == 0)
			return;
		for (int i = 0; i < pt.children.length; i++) {
			if (pt.children[i] == null) {
				ptwl.children[i] = ptwl_nl;
				ptwl.weights[i] = -1;
				contiguousFailureLinks.add(nodeNr.get(ptwl) + " "
						+ Alphabet.get().c(i) + " " + nodeNr.get(ptwl_nl) + " "
						+ (ptwl.weights[i] + 1));
			} else {
				traverseContFailureLinks(pt.children[i], ptwl.children[i],
						ptwl_nl);
			}
		}
	}

	private static void traverseContFailureLinks(PatternTrie pt,
			PatternTrie ptwl, PatternTrie ptwl_nl) {
		if (pt.getChildren().size() == 0)
			return;
		for (int i = 0; i < pt.children.length; i++) {
			if (pt.children[i] == null) {
				ptwl.children[i] = ptwl_nl.children[i];
				ptwl.weights[i] = ptwl_nl.weights[i] - 1;
				contiguousFailureLinks.add(nodeNr.get(ptwl) + " "
						+ Alphabet.get().c(i) + " "
						+ nodeNr.get(ptwl_nl.children[i]) + " "
						+ (ptwl.weights[i] + 1));
			} else {
				traverseContFailureLinks(pt.children[i], ptwl.children[i],
						ptwl_nl.children[i]);
			}
		}
	}

	private static void printComplementEdge(PrintWriter o, Character c) {
		o
				.println("child[complement] { node[vertex] {} edge from parent [->] node[label] {"
						+ c + "} } ");
	}

	private static void printBeforeNode(PatternTrie pt, PatternTrie ptwl,
			PrintWriter o, Character c) {
		o.print("child ["
				+ (pt.getChildren().get(c).isComplete() ? "complete," : "")
				+ (!onPathToSink.contains(ptwl.getChildren().get(c)) ? "cnosink," : "")
				+ (!reachesPath(ptwl.getChildren().get(c)) ? "doesnotreachpathfromroot," : "")				
				+ "] { node [vertex,"
				+ (onPathFromRoot.contains(ptwl.getChildren().get(c)) ? "onpathfromroot," : "")
				+ (reachesPath(ptwl.getChildren().get(c)) ? "reachespathfromroot," : "")
				+ "] (n"
				+ nodeNr.get(ptwl.getChildren().get(c)) + ")");
		o.print("{");
		if( DEBUG_NODE_NR )
			o.print( nodeNr.get(ptwl.getChildren().get(c)) );
		o.print("} ");
	}

	private static void printAfterNode(PrintWriter o, Character c) {
		o.println("edge from parent [->] node[label] {" + c
				+ "\\negselfailurelinkweight{1} } }");
	}

	private static boolean traverseToSink(PatternTrie ptwl) {
		if (nodeNr.get(ptwl) <= r){
			onPathToSink.add( ptwl );
			return true;
		}
		boolean success = false;
		for (Character c : ptwl.getChildren().keySet()) {
			PatternTrie t = ptwl.getChildren().get(c);
			if (onPathToSink.contains(t) || traverseToSink(t)){
				onPathToSink.add( ptwl );
				success = true;
			}
		}
		return success;
	}
	
	private static boolean traverseFromRoot(PatternTrie ptwl) {
		if (nodeNr.get(ptwl) <= r ){
			onPathFromRoot.add(ptwl);
			return true;
		}
		if( ptwl.isComplete() ) return false;
		boolean success = false;
		for (Character c : ptwl.getChildren().keySet()) {
			PatternTrie t = ptwl.getChildren().get(c);
			if( traverseFromRoot(t) ){
				onPathFromRoot.add(t);
				success = true;
			}
		}
		return success;
	}

	private static boolean reachesPath(PatternTrie ptwl) {
		if (onPathFromRoot.contains(ptwl))
			return true;
		if (nodeNr.get(ptwl) <= r ) return false;
		for (Character c : ptwl.getChildren().keySet()) {
			if (reachesPath(ptwl.getChildren().get(c)))
				return true;
		}
		return false;
	}

	private static void traverseNumber(PatternTrie pt, int level) {
		if (!nodeNr.containsKey(pt))
			nodeNr.put(pt, ++lastNr);
		pt.weights = new int[Alphabet.get().letters().size()];
		for (Character c : Alphabet.get().letters()) {
			if (pt.getChildren().containsKey(c))
				traverseNumber(pt.getChildren().get(c), level + 1);
			else if ( pt.isComplete() )
				chunkLinks.add(nodeNr.get(pt) + " " + c + " " + (level + 1));
		}
	}
}
