import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import util.Debug;
import util.Settings;
import algorithms.ContiguousCountingDAG;
import algorithms.PatternTrie;
import algorithms.RChunkPatterns;
import alphabets.Alphabet;
import alphabets.AminoAcidAlphabet;
import alphabets.BinaryAlphabet;
import alphabets.BinaryLetterAlphabet;
import alphabets.DegenerateAminoAcidAlphabet;

/*
 * Created on 27.08.2009 by Johannes Textor
 * This Code is licensed under the BSD license:
 * http://www.opensource.org/licenses/bsd-license.php
 */

public class SeqLogo {
	public static void main(String[] args) throws FileNotFoundException {		
		Options myOptions = new Options();
		myOptions.addOption(new Option("n", true,
				"Length of strings in self set"));
		myOptions.addOption(new Option("r", true, "Parameter r <= n"));
		myOptions.addOption(new Option("g", false, "Print debug information"));		
		myOptions.addOption(new Option("self", true,
				"File containing self set (1 string per line)"));
		myOptions.addOption(new Option("alphabet", true,
				"Alphabet, currently one of [infer|binary|binaryletter|amino|damino]. Default: infer (uses all characters from \"self\" file as alphabet)"));
		CommandLineParser parser = new BasicParser();
		int n = 0;
		int r = 0; // matching length used for training
		int r2 = 0; // matching length used for classification
		String self = "";
		boolean invertmatch = false;
		boolean usechunk = false;
		boolean subtract_baseline = false;
		boolean matching_profile = true;
		boolean logarithmize = false;
      	boolean sliding = false;
		boolean counting = true;
		Alphabet.set(new BinaryAlphabet());
		CommandLine cmdline = null;
		try {
			cmdline = parser.parse(myOptions, args);
			n = Integer.parseInt(cmdline.getOptionValue("n"));
			r = Integer.parseInt(cmdline.getOptionValue("r"));
			if( cmdline.hasOption("g") ) Settings.DEBUG = true;
			self = cmdline.getOptionValue("self");
			if (!new File(self).canRead()) {
				throw new IllegalArgumentException("Can't read file " + self);
			}
			String alpha = cmdline.getOptionValue("alphabet");
			if (alpha != null) {
            if (alpha.startsWith("file://")){
               Alphabet.set(new Alphabet(new File(alpha.substring(7))));
            }
				if (alpha.equals("infer"))
					Alphabet.set(new Alphabet(new File(self)));
				if (alpha.equals("amino"))
					Alphabet.set(new AminoAcidAlphabet());
				if (alpha.equals("binary"))
					Alphabet.set(new BinaryAlphabet());
				if (alpha.equals("binaryletter"))
					Alphabet.set(new BinaryLetterAlphabet());
				if (alpha.equals("damino"))
					Alphabet.set(new DegenerateAminoAcidAlphabet());
			} else {
				Alphabet.set(new Alphabet(new File(self)));
			}
			if (r < 0 || n <= 0 || r > n) {
				throw new IllegalArgumentException(
						"Illegal value(s) for n and/or r");
			}
		} catch (Exception e) {
			System.out.print("Error parsing command line: " + e.getMessage()
					+ "\n\n");
			HelpFormatter help = new HelpFormatter();
			help.printHelp("java -jar negsel.jar", myOptions);
			System.exit(1);
		}
 
		Debug.log("constructing matcher");
		List<PatternTrie> chunks = RChunkPatterns.rChunkPatterns(self, n, r, 0);
		PatternTrie matcher = null;
		ContiguousCountingDAG counter = null;
		long baseline = 0;  
		counter = new ContiguousCountingDAG(chunks, n, r);

		// output matching lengths to be used
		int i1 = 0, i2 = n;

		Debug.log("matcher constructed");

		for( char c : Alphabet.get().letters() ){
			System.out.print( c+"\t" );
			System.out.println( counter.countStringsThatMatch( new String(new char[n]).replace("\0", ""+c), 1 ) );
		}

		//Debug.log( String.valueOf( counter.countStrings() ) );
		//Debug.log( String.valueOf( counter.countStringsRev() ) );
	}
}
