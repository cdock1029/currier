/**
 * Displays listing of menu items for the selected Seller that brought you to
 * this activity.  Menu titles are the headings, and the child items are specific items for that menu category.
 * Selecting multiple items will highlight them, and selected the Add to Cart button will consolidate these items to a Shopping cart.
 * 
 * *** There is a glitch that I sometimes had trouble with. When items are hidden in a collapsed menu, and other items are selected, sometimes the hidden
 * *** items colors get changed (but they aren't "selected" for the order....when you touch them again their color doesn't change because they are NOW selected and added to order")
 * *** This has something to do with the "re-use" of view-objects.  Since I'm modifying the background color of selected view, sometimes the "off-screen" or hidden view(s)
 * *** color gets changed too. If the menu's are expanded first, then desired items are selected..it doesn't seem to be so glitchy and unwanted items don't have their colors changed.  
 * ***
 * *** Edit: I've "locked" the collapsible list open, on activity start (it can be collpased still).. this might improve things.
 */
package edu.osu.currier;
import java.text.NumberFormat;
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
import edu.osu.currier.library.HelperFunctions;


import android.app.Dialog;
import android.app.ExpandableListActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;


public class SellerMenuActivity extends ExpandableListActivity {
	private Context context = null;
	ExpandableListAdapter mAdapter;
	ExpandableListView elv;
	private final static String[] grpKeys = new String[] {"MENU"};
	private final static String[] chKeys = new String[] {"NAME","DESC","PRICE"};
	private static final String TAG = "MENU";
	private static Drawable background;
	private List<ParseObject> menuItems;
	private Dialog progressDialog;

	private static int SEL;
	private Color sel = new Color();
	private static int UNSEL; 
	private Button orderButton;
	private Map<String, String[]> selections = new HashMap<String, String[]>();
	Money money = Money.parse(HelperFunctions.country.Locality + "0.00");
	private String sellerId = "3djgQh1VTZ";
	private String sellerName = "Best in Town";
	//format currenty correctly
	static NumberFormat nf = NumberFormat.getCurrencyInstance(HelperFunctions.country.LOC);
	
	/**
	 * This is a list of maps that each have one key, "MENU", mapped to the name of the menu
	 */
	List<Map<String,String>> groupDataX;
	
	/**
	 * For Each menu type, there is a list of maps, with keys from chKeys, and values corresonding to data from Parse.com DB.
	 */
	List<List<Map<String,String>>> childDataX;
	
	/**
	 * Remote class for accessing the database to populate the menu list.
	 * @author conordockry
	 *
	 */
	private class RemoteDataTask extends AsyncTask<Void,Void,Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			//We need to "join" some data from multiple tables from Parse..3 tables: Seller, Menu, and MenuItem.
			// passed in from FindfoodActivity.. here we start building it.
			ParseQuery innerInnerSellerQuery = new ParseQuery("Seller");
			
			//this will hold the seller object once we get it
			ParseObject sell;
			try {
				//This makes a direct call to the database...we're in a background thread so we're ok.
				//This call...and then the find at the end are why the try/catch block is needed.
				sell = innerInnerSellerQuery.get(sellerId);
				
				//This is what we really need more than the seller...
				ParseQuery innerMenuQuery = new ParseQuery("Menu");
				
				//we want the Menu(s) that correspond to the seller that was sellected...
				innerMenuQuery.whereEqualTo("seller", sell);
				
				//the 3rd part of our composite query... 
				ParseQuery query = new ParseQuery("MenuItems");
				
				// We constrain our search of items to those which belong to the menus...(that belong to the seller)
				query.whereMatchesKeyInQuery("menu","objectId",innerMenuQuery);
				
				// just a parse API necessity.. it doesn't actually get nested query objects unless you include the "foreign key" fields.
				query.include("menu");
				
				// Actually makes request to the Parse web service here.. This blocks but we're in a background thread.
				menuItems = query.find();
				
			} catch (ParseException e1) {
				Log.d(TAG, "Parse: " + e1.getMessage());
				//finish();
			}
			
			groupDataX = new ArrayList<Map<String,String>>();
			childDataX = new ArrayList<List<Map<String,String>>>();
			Map<String, List<Map<String,String>>> temp = new LinkedHashMap<String, List<Map<String,String>>>();
			Set<String> menuSet = new HashSet<String>(4); //Set of menu names.

