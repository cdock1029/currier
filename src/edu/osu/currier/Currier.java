package edu.osu.currier;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Application;
import android.util.Log;

public class Currier extends Application {
	//static ParseObject o;
	//static ArrayList<ParseObject> l = new ArrayList<ParseObject>();
	@Override
	public void onCreate() {
		// parse initialization
		Parse.initialize(this, Configurator.keys.applicationId, Configurator.keys.clientKey);
//		
//		ParseQuery q = new ParseQuery("MenuItems");
//		q.whereEqualTo("menuId", "A3H38rwYcA");
//		q.findInBackground(new FindCallback() {
//
//			@Override
//			public void done(List<ParseObject> objects, ParseException e) {
//				if (e == null) {
//					Log.d("menuItem", "retrieved " + objects.size() + " menu item(s)");
//					//o = objects.get(0);
//					for (ParseObject p : objects) {
//						Currier.l.add(p);
//					}
//					Currier.use();
//				}
//				
//			}
//			
//		});
		
	}
//	protected static void use() {
//		for (ParseObject p : Currier.l) {
//			p.put("menu", ParseObject.createWithoutData("Menu", p.getString("menuId")));
//			p.saveInBackground();
//		}
//		
//		
//	}

}
