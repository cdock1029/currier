package edu.osu.currier;

import java.util.Iterator;
import java.util.List;

import com.parse.*;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ListSellers extends Activity {

	private static final String TAG = "ListSellers";
	protected static final String EXTRA_SELLER_NAME = "SELLER_NAME";
	protected static final String EXTRA_RATING = "SELLER_RATING";
	public static ParseUser mUserId = null;
	public static ParseObject mSeller = null;

	TableRow[] tr;
	TextView[] names;
	RatingBar[] ratings;

	float[] mRating;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_sellers);

		// set the application id and client key for the parse database
		Parse.initialize(this, "mGgQCNePMku0rAuWaV8vIpJJ2Qd4PrzmzTnxzdB8",
				"WmYOeKOFUh7v3UVk6SKICi7md3Wx0FfFsU69NgMa");

		getUserId();

		getSellersData();

		addListenerOnButton();

	}

	private void addListenerOnButton() {

	}

	private void getSellersData() {
		// final LinearLayout listSellersLayout = (LinearLayout)
		// findViewById(R.id.list_sellers);
		Log.d(TAG, "Attempting to create the seller table now...");

		final TableLayout sellerTable = (TableLayout) findViewById(R.id.sellerTable);

		ParseQuery query = new ParseQuery("Seller");
		query.findInBackground(new FindCallback() {
			public void done(List<ParseObject> sellers, ParseException e) {
				if (e == null) {
					try {

						tr = new TableRow[sellers.size()];
						names = new TextView[sellers.size()];
						ratings = new RatingBar[sellers.size()];
						mRating = new float[sellers.size()];

						TextView hello = (TextView) findViewById(R.id.hello);
						hello.setText("Your nearby sellers:");

						for (int i = 0; i < sellers.size(); i++) {

							tr[i] = new TableRow(getApplicationContext());
							names[i] = new TextView(getApplicationContext());
							ratings[i] = new RatingBar(getApplicationContext());
						}

						Log.d(TAG, "Size = " + sellers.size());

						Iterator<ParseObject> it = sellers.iterator();
						int i = 0;
						while (it.hasNext()) {
							// 1) For each seller
							ParseObject s = it.next();
							// mSellerId = s.getObjectId();

							// 2) Set a TextView to its publicName
							String publicName = (String) s.get("publicName");
							names[i].setText(publicName);
							Log.d(TAG, "name view created -- " + publicName);

							// 3) Set a RatingBar to its rating
							mRating[i] = (float) s.getDouble("avgRating");
							float r = mRating[i];
							Log.d(TAG, "Rating " + r + " recd.");

							ratings[i].setRating(r);
							// ratings[i].setIsIndicator(true);
							Log.d(TAG, "Rating " + r + " set");

							// 4) Add the name and rating to the table row
							tr[i].addView(names[i]);
							tr[i].addView(ratings[i]);

							addListenerOnRatingBar(i, ratings[i]);

							// 4) Add this nameView on sellerLayouts[i]
							// sellerLayouts[i].addView(publicNameView);
							Log.d(TAG, "name & rating added to tr[" + i + "]");

							// n) Add this tr[i] to the listSellersLayout
							sellerTable.addView(tr[i]);
							Log.d(TAG, "tr[" + i + "]"
									+ " added to sellerTable");

							i++;
						}
					} catch (Exception e1) {
						Log.d(TAG, e1.getMessage());
					}
				} else {
					Log.d(TAG, e.getMessage());
				}
			}
		});
	}

	protected void addListenerOnRatingBar(final int i, RatingBar ratingBar) {

		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {

				// ParseQuery q = new ParseQuery("Seller");
				// q.whereEqualTo("publicName", names[i].getText().toString());
				// q.findInBackground(new FindCallback() {
				//
				// public void done(List<ParseObject> list, ParseException e) {
				// try {
				// if (e == null) {
				// if (list.size() == 1) {
				// mSeller = list.get(0);
				// Log.d(TAG,
				// "Seller: Retrieved "
				// + mSeller.get("publicName"));
				// } else {
				// Log.d(TAG, "more than 1 match");
				// }
				// } else {
				// Log.d(TAG, "Error: " + e.getMessage());
				// }
				//
				// } catch (Exception e1) {
				// Log.d(TAG, e1.getMessage());
				// }
				// }
				// });
				//
				// if (mSeller == null) {
				// Log.d(TAG, "mSeller = null when not expected");
				// } else {
				// Toast.makeText(
				// ListSellers.this,
				// "This should now take us to RateReview Activity "
				// + String.valueOf(ratingBar.getRating()),
				// Toast.LENGTH_LONG).show();

				Intent intent = new Intent(ListSellers.this, RateReview.class);
				String sellerName = names[i].getText().toString();
				intent.putExtra(EXTRA_SELLER_NAME, sellerName);
				intent.putExtra(EXTRA_RATING, mRating[i]);
				startActivity(intent);
				// }
				// }
			}
		});
	}

	private void getUserId() {
		// hard coded user for now
		try {
			mUserId = ParseUser.logIn("newuser@gmail.com", "abc");
			Log.d(TAG, "userid is fine");
		} catch (ParseException e) {
			Log.d(TAG, e.getMessage());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_sellers, menu);
		return true;
	}

}
