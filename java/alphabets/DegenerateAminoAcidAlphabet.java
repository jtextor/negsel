package alphabets;

import java.util.Collections;

public class DegenerateAminoAcidAlphabet extends Alphabet {
	public int i( char c ){
		char _c = c;
		if( c == 'I' || c == 'L' ){
			_c = 'V';
		}
		if( c == 'Y' ){
			_c = 'F';
		}
		if( c == 'Q' ){
			_c = 'E';
		}		
		/*if( c == 'M' ){
			_c = 'L';
		}*/
		/*if( c == 'A' ){
			_c = 'G';
		}*/
		return Collections.binarySearch(letters(), _c);
	}
	
	public DegenerateAminoAcidAlphabet(){
		letters.add('S');
		letters.add('T');		
		letters.add('C');
		letters.add('H');
		letters.add('M');
		letters.add('F');
		letters.add('P');
		letters.add('W');
		//letters.add('Y');
		
		letters.add('N');
		//letters.add('Q');

		letters.add('D');
		letters.add('E');
		
		letters.add('A');
		letters.add('G');
		
		letters.add('K');
		letters.add('R');
		
		//letters.add('I');
		letters.add('V');
		//letters.add('L');
		
		Collections.sort(letters);
	}
}
