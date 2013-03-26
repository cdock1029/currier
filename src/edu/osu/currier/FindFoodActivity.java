package edu.osu.currier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Build;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.Address;
import android.location.LocationListener;
import android.location.LocationManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



public class FindFoodActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener, OnMapClickListener, LocationListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static final String ACT = "FindFoodActivity";
	
	final int RQS_GooglePlayServices = 1;
	LocationManager locationManager;
	double latitude;
	double longitude;
	GoogleMap map;
	
	private FragmentManager fragmentManager = getFragmentManager();
    private FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    
    //A sample list of addresses for a ListFragment
    static String[] addresses = new String[] {
    	"252 East Maynard Avenue Columbus Ohio 43202",
    	"30 East Lane Avenue Columbus Ohio 43210"
    };
    
    //The List to contain names for sellers
    List<String> sellerName = new ArrayList<String>();
    //The list to contain address for sellers
    List<String> sellerAddress = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {
			// fill out the view
			setContentView(R.layout.activity_find_food);
			
			// Set up the action bar to show a dropdown list.
			final ActionBar actionBar = getActionBar();
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			actionBar.setDisplayHomeAsUpEnabled(true);

			// Set up the dropdown list navigation in the action bar.
			actionBar.setListNavigationCallbacks(
			// Specify a SpinnerAdapter to populate the dropdown list.
					new ArrayAdapter<String>(getActionBarThemedContextCompat(),
							android.R.layout.simple_list_item_1,
							android.R.id.text1, new String[] {
									getString(R.string.title_section1),
									getString(R.string.title_section2),
									getString(R.string.title_section3), }), this);
			
		} else {
			// show the signup or login screen
			Intent login = new Intent(getApplicationContext(), LoginActivity.class);
			// look for LoginActivity already running and clear out Act's above it.
			login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(login);
			//close FindFood screen
			finish();
		}

		Log.d(ACT,"Finishing onCreate...");
	}

	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_find_food, menu);
		return result;
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		FragmentManager fragmentManager = getFragmentManager();
	    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	    
		switch(position){
		//Dummy scenario
		case 0:		
			fragmentManager.popBackStack();
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			fragmentTransaction.add(R.id.container, fragment);
		    fragmentTransaction.addToBackStack(null);
		    fragmentTransaction.commit();
			return true;
		//Display the neighborhood map
		case 1:
			fragmentManager.popBackStack();
			final MapFragment fullMapFragment = new MapFragment();
		    fragmentTransaction.add(R.id.fullMapView, fullMapFragment);
		    fragmentTransaction.addToBackStack(null);
		    fragmentTransaction.commit();
		    
		    moveMap(fullMapFragment);
			return true;
		//Display the neighborhood map on top of screen, list of addresses on bottom half of screen
		case 2:
			fragmentManager.popBackStack();
			
			//Set the content for the list fragment
			final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,sellerAddress) {
				//Sets the text color of the ListFragment to BLACK
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					TextView textView = (TextView) super.getView(position, convertView, parent);
					textView.setTextColor(android.graphics.Color.BLACK);
					return textView;
				}
			};
			
			//Grab the sellers' names and addresses from Parse
			final ParseQuery query = new ParseQuery("Seller");

			query.findInBackground(new FindCallback() {
				public void done(List<ParseObject> objects, ParseException e) {
					if (e == null) {
						System.out.println("Successful retrieval from server!");
						for(ParseObject seller: objects){
							sellerAddress.add(seller.getString("Address"));
							sellerName.add(seller.getString("publicName"));
						}
						adapter.notifyDataSetChanged();
					} else {
						System.out.println("Whoopsie daisy, problem with the Parse server!");
					}
				}
			});
			
			ListFragment listFragment = new ListFragment() {
				//On clicking an item in the ListFragment, moves map to item's address
				@Override
				public void onListItemClick(ListView l, View v, int position, long id) {
					List<Address> address = null;
					try {
						address = new Geocoder(FindFoodActivity.this, Locale.ENGLISH).getFromLocationName(sellerAddress.get(position), 1);
						double lat = address.get(0).getLatitude();
						double lon = address.get(0).getLongitude();
						map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15));

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			listFragment.setListAdapter(adapter);
			
			final MapFragment partialMapFragment = new MapFragment();
			
			fragmentTransaction.add(R.id.mapComponent, partialMapFragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.add(R.id.list, listFragment);
		    fragmentTransaction.addToBackStack(null);
		    fragmentTransaction.commit();
		    
		    moveMap(partialMapFragment);	    
			return true;
		default:
			return true;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Handle selected option item
		switch (item.getItemId()) {
		case R.id.menu_logout:
			logout();
			return true;
		case R.id.menu_items:
			startActivity(new Intent(getApplicationContext(), SellerMenuExpandableListActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}

	/**
	 * Method that's fired when logout option item is pressed.
	 */
	private void logout() {
		//logout of parse
		ParseUser.logOut();
		// show the signup or login screen
		startActivity(new Intent(getApplicationContext(), LoginActivity.class));
		finish();
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Create a new TextView and set its text to the fragment's section
			// number argument value.
			TextView textView = new TextView(getActivity());
			textView.setGravity(Gravity.CENTER);
			textView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return textView;
		}
	}
	
	//Moves the provided MapFragment to the user's current location, placing a marker at the user's location
	public void moveMap(final MapFragment mapFragment){
		//Start the location manager and get the user's most current (lat, long)
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, this);
	    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    if(location!=null){
	    	latitude = location.getLatitude();
	    	longitude = location.getLongitude();
	    }
	    //Ensure the MapFragment has initialized via Timer, move the map to the (lat, long) from LocationManager
	    final Timer timer = new Timer();
	    timer.schedule(new TimerTask() {
	    	@Override
	    	public void run() {
	    		if(mapFragment != null && mapFragment.isVisible()) {
	    			timer.cancel();
	    			runOnUiThread(new Runnable() {
	    				@Override
	    				public void run() {
	    					map = mapFragment.getMap();
	    					if(map!=null) {
	    						map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
	    						map.addMarker(new MarkerOptions()
	    						.position(new LatLng(latitude,longitude))
	    						.title("User's Location"));
	    						
	    						//Adds markers for all the possible houses to order from
	    						//Grabs the (lat, lon) from the String "location"
	    						for(String location : sellerAddress){
	    							List<Address> address = null;
	    							try {
										address = new Geocoder(FindFoodActivity.this, Locale.ENGLISH).getFromLocationName(location, 1);
										double lat = address.get(0).getLatitude();
										double lon = address.get(0).getLongitude();
										map.addMarker(new MarkerOptions()
										.position(new LatLng(lat, lon))
										.title(location));
									} catch (IOException e) {
										e.printStackTrace();
									}
									
	    						}
	    					}
	    					
	    				}
	    			});
	    		}
	    	}
	    }, 0, 200);	    
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	private LocationListener locationListener = new LocationListener(){
		@Override
		public void onLocationChanged(Location arg0){
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
		
		@Override
		public void onGpsStatusChanged(int event) {
			// TODO Auto-generated method stub
			
		}
	};

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub
		
	}

}
