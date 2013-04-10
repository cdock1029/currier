package edu.osu.currier;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.money.Money;


import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ExpandableListActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;


public class SellerMenuActivity extends ExpandableListActivity {
	
	private Context context = null;
	ExpandableListAdapter mAdapter;
	ExpandableListView elv;
	private static Drawable background;
	private List<ParseObject> menuItems;
	private Dialog progressDialog;
	
	private List<String> pricesSelected;
	private List<String> itemsSelected;
	
	private Map<String, String[]> selections = new HashMap<String, String[]>();
	Money money = Money.parse("USD 0.00");
	
	private String sellerId = "3djgQh1VTZ";

	private class RemoteDataTask extends AsyncTask<Void,Void,Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			ParseQuery innerInnerSellerQuery = new ParseQuery("Seller");
			//innerInnerSellerQuery.whereEqualTo("objectId", "9ML249tNNY");
			//innerInnerSellerQuery.whereEqualTo("objectId", "3djgQh1VTZ");
			innerInnerSellerQuery.whereEqualTo("objectId", sellerId);
			
			ParseQuery innerMenuQuery = new ParseQuery("Menu");
			innerMenuQuery.whereMatchesQuery("seller", innerInnerSellerQuery);
			
			ParseQuery query = new ParseQuery("MenuItems");
			query.whereMatchesQuery("menu", innerMenuQuery);
			query.include("menu.seller");
			
			try {
				menuItems = query.find();
			} catch (ParseException e) {
				Log.d("parse", "SMELA: " + e.getMessage());
			}
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			SellerMenuActivity.this.progressDialog = ProgressDialog.show(SellerMenuActivity.this, "",
					"Loading...", true);
			super.onPreExecute();
		}
		
		@Override
		protected void onPostExecute(Void result) {
			List<Map<String,String>> groupDataX = new ArrayList<Map<String,String>>();
			List<List<Map<String,String>>> childDataX = new ArrayList<List<Map<String,String>>>();
			Map<String, List<Map<String,String>>> temp = new LinkedHashMap<String, List<Map<String,String>>>();
			final String[] grpKeys = new String[] {"MENU"};
			final String[] chKeys = new String[] {"NAME","DESC","PRICE"};
			
			Set<String> menuSet = new HashSet<String>(4); //Set of menu names.
			Log.d("parse", "SMELA: seller name: " + menuItems.get(0).getParseObject("menu").getParseObject("seller").getString("publicName"));
			//Get all unique menu names.
			for (ParseObject menuItem : menuItems) {
				ParseObject menu = menuItem.getParseObject("menu");
				String menuTitle = menu.getString("title");
				if (menuSet.contains(menuTitle) == false) {
					menuSet.add(menuTitle);
					Log.d("parse", "menu title: " + menuTitle);
					Map<String, String> grp = new HashMap<String, String>(1);
					grp.put(grpKeys[0], menuTitle); // define each menu
					groupDataX.add(grp); // put menu in group array
	
					temp.put(menuTitle, new ArrayList<Map<String,String>>());
				}
				Map<String, String> itemParts = new HashMap<String,String>();
				itemParts.put(chKeys[0], menuItem.getString("name"));
				itemParts.put(chKeys[1], menuItem.getString("description"));
				itemParts.put(chKeys[2], String.valueOf(menuItem.getDouble("price")));
				//keep track of objectId too!!
				Log.d("MENU", "objectId : " + menuItem.getObjectId());
				itemParts.put("ID", menuItem.getObjectId());
				temp.get(menuTitle).add(itemParts);
			}
			for (Map.Entry<String, List<Map<String,String>>> entry : temp.entrySet()) {
				childDataX.add(entry.getValue());
			}
			mAdapter = new SimpleExpandableListAdapter(
					SellerMenuActivity.this,			//context
					groupDataX,										//list of maps
					android.R.layout.simple_expandable_list_item_1,	//layout for groups
					grpKeys,										//groupFrom list of keys fetched from map ass w/ each grp
					new int[] { android.R.id.text1},				//groupTo group views that dsiplay the collumn listed in groupFrom
					childDataX,										//list of list of maps. outer list has entries corresponding to group.	
																	//inner list has entries wich are child of group. map has data for a child specified
																	// in childFrom. 
					R.layout.menu_item,								//childLayout: 
					chKeys,
					new int[] { R.id.itemName,R.id.itemDescr,R.id.itemPrice }
					);
			
			
			setListAdapter(mAdapter);
			SellerMenuActivity.this.progressDialog.dismiss();
		}
	}
	
	
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = SellerMenuActivity.this;
		elv = getExpandableListView();
		
		background = getResources().getDrawable(R.drawable.tiles);
		elv.setBackgroundDrawable(background);
		View footerView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.cart_footer,null,false);
		elv.addFooterView(footerView);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			sellerId = extras.getString("objectId");
		}
		
		
		Button orderButton = (Button)this.findViewById(R.id.submit_order_button);
		//Button orderButton = new Button(context);
		//orderButton.setText(R.string.add_cart);
		
		orderButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ArrayList<String> _names = new ArrayList<String>();
				ArrayList<String> _prices = new ArrayList<String>();
				
				Intent i = new Intent(SellerMenuActivity.this, CartActivity.class);
				i.putStringArrayListExtra("ids", new ArrayList<String>(selections.keySet()));
				for (Entry<String, String[]> entry : selections.entrySet()) {
					money = money.plus(Money.parse("USD " + entry.getValue()[1]));
					//Log.d("MENU", "Money during submit: " + money.getAmount());
					_prices.add(entry.getValue()[1]);
					_names.add(entry.getValue()[0]);
				}
				i.putStringArrayListExtra("names", _names);
				i.putStringArrayListExtra("prices", _prices);
				i.putExtra("total", money.getAmount().toString());
				startActivity(i);
				
			}
		});
		
		new RemoteDataTask().execute();
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition,
			long id) {
		super.onChildClick(parent, v, groupPosition, childPosition, id);
		String oid = (String)((Map<String,String>)mAdapter.getChild(groupPosition, childPosition)).get("ID");
		
		
		//if checkbox is selected, deselect/remove
				
		//selections.remove(oid);
		//else add..
		String name = (String)((Map<String,String>)mAdapter.getChild(groupPosition, childPosition)).get("NAME");
		String price = (String)((Map<String,String>)mAdapter.getChild(groupPosition, childPosition)).get("PRICE");
		
		selections.put(oid, new String[] {name,price});
		
		
		Toast.makeText(SellerMenuActivity.this, name + " " + price + " " + oid,Toast.LENGTH_SHORT).show();
		
		return true;
	}

	

}
