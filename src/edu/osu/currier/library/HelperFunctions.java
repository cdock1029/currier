package edu.osu.currier.library;

import java.util.Locale;

import com.parse.Parse;

import edu.osu.currier.Configurator;

/**
 * Place to consolidate helper functions.
 * @author conordockry
 *
 */
public final class HelperFunctions {

	public static final class country {
		public static final String Locality = "USD ";
		//public static final String Locality = "INR "; //joda Money parameter, Indian rupee
		public static final Locale LOC = Locale.US;  //NumberFormat Locale
		//public static final Locale  LOC = new Locale("hi", "IN");  //Hindi, India
		//public static final Locale LOC = new Locale("en", "IN"); //English, India
	}

}
