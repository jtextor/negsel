package util;
import java.util.Random;

/*
 * Created on 07.09.2009 by Johannes Textor
 * This Code is licensed under the BSD license:
 * http://www.opensource.org/licenses/bsd-license.php
 */

public class RandomUtility {

	private static Random rng = null;

	/**
	 * @return Returns the rng.//Ist dafür gut, dass nur einmal rng erzeugt wird
	 */
	public static Random getRng() {
		if (rng == null)
			rng = new Random();
		return rng;
	}

	/**
	 * Diese Methode generiert eine Zufallszahl zwischen 0 und n-1
	 * 
	 * @param n Die Zahl n
	 * @return Eine zufällige Zahl zwischen 0 und n-1
	 */
	public static int randomInt(int n) {
		return getRng().nextInt(n);
	}

	/**
	 * 
	 * @param n Die Zahl n
	 * @return Eine zufällige Zahl zwischen 0 und 1.0
	 */
	public static double nextDouble() {
		return getRng().nextDouble();
	}

	/**
	 * 
	 * Erzeugt einen zufälligen String aus Nullen und Einsen.
	 * 
	 * @param string_length Die erwünschte Länge des Strings.
	 * @return Der zufällige String.
	 */
	public static String createString(int string_length) {
		String new_string = "";
		for (int i = 0; i < string_length; i++) {
			if (getRng().nextBoolean() == true) {
				new_string = new_string + 1;
			} else {
				new_string = new_string + 0;
			}
		}
		return new_string;
	}

	/**
	 * 
	 * Diese Methode erzeugt eine Bibliothek mit zufälligen Strings
	 * 
	 * @param string_length ist die Länge der zufällig erzeugten Strings
	 * @param array_length ist die Größe der Bibliothek
	 * @return Bibliothek mit zufälligen Strings
	 */
	public static String[] createArray(int string_length, int array_length) {
		String[] new_array = new String[array_length];
		//String [] new_array;
		for (int i = 0; i < array_length; i++) {
			new_array[i] = "";
			for (int j = 0; j < string_length; j++) {
				if (getRng().nextBoolean() == true) {
					new_array[i] = new_array[i] + "1";
				} else {
					new_array[i] = new_array[i] + "0";
				}

			}
		}
		return new_array;
	}

	/**
	 * 
	 * Diese Methode wählt aus einem gegebenen Array einen String aus
	 * 
	 * @param gen ist der übergebene Array
	 * @return Der zufällig ausgewählte String
	 */
	public static String searchElement(String[] gen) {
		int random_int = randomInt(gen.length);
		return (gen[random_int]);
	}
}
