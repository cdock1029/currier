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
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;


public class SellerMenuExpandableListActivity extends ExpandableListActivity {
	private static final String NAME = "NAME";
	private static final String IS_EVEN = "IS_EVEN";
	ExpandableListAdapter mAdapter;
	private String seller;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<Map<String,String>> groupData = new ArrayList<Map<String,String>>();
		List<List<Map<String,String>>> childData = new ArrayList<List<Map<String,String>>>();
		
		List<Map<String,String>> groupDataX = new ArrayList<Map<String,String>>();
		List<List<Map<String,String>>> childDataX = new ArrayList<List<Map<String,String>>>();

		ParseQuery innerInnerSellerQuery = new ParseQuery("Seller");
		innerInnerSellerQuery.whereEqualTo("objectId", "3djgQh1VTZ");
		
		ParseQuery innerMenuQuery = new ParseQuery("Menu");
		innerMenuQuery.whereMatchesQuery("seller", innerInnerSellerQuery);
		
		ParseQuery query = new ParseQuery("MenuItems");
		query.whereMatchesQuery("menu", innerMenuQuery);
		query.include("menu.seller");
		
		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> itemsList, ParseException e) {
				// set list headings to menu names
				if (e == null) {
					Log.d("parse", "Success");
					seller = itemsList.get(0).getParseObject("menu").getParseObject("seller").getString("publicName");
					Log.d("parse", "seller name: " + seller);
					for (ParseObject menu : itemsList) {
						Map<String, String> grp = new HashMap<String, String>(1);
						grp.put("MENU", menu.getString("title"));
					}
				} else {
					Log.d("parse", e.getMessage());
				}

				// query for items within each menu

			}
		});

	
		
		
		Map<String, String> grp1 = new HashMap<String,String>(1);
		Map<String, String> grp2 = new HashMap<String,String>(1);
		Map<String, String> grp3 = new HashMap<String,String>(1);
		grp1.put("MENU", "Breakfast");
		grp2.put("MENU", "Lunch");
		grp3.put("MENU", "Dinner");
		groupDataX.add(grp1);
		groupDataX.add(grp2);
		groupDataX.add(grp3);
		
		
		Map<String, String> ch1 = new HashMap<String,String>(3);
		Map<String, String> ch2 = new HashMap<String,String>(3);
		Map<String, String> ch3 = new HashMap<String,String>(3);
		Map<String, String> ch4 = new HashMap<String,String>(3);
		
		
		ch1.put("DESC", "Really goud chicken soup");
		ch1.put("PRICE", "5.95");
		ch1.put("TITLE", "Chicken Soup");
		
		
		ch2.put("TITLE", "Hamburger");
		ch2.put("DESC", "Big juicy burger");
		ch2.put("PRICE", "6.00");
		
		ch3.put("TITLE", "Steak");
		ch3.put("DESC", "Filet Mignon steak cooked to order");
		ch3.put("PRICE", "19.95");
		
		ch4.put("TITLE", "Omellete");
		ch4.put("DESC", "Western Omellete, 3 eggs");
		ch4.put("PRICE", "5.15");
		
		String[] grpKeys = new String[] {"MENU"};
		String[] chKeys = new String[] {"TITLE","DESC","PRICE"};
		
		List<Map<String,String>> c1 = new ArrayList<Map<String,String>>();
		List<Map<String,String>> c2 = new ArrayList<Map<String,String>>();
		List<Map<String,String>> c3 = new ArrayList<Map<String,String>>();
		
		c1.add(ch4);
		c2.add(ch1);
		c2.add(ch2);
		c3.add(ch3);
		
		childDataX.add(c1);
		childDataX.add(c2);
		childDataX.add(c3);
		
		

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
				//Money money = Money.parse("USD " + j + ".00");
				//MenuItem item = new MenuItem("item", "this is an item", money);
				curChildMap.put(IS_EVEN, (j % 2 == 0) ? "Hello " + j: "Good Morning "+ j);
				//curChildMap.put("some string", item.getDescription());
			}
			childData.add(children);
		}

		// Set up our adapter
		/*
		mAdapter = new SimpleExpandableListAdapter(
				this,													//context
				groupData,												//list of maps
				android.R.layout.simple_expandable_list_item_1,			//layout for groups
				new String[] { NAME, IS_EVEN },							//groupFrom list of keys fetched from map ass w/ each grp
				new int[] { android.R.id.text1, android.R.id.text2 },	//groupTo group views that dsiplay the collumn listed in groupFrom
				childData,												//list of list of maps. outer list has entries corresponding to group.	
																		//inner list has entries wich are child of group. map has data for a child specified
																		// in childFrom. 
				android.R.layout.simple_expandable_list_item_2,			//childLayout: 
				new String[] { NAME, IS_EVEN },
				new int[] { android.R.id.text1, android.R.id.text2 }
				);
		*/
		
		
		mAdapter = new SimpleExpandableListAdapter(
				this,													//context
				groupDataX,												//list of maps
				android.R.layout.simple_expandable_list_item_1,			//layout for groups
				grpKeys,							//groupFrom list of keys fetched from map ass w/ each grp
				new int[] { android.R.id.text1},	//groupTo group views that dsiplay the collumn listed in groupFrom
				childDataX,												//list of list of maps. outer list has entries corresponding to group.	
																		//inner list has entries wich are child of group. map has data for a child specified
																		// in childFrom. 
				R.layout.menu_item,			//childLayout: 
				chKeys,
				new int[] { R.id.itemName,R.id.itemDescr,R.id.itemPrice }
				);
		
		
		
		setListAdapter(mAdapter);

	}
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition,
			long id) {
		
		Toast.makeText(SellerMenuExpandableListActivity.this, "Clicked On Child",
			    Toast.LENGTH_SHORT).show();
		return true;
		
	}

	


}
