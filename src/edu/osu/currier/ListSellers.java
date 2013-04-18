package edu.osu.currier;

import java.util.Iterator;
import java.util.List;

import com.parse.*;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

/* This activity lists all the sellers and their ratings.
 * It then waits for the user to click on the ratings of a seller.
 * This then initiates a new activity where the user can actually provide his review. 
 */
public class ListSellers extends Activity {

	private static final String TAG = "ListSellers";

	public static ParseObject mSeller = null;

	public static ParseObject[] mSellerList; // the list of all the sellers
	RatingBar[] rbSellers; // for their ratings

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_sellers);

		showSellersData();
	}

	private void showSellersData() {

		// fetch the TableLayout from the layout
		final LinearLayout lloSellers = (LinearLayout) findViewById(R.id.lloSellers);

		ParseQuery query = new ParseQuery("Seller");

		query.findInBackground(new FindCallback() {
			public void done(List<ParseObject> sellers, ParseException e) {
				if (e == null) {
					try {

						int numSellers = sellers.size();
						rbSellers = new RatingBar[numSellers];
						mSellerList = new ParseObject[numSellers];

						// iterate over each seller
						Iterator<ParseObject> it = sellers.iterator();
						int i = 0;
						while (it.hasNext()) {
							// 1) For each seller, Make a note in the global
							// array
							ParseObject s = it.next();
							mSellerList[i] = s;

							// 2) Set a TextView to its publicName
							String publicName = (String) s.get("publicName");
							TextView txtSellerName = new TextView(
									getApplicationContext());
							txtSellerName.setText(publicName);
							txtSellerName.setTextColor(Color
									.parseColor("#000000"));
							txtSellerName.setTextSize(
									TypedValue.COMPLEX_UNIT_SP, 18);

							// 3) Set a RatingBar to its rating
							float r = (float) s.getDouble("avgRating");
							rbSellers[i] = new RatingBar(
									getApplicationContext());
							rbSellers[i].setRating(r);
							rbSellers[i].setNumStars(5);
							LayoutParams lp = new LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT);
							rbSellers[i].setLayoutParams(lp);
							addListenerOnRatingBar(i, rbSellers[i]);
							Log.d(TAG, "Rating " + r + " set");

							// 4) Add the name and rating to the table row
							// tr[i] = new TableRow(getApplicationContext());
							// tr[i].addView(txtNames[i]);
							// tr[i].addView(rbSellers[i]);

							// 5) Add this table row to the table layout
							lloSellers.addView(txtSellerName);
							lloSellers.addView(rbSellers[i]);
							Log.d(TAG, "tr[" + i + "]" + " added to lloSellers");

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
				try {
					Intent rateReview = new Intent(getApplicationContext(),
							RateReview.class);
					// note which seller is clicked, this will be useful in the
					// next activity
					mSeller = mSellerList[i];

					// delete all the arrays that are of no use anymore
					mSellerList = null;
					rbSellers = null;
					System.gc();

					// start the new activity
					startActivity(rateReview);

				} catch (Exception e) {
					Log.d(TAG, e.getMessage());
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_sellers, menu);
		return true;
	}

}
