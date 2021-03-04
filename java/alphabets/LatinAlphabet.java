package alphabets;

import java.util.Collections;

/*
 * Created on 25.12.2016 by Johannes Textor
 * This Code is licensed under the BSD license:
 * http://www.opensource.org/licenses/bsd-license.php
 */

public class LatinAlphabet extends Alphabet {	
	public LatinAlphabet(){
		letters.add('a');
		letters.add('b');
		letters.add('c');
		letters.add('d');
		letters.add('e');
		letters.add('f');
		letters.add('g');
		letters.add('h');
		letters.add('i');
		letters.add('j');
		letters.add('k');
		letters.add('l');
		letters.add('m');
		letters.add('n');
		letters.add('o');
		letters.add('p');
		letters.add('q');
		letters.add('r');
		letters.add('s');
		letters.add('t');
		letters.add('u');
		letters.add('v');
		letters.add('w');
		letters.add('x');
		letters.add('y');
		letters.add('z');
		letters.add('_');
		
		Collections.sort(letters);
	}
}
