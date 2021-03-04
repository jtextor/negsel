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
import alphabets.LatinAlphabet;

/*
 * Created on 27.08.2009 by Johannes Textor
 * This Code is licensed under the BSD license:
 * http://www.opensource.org/licenses/bsd-license.php
 */

public class Main {
	public static void main(String[] args) throws FileNotFoundException {		
		Options myOptions = new Options();
		myOptions.addOption(new Option("n", true,
				"Length of strings in self set"));
		myOptions.addOption(new Option("r", true, "Parameter r <= n"));
		myOptions.addOption(new Option("o", false, "Offset into strings"));
		myOptions.addOption(new Option("d", true, "Add to alphabets the digits from 0 to ..."));		
		myOptions.addOption(new Option("g", false, "Print debug information"));
		myOptions.addOption(new Option("v", false, "Invert match (like grep)"));
		//myOptions.addOption(new Option("b", false, "Subtract baseline noise"));
		myOptions.addOption(new Option("c", false, "Count matching detectors instead of binary match"));
		myOptions.addOption(new Option("l", false, "Output logarithms instead of actual values"));		
		myOptions.addOption(new Option("k", false, "Use r-chunk instead of r-contiguous matching"));
		myOptions.addOption(new Option("p", true, "Output k-th component of matching profile (0 for full profile)"));
		myOptions.addOption(new Option("self", true,
				"File containing self set (1 string per line)"));
		myOptions.addOption(new Option("alphabet", true,
				"Alphabet, currently one of [infer|binary|binaryletter|amino|damino|latin]. Default: infer (uses all characters from \"self\" file as alphabet). Alternatively, specify file://[f] to set the alphabet to all characters found in file [f]."));

		CommandLineParser parser = new BasicParser();

		int n = 0;
		int r = 0; // matching length used for training
      int r2 = 0; // matching length used for classification
		String self = "";
		boolean invertmatch = false;
		boolean usechunk = false;
		boolean subtract_baseline = false;
		boolean matching_profile = false;
		boolean logarithmize = false;
      boolean sliding = false;
		boolean counting = false;
		Alphabet.set(new BinaryAlphabet());
		CommandLine cmdline = null;
		try {
			cmdline = parser.parse(myOptions, args);
			n = Integer.parseInt(cmdline.getOptionValue("n"));
			r = Integer.parseInt(cmdline.getOptionValue("r"));
			if( cmdline.hasOption("g") ) Settings.DEBUG = true;
			if( cmdline.hasOption("v") ) invertmatch = true;
			if( cmdline.hasOption("k") ) usechunk = true;
			if( cmdline.hasOption("c") ) counting = true;
			if( cmdline.hasOption("l") ) logarithmize = true;
			if( cmdline.hasOption("p") ){
            matching_profile = true;
            r2 = Integer.parseInt(cmdline.getOptionValue("p"));
         }
			//if( cmdline.hasOption("b") ) subtract_baseline = true;
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
				if (alpha.equals("latin"))
					Alphabet.set(new LatinAlphabet());
			} else {
				Alphabet.set(new Alphabet(new File(self)));
			}
			if( cmdline.hasOption("d") ){
				int escape_letters = Integer.parseInt(cmdline.getOptionValue("d"));
				for( int i = 0 ; i < escape_letters && i < 10 ; i ++ ){
					Alphabet.get().letters().add(Character.forDigit(i,10));
				}
				Collections.sort(Alphabet.get().letters());
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
		if( !usechunk ){
			if( counting ){
				counter = new ContiguousCountingDAG(chunks, n, r);
				if( subtract_baseline ){
					baseline = counter.countStringsThatMatch( Settings.first_self_string, r-1 );
				}
			} else {
				matcher = RChunkPatterns.rContiguousGraphWithFailureLinks(chunks, n, r);
			}
		}

      // output matching lengths to be used
      int i1 = 0, i2 = 0;
      if( !matching_profile || usechunk ){
         i1 = r; i2 = r; 
      } else {
         if( r2 > 0 && r2 <= n ){
            i1 = r2; i2 = r2;
         } else {
            i1 = 0; i2 = n;
         }
      }

		Debug.log("matcher constructed");
		Scanner scan = new Scanner(System.in);
		while ( scan.hasNextLine() ) {
         String line = scan.nextLine().trim();
         //System.out.print( line );

         int lineindex;
			if( line.length() < n ){
				System.out.print( "NaN" );
			}
         for( lineindex = 0; lineindex <= line.length()-n ; lineindex ++ ){
	         double[] nmatch = new double[i2-i1+1];
            String l = line.substring( lineindex, lineindex+n );
            if( usechunk ){
               // r-chunk detectors
               int i = 0;
               for( PatternTrie chunkmatcher : chunks ){
                  if( chunkmatcher.matches(l.substring(i),r) >= r != invertmatch ){
                     nmatch[0] ++;
                  }
                  /*PatternTrie prefixmatcher = chunkmatcher.destinationNode(l.substring(i,i+r-1));
                  if( prefixmatcher != null )
                     nmatch_r_l += prefixmatcher.count(1);
                  for( PatternTrie postfixmatcher : chunkmatcher.children ){
                     if( ( postfixmatcher != null && 
                        postfixmatcher.matches(l.substring(i),r-1) >= r-1 ) 
                              != invertmatch ){
                        nmatch_r_r ++;
                     }
                  }*/
                  i++;
               }
					if( logarithmize ){
						nmatch[0] = Math.log(1+nmatch[0])/Math.log(2.);
					}
            }
            else{
               // r-contiguous detectors
               long last_result = -1;
               for( int i = i1 ; i <= i2 ; i ++ ){
                  if( last_result != 0 ){
							if( counting ){
								last_result = counter.countStringsThatMatch(l,i)
									- (i<r?baseline:0);
							} else {
								int rm = 1;
								while( matcher.matches(l,rm) >= rm && rm <= n ){
									rm ++;
								}
								//if( rm > r ){
								//	last_result = 1;
								//} else {
								//	last_result = 0;
								//}
								last_result = rm-1;
							}
                     //last_result = ;
                     if( logarithmize ){
                        nmatch[i-i1] += Math.log(1+last_result)/Math.log(2.);
                     } else {
                        nmatch[i-i1] += last_result;
                     }
                  }
               } 
            }
	         for( double i : nmatch ){
   	         System.out.print(i+" ");
      	   }
         }
         System.out.println();
         System.out.flush();
		}

		// System.out.println("n: "+n);
		// example1();
	}
}
