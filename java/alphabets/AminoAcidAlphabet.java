package alphabets;

import java.util.Collections;

/*
 * Created on 08.09.2009 by Johannes Textor
 * This Code is licensed under the BSD license:
 * http://www.opensource.org/licenses/bsd-license.php
 */

public class AminoAcidAlphabet extends Alphabet {	
	public AminoAcidAlphabet(){
		letters.add('A');
		letters.add('R');
		letters.add('N');
		letters.add('D');
		letters.add('C');
		letters.add('E');
		letters.add('Q');
		letters.add('G');
		letters.add('H');
		letters.add('I');
		letters.add('L');
		letters.add('K');
		letters.add('M');
		letters.add('F');
		letters.add('P');
		letters.add('S');
		letters.add('T');
		letters.add('W');
		letters.add('Y');
		letters.add('V');
		
		Collections.sort(letters);
	}
}
