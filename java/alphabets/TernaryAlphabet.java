/*
 * Created on 22.10.2009 by Johannes Textor
 * This Code is licensed under the BSD license:
 * http://www.opensource.org/licenses/bsd-license.php
 */
package alphabets;

import java.util.Collections;

public class TernaryAlphabet extends Alphabet {
        public TernaryAlphabet(){
                letters().add('a');
                letters().add('b');
                letters().add('c');
                Collections.sort(letters);
        }
}
