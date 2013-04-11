package edu.osu.currier;

import java.util.Iterator;
import java.util.List;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class RateReview extends Activity implements OnClickListener {
	private static String TAG = "RateReview";

	TextView txtName;
	TextView txtAddr;
	RatingBar rbarSeller;
	EditText editReview;
	Button btnPost;

	float mRating;
	String mReview;

	private String sellerName;

	private String sellerAddr;

	private TableLayout tblReviews;

	protected String recordId;

	protected static ParseUser user = null;
	protected static ParseObject seller = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_rate_review);
			// Show the Up button in the action bar.
			setupActionBar();

			// Parse.initialize(this,
			// "mGgQCNePMku0rAuWaV8vIpJJ2Qd4PrzmzTnxzdB8",
			// "WmYOeKOFUh7v3UVk6SKICi7md3Wx0FfFsU69NgMa");

			Intent intent = getIntent();

			setUser();
			user = ParseUser.getCurrentUser();
			Log.d(TAG, "user = " + user.getUsername());
			// Toast.makeText(RateReview.this, "user = " + user.getUsername(),
			// Toast.LENGTH_LONG).show();

			ParseQuery query = new ParseQuery("Seller");
			sellerName = intent.getStringExtra(ListSellers.EXTRA_SELLER_NAME);
			mRating = intent.getFloatExtra(ListSellers.EXTRA_RATING, -1);
			// Log.d(TAG, "mRating = " + mRating);

			Log.d(TAG,
					"SELLER_NAME = "
							+ intent.getStringExtra(ListSellers.EXTRA_SELLER_NAME));
			// hard coded seller for now
			// String sellerId = "Ja9S22poQM";

			// seller = query.get(sellerId);

			query.whereEqualTo("publicName", sellerName);
			try {
				seller = query.getFirst();
			} catch (Exception e) {
				Log.d(TAG, e.getMessage());
			}
			// sellerName = seller.getString("publicName");
			sellerAddr = seller.getString("Address");
			mRating = (float) seller.getDouble("avgRating");
			Log.d(TAG, "mRating = " + mRating);

			// Log.d(TAG, "onCreate(): seller = " + sellerName);
			Toast.makeText(
					RateReview.this,
					"onCreate(): seller = " + sellerName + ".. Rating is: "
							+ mRating, Toast.LENGTH_LONG).show();

			setAllFields();

			btnPost.setOnClickListener(this);

		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
	}

	private void setUser() {
		// hard coded user for now
		try {
			user = ParseUser.logIn("newuser@gmail.com", "abc");
			Log.d(TAG, "getUser: user = " + user.getUsername());
		} catch (ParseException e) {
			Log.d(TAG, e.getMessage());
		}
	}

	private void setAllFields() {
		txtName = (TextView) findViewById(R.id.sellerName);
		txtAddr = (TextView) findViewById(R.id.sellerAddr);
		rbarSeller = (RatingBar) findViewById(R.id.rating);
		editReview = (EditText) findViewById(R.id.review);
		btnPost = (Button) findViewById(R.id.postButton);
		tblReviews = (TableLayout) findViewById(R.id.reviewsTable);

		txtName.setText(sellerName);
		txtAddr.setText(sellerAddr);

		fetchDisplayAllRatings();

	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rate_review, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void postRating(View view) {

	}

	@Override
	public void onClick(View v) {
		final String description = editReview.getText().toString();
		final float r = rbarSeller.getRating();

		// ParseObject db = new ParseObject("SellerRating");

		// see if an object with same seller-user exists
		ParseQuery q = new ParseQuery("SellerRating");
		q.whereEqualTo("seller", seller);
		q.whereEqualTo("user", user);
		q.getFirstInBackground(new GetCallback() {

			@Override
			public void done(ParseObject ob, ParseException e) {
				if (e == null) {

				} else {
					ob = new ParseObject("SellerRating");
					ob.put("seller", seller);
					ob.put("user", user);
					Log.d(TAG, e.getMessage());
				}
				ob.put("description", description);
				ob.put("rating", r);
				ob.saveInBackground(new SaveCallback() {
					public void done(ParseException e) {
						try {

							// ob.saveInBackground();
							Log.d(TAG + " onClick(): ", "Saved: " + description
									+ ", " + r + ", " + sellerName + ", "
									+ user.getString("username"));

							Toast.makeText(RateReview.this,
									"Thank you for rating " + sellerName,
									Toast.LENGTH_LONG).show();
							updateRatings();
						} catch (Exception e2) {
							Log.d(TAG, e2.getMessage());
						}
					}
				});
			}

		});

	}

	public void updateRatings() {
		// go through the entries of SellerRating for this seller, and compute
		// new avg
		ParseQuery q = new ParseQuery("SellerRating");
		q.whereEqualTo("seller", seller);
		q.findInBackground(new FindCallback() {

			@Override
			public void done(List<ParseObject> list, ParseException e) {
				Iterator<ParseObject> it = list.iterator();
				double sum = 0;
				while (it.hasNext()) {
					ParseObject ob = it.next();
					sum += ob.getDouble("rating");
				}
				final double avg = sum / (double) list.size();

				// update seller record with the new average
				seller.put("avgRating", avg);
				seller.saveInBackground();
			}
		});

		// remove the editable fields to display new ratings
		LinearLayout layout = (LinearLayout) findViewById(R.id.sellerInfoLayout);
		layout.removeView(editReview);
		layout.removeView(rbarSeller);
		layout.removeView(btnPost);

		tblReviews.removeAllViews();
		fetchDisplayAllRatings();

		// layout.refreshDrawableState();
	}

	public void fetchDisplayAllRatings() {
		// fetch all ratings for this seller from the SellerRating table
		ParseQuery sr = new ParseQuery("SellerRating");
		sr.whereEqualTo("seller", seller);

		sr.findInBackground(new FindCallback() {

			@Override
			public void done(List<ParseObject> reviews, ParseException e) {

				Log.d(TAG, "reviews found");

				// create as many rows as #ratings
				TableRow[] tr = new TableRow[reviews.size() * 3];
				String[] userNames = new String[reviews.size()];
				String[] descriptions = new String[reviews.size()];
				Double[] ratingPts = new Double[reviews.size()];

				TextView tv = new TextView(getApplicationContext());
				tv.setText("Average Rating (out of " + reviews.size()
						+ " ratings):");
				RatingBar rb = new RatingBar(getApplicationContext());
				mRating = (float) seller.getDouble("avgRating");
				rb.setRating(mRating);
				rb.setIsIndicator(true);

				TableRow trow0 = new TableRow(getApplicationContext());
				trow0.addView(tv);
				tblReviews.addView(trow0);

				TableRow trow1 = new TableRow(getApplicationContext());
				trow1.addView(rb);
				tblReviews.addView(trow1);

				// iterate over each review
				Iterator<ParseObject> it = reviews.iterator();
				int i = 0;
				while (it.hasNext()) {
					ParseObject r = it.next();

					descriptions[i] = r.getString("description");
					Log.d(TAG, "desc = " + descriptions[i]);

					ratingPts[i] = r.getDouble("rating");
					Log.d(TAG, "rating = " + ratingPts[i].toString());

					// fetch the user
					// userNames[i] = u.getUsername();
					ParseObject ob = r.getParseObject("user");
					Log.d(TAG, "user = " + ob.getObjectId());

					ParseQuery userq = ParseUser.getQuery();
					ParseUser u;
					try {
						u = (ParseUser) userq.get(ob.getObjectId());
						Log.d(TAG, "Fetched " + u.getObjectId());
						userNames[i] = u.getString("username");
						Log.d(TAG, "uname = " + userNames[i]);
					} catch (ParseException e1) {
						Log.d(TAG, e1.getMessage());
					}

					Log.d(TAG, "Fetching: " + userNames[i] + " "
							+ descriptions[i] + " " + ratingPts[i]);

					// Create objects for showing this info
					TextView uname = new TextView(getApplicationContext());
					uname.setText(userNames[i]);

					tr[i] = new TableRow(getApplicationContext());
					tr[i + 1] = new TableRow(getApplicationContext());
					tr[i + 2] = new TableRow(getApplicationContext());

					rb = new RatingBar(getApplicationContext());
					rb.setRating(ratingPts[i].floatValue());
					rb.setIsIndicator(true);
					Log.d(TAG,
							"new RatingBar created: "
									+ ratingPts[i].floatValue());

					tr[i].addView(uname);
					tr[i + 1].addView(rb);
					Log.d(TAG, "rb view added");

					TextView desc = new TextView(getApplicationContext());
					desc.setText(descriptions[i]);
					Log.d(TAG, "new TextView created: " + descriptions[i]);

					tr[i + 2].addView(desc);
					Log.d(TAG, "desc view added");

					tblReviews.addView(tr[i]);
					tblReviews.addView(tr[i + 1]);
					tblReviews.addView(tr[i + 2]);
					Log.d(TAG, "all items added to tblReviews");

					i++;
				}
			}

		}); // end sr.findInBackground
	}
}
