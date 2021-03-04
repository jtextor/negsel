/*
 * Created on 26.10.2009 by Johannes Textor
 * This Code is licensed under the BSD license:
 * http://www.opensource.org/licenses/bsd-license.php
 */
package alphabets;

import java.util.Collections;

public class BinaryLetterAlphabet extends Alphabet {
	public BinaryLetterAlphabet(){
		letters().add('a');
		letters().add('b');
		Collections.sort(letters);
	}
}
