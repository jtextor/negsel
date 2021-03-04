package alphabets;

import java.util.Collections;

/*
 * Created on 08.09.2009 by Johannes Textor
 * This Code is licensed under the BSD license:
 * http://www.opensource.org/licenses/bsd-license.php
 */

public class BinaryAlphabet extends Alphabet {
	public BinaryAlphabet(){
		letters().add('0');
		letters().add('1');
		Collections.sort(letters);
	}
}
