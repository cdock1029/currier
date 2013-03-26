package edu.osu.currier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.money.Money;

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
	private static final String NAME = "NAME";
	private static final String IS_EVEN = "IS_EVEN";
	ExpandableListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<Map<String,String>> groupData = new ArrayList<Map<String,String>>();
		List<List<Map<String,String>>> childData = new ArrayList<List<Map<String,String>>>();

		/*
		ParseQuery query = new ParseQuery("Menu");

		String[] menus = {"Meat", "Vegetarian"};
		String[] items = {};

		query.whereEqualTo("sellerId", "9ML249tNNY");
		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// set list headings to menu names


				// query for items within each menu



			}
		});

		 */

		for (int i = 0; i < 3; i++) {
			Map<String, String> curGroupMap = new HashMap<String, String>();
			groupData.add(curGroupMap);
			curGroupMap.put(NAME, "Item " + i);
			curGroupMap.put(IS_EVEN, (i % 2 == 0) ? "This group is even" : "This group is odd");

			List<Map<String, String>> children = new ArrayList<Map<String, String>>();
			for (int j = 0; j < 6; j++) {
				Map<String, String> curChildMap = new HashMap<String, String>();
				children.add(curChildMap);
				// curChildMap.put(NAME, "Child " + j);
				Money money = Money.parse("USD " + j + ".00");
				MenuItem item = new MenuItem("item", "this is an item", money);
				curChildMap.put(IS_EVEN, (j % 2 == 0) ? "Hello " + j: "Good Morning "+ j);
				//curChildMap.put("some string", item.getDescription());
			}
			childData.add(children);
		}

		// Set up our adapter
		mAdapter = new SimpleExpandableListAdapter(
				this,
				groupData,
				android.R.layout.simple_expandable_list_item_1,
				new String[] { NAME, IS_EVEN },
				new int[] { android.R.id.text1, android.R.id.text2 },
				childData,
				android.R.layout.simple_expandable_list_item_2,
				new String[] { NAME, IS_EVEN },
				new int[] { android.R.id.text1, android.R.id.text2 }
				);
		setListAdapter(mAdapter);

	}

	


}
