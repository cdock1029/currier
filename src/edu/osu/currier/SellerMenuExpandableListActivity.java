package edu.osu.currier;
import java.util.List;
import java.util.Map;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.ExpandableListActivity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.SimpleExpandableListAdapter;


public class SellerMenuExpandableListActivity extends ExpandableListActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExpandableListAdapter mAdapter;
		ParseQuery query = new ParseQuery("Menu");
		
		String[] menus = {"Meat", "Vegetarian"};
		String[] items = {};
		
//		query.whereEqualTo("sellerId", "9ML249tNNY");
//		query.findInBackground(new FindCallback() {
//			@Override
//			public void done(List<ParseObject> objects, ParseException e) {
//				// set list headings to menu names
//				
//				
//				// query for items within each menu
//				
//				
//				
//			}
//		});
		
		mAdapter = new SimpleExpandableListAdapter(this, createGroupList(), android.R.layout.simple_expandable_list_item_1, items, null, null, 0, items, null);
		
		
		
		
	}

	private List<? extends Map<String, ?>> createGroupList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