			//Get all unique menu names.
			for (ParseObject menuItem : menuItems) {
				ParseObject menu = menuItem.getParseObject("menu");
				String menuTitle = menu.getString("title");
				if (menuSet.contains(menuTitle) == false) {
					menuSet.add(menuTitle);
					Log.d(TAG, "Parse: menu title - " + menuTitle);
					Map<String, String> grp = new HashMap<String, String>(1);
					grp.put(grpKeys[0], menuTitle); // define each menu
					groupDataX.add(grp); // put menu in group array

					temp.put(menuTitle, new ArrayList<Map<String,String>>());
				}
				Map<String, String> itemParts = new HashMap<String,String>();
				itemParts.put(chKeys[0], menuItem.getString("name"));
				itemParts.put(chKeys[1], menuItem.getString("description"));
				itemParts.put(chKeys[2], nf.format(menuItem.getDouble("price")));
				
				//keep track of objectId too!!
				itemParts.put("ID", menuItem.getObjectId());
				temp.get(menuTitle).add(itemParts);
			}
			for (Map.Entry<String, List<Map<String,String>>> entry : temp.entrySet()) {
				childDataX.add(entry.getValue());
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
			expandList();
			SellerMenuActivity.this.progressDialog.dismiss();
		}
	}
	

	@Override
	public void onResume() {

		
		super.onResume();
	}

	//Used to lock open the collapsible list view..seems to be glitchy if it is collapsed and items in
	//menus are selected.
	public void expandList() {
		if (mAdapter != null) {
			elv = getExpandableListView();
			int count = mAdapter.getGroupCount();
			for (int i = 0; i < count; i++) {
				elv.expandGroup(i);
			}
		} else {
			Toast.makeText(getApplicationContext(), "mAdapter is null :(", Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = SellerMenuActivity.this;
		elv = getExpandableListView();
		

		// set View background image
		background = getResources().getDrawable(R.drawable.tiles);
		elv.setBackgroundDrawable(background);

		// add footer (contains the button) to listview
		View footerView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.menu_footer,null,false);
		elv.addFooterView(footerView);

		// Get the seller data from FindFoodActivity
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			sellerId = extras.getString("objectId");
			sellerName = extras.getString("sellerName");
		}
		setTitle(sellerName);
		
		//Make the button and add the listener to it
		setUpOrderButton();
		
		//The colors for unselected and selected list items.
		SEL = getResources().getColor(R.color.menu_background_selected);
		UNSEL = getResources().getColor(R.color.menu_background_unselected);
		
		//Execute background task to fetch data from parse database
		new RemoteDataTask().execute();
	}

	private void setUpOrderButton() {
		orderButton = (Button)this.findViewById(R.id.add_cart_button);
		orderButton.getBackground().setAlpha(255);
		orderButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ArrayList<String> _names = new ArrayList<String>();
				ArrayList<String> _prices = new ArrayList<String>();

				Intent i = new Intent(SellerMenuActivity.this, CartActivity.class);
				i.putStringArrayListExtra("ids", new ArrayList<String>(selections.keySet()));
				for (Entry<String, String[]> entry : selections.entrySet()) {
					money = money.plus(Money.parse(HelperFunctions.country.Locality + entry.getValue()[1]));
					//Log.d("MENU", "Money during submit: " + money.getAmount());
					_prices.add(entry.getValue()[1]);
					_names.add(entry.getValue()[0]);
				}
				i.putStringArrayListExtra("names", _names);
				i.putStringArrayListExtra("prices", _prices);
				i.putExtra("total", money.getAmount().toString());
				i.putExtra("sellerId", sellerId);
				startActivity(i);	
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition,
			long id) {
		
		String oid = (String)((Map<String,String>)mAdapter.getChild(groupPosition, childPosition)).get("ID");
		
		//Toggles color of listitems and also adds/removes from map used to keep track of "whats in cart"
		//if checkbox is selected, deselect/remove
		if (selections.containsKey(oid)) {
			selections.remove(oid);
			v.setBackgroundColor(UNSEL);
		} else {
			//else add..
			String name = (String)((Map<String,String>)mAdapter.getChild(groupPosition, childPosition)).get("NAME");
			String price = ((String)((Map<String,String>)mAdapter.getChild(groupPosition, childPosition)).get("PRICE")).substring(1);
			selections.put(oid, new String[] {name,price});
			v.setBackgroundColor(SEL);
		}
		parent.requestLayout();
		return super.onChildClick(parent, v, groupPosition, childPosition, id);
	}



}
