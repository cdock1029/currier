package edu.osu.currier;

import com.parse.Parse;

import android.app.Application;

public class Currier extends Application {
	@Override
	public void onCreate() {
		// parse initialization
		Parse.initialize(this, Configurator.keys.applicationId, Configurator.keys.clientKey);
	}

}
