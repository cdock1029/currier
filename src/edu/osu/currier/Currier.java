/**
 * Initialize the parameters for connecting to the data store online.
 */
package edu.osu.currier;

import com.parse.Parse;
import android.app.Application;

public class Currier extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		// parse initialization
		Parse.initialize(this, Configurator.keys.applicationId, Configurator.keys.clientKey);
	}
}
