package util;


public class Debug {
	public static void log( String s ){
		if( Settings.DEBUG ){
			System.err.println(s);
		}
	}
}
