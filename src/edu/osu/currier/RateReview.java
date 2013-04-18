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
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;

public class RateReview extends Activity implements OnClickListener {
	private static String TAG = "RateReview";

	TextView txtName;
	TextView txtAddr;
	RatingBar rbSeller;
	EditText editReview;
	Button btnPost;

	float mRating;
	String mReview;

	private LinearLayout lloReviews;

	protected TextView uname;

	protected static ParseUser user = null;
	protected static ParseObject seller = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_rate_review);
			// Show the Up button in the action bar.
			setupActionBar();

			Parse.initialize(this, "mGgQCNePMku0rAuWaV8vIpJJ2Qd4PrzmzTnxzdB8",
					"WmYOeKOFUh7v3UVk6SKICi7md3Wx0FfFsU69NgMa");

			getIntent();

			user = ParseUser.getCurrentUser();
			Log.d(TAG, "user = " + user.getUsername());

			seller = ListSellers.mSeller;
			String sellerName = seller.getString("publicName");
			String sellerAddr = seller.getString("Address");
			mRating = (float) seller.getDouble("avgRating");
			Log.d(TAG, "Fetched seller: " + sellerName + ", " + sellerAddr
					+ ", " + mRating);

			setAllFields();

			btnPost.setOnClickListener(this);

		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
	}

	private void setAllFields() {
		txtName = (TextView) findViewById(R.id.txtSellerName);
		txtAddr = (TextView) findViewById(R.id.txtSellerAddr);
		rbSeller = (RatingBar) findViewById(R.id.rbSeller);
		rbSeller.setNumStars(5);
		editReview = (EditText) findViewById(R.id.editReview);
		btnPost = (Button) findViewById(R.id.btnPost);
		lloReviews = (LinearLayout) findViewById(R.id.sellerInfoLayout);

		txtName.setText(seller.getString("publicName"));
		txtAddr.setText(seller.getString("Address"));

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

	@Override
	public void onClick(View v) {
		final String description = editReview.getText().toString();
		final float r = rbSeller.getRating();

		// see if an object with same seller-user exists
		ParseQuery q = new ParseQuery("SellerRating");
		q.whereEqualTo("seller", seller);
		q.whereEqualTo("user", user);

		// 1-a) Fetch the row from the SellerRating table which matches that
		// particular seller and the current user
		q.getFirstInBackground(new GetCallback() {

			@Override
			public void done(ParseObject ob, ParseException e) {
				if (e == null) {

				} else {
					// 1-b) If there is no such row, create a row with this
					// seller-user pair
					ob = new ParseObject("SellerRating");
					ob.put("seller", seller);
					ob.put("user", user);
					Log.d(TAG, e.getMessage());
				}
				// 2) Update the description and rating for this row
				ob.put("description", description);
				ob.put("rating", r);
				ob.saveInBackground(new SaveCallback() {
					public void done(ParseException e) {
						try {

							// ob.saveInBackground();
							Log.d(TAG, "Saved: " + description + ", " + r
									+ ", " + seller.getString("publicName")
									+ ", " + user.getString("username"));
							// 3) call the function that displays the updated
							// ratings
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

		// 1) Compute the new average rating
		// 1-a) Fetch the rows of this seller from SellerRating
		q.findInBackground(new FindCallback() {

			@Override
			public void done(List<ParseObject> list, ParseException e) {

				// 1-b) iterate over each rating of this seller
				Iterator<ParseObject> it = list.iterator();
				double sum = 0;
				while (it.hasNext()) {
					ParseObject ob = it.next();
					sum += ob.getDouble("rating");
				}
				// 1-c) compute the average
				final double avg = sum / (double) list.size();

				// 2) update seller record with the new average
				seller.put("avgRating", avg);
				seller.saveInBackground();

				// 3) Display a thank-you message which signals successful
				// completion of RateReview activity
				Toast.makeText(
						RateReview.this,
						"Thank you, " + user.getUsername()
								+ " for providing the review.",
						Toast.LENGTH_LONG).show();
			}
		});

		// 4) need to redraw everything
		lloReviews.removeAllViews();

		// 5) Draw the seller's name-address again
		lloReviews.addView(txtName);
		lloReviews.addView(txtAddr);

		// 6) Fetch the new reviews and display them
		fetchDisplayAllRatings();

	}

	public void fetchDisplayAllRatings() {
		// fetch all ratings for this seller from the SellerRating table
		ParseQuery sr = new ParseQuery("SellerRating");
		sr.whereEqualTo("seller", seller);

		// 1) Fetch all the rows that match this seller
		sr.findInBackground(new FindCallback() {

			@Override
			public void done(List<ParseObject> reviews, ParseException e) {

				// 2) Display the average rating
				TextView txtAvg = new TextView(getApplicationContext());
				txtAvg.setText("Average Rating (out of " + reviews.size()
						+ " ratings):");
				txtAvg.setTextColor(Color.parseColor("#000000"));
				txtAvg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

				lloReviews.addView(txtAvg);

				// Must set the RatingBar's width to WRAP_CONTENT to ensure the
				// number of stars is 5
				RatingBar rbAvg = new RatingBar(getApplicationContext());
				LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				((View) rbAvg).setLayoutParams(lp);

				float avgRating = (float) seller.getDouble("avgRating");
				rbAvg.setRating(avgRating);
				rbAvg.setIsIndicator(true);
				rbAvg.setNumStars(5);
				lloReviews.addView(rbAvg);

				// 3) Display each review
				// 3-a) iterate over each review
				Iterator<ParseObject> it = reviews.iterator();
				while (it.hasNext()) {
					final ParseObject r = it.next();

					ParseQuery q = ParseUser.getQuery();
					ParseObject ob = r.getParseObject("user");
					q.getInBackground(ob.getObjectId(), new GetCallback() {

						@Override
						public void done(ParseObject u, ParseException e1) {

							// 3-b) Display the user's name who wrote this
							// review
							String tempUname = u.getString("username");
							Log.d(TAG, "Fetched " + tempUname);

							TextView txtUname = new TextView(
									getApplicationContext());
							txtUname.setText("Review By: " + tempUname);
							txtUname.setTextColor(Color.parseColor("#000000"));
							txtUname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);

							lloReviews.addView(txtUname);

							// 3-c) Display the verbal review
							String desc = r.getString("description");
							TextView txtDesc = new TextView(
									getApplicationContext());
							txtDesc.setText(desc);
							txtDesc.setTextColor(Color.parseColor("#000000"));
							txtDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

							lloReviews.addView(txtDesc);

							// 3-d) Display the star-rating
							// (the step is 0.5, so may not see the accurate
							// rating)
							float ratingVal = (float) r.getDouble("rating");
							RatingBar rbRating = new RatingBar(
									getApplicationContext());
							rbRating.setNumStars(5);
							rbRating.setRating(ratingVal);
							rbRating.setIsIndicator(true);
							LayoutParams lp = new LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT);
							rbRating.setLayoutParams(lp);

							lloReviews.addView(rbRating);
						}
					});

				}

			}

		}); // end sr.findInBackground
	}
}
