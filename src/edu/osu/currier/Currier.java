package edu.osu.currier;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Application;
import android.util.Log;

public class Currier extends Application {
	@Override
	public void onCreate() {
		// parse initialization
		Parse.initialize(this, Configurator.keys.applicationId, Configurator.keys.clientKey);
	}
}
