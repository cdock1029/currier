package edu.osu.currier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.money.Money;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import edu.osu.currier.library.HelperFunctions;

public class CartActivity extends Activity implements OnClickListener {
	static final String TAG = "CART";
	List<String> names;
	List<String> prices;
	List<String> ids;
	String total;
	String sellerId;
	Dialog progressDialog;
	Context context;
	ListView cartLv;
	ParseObject order;
	ParseUser user;
	AlertDialog.Builder builder;

	static final String[] CART_COLS = {"NAME", "PRICE"};
	List<Map<String,String>> data;

	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			CartActivity.this.progressDialog = ProgressDialog.show(CartActivity.this, "",
					"Loading...", true);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			order = new ParseObject("Order");
			order.put("userId", ParseObject.createWithoutData("_User", user.getObjectId()));
			order.put("sellerId", ParseObject.createWithoutData("Seller", sellerId));
			order.put("total", total);
			Money money = Money.parse(HelperFunctions.country.Locality + total);
			ParseRelation relation = order.getRelation("menuItems");
			for (String id : ids) {
				relation.add(ParseObject.createWithoutData("MenuItems", id));
			}
			try {
				order.save();
				user.increment("balance", money.getAmount());
				user.saveEventually();
			} catch (ParseException e) {
				Log.d(TAG, "Parse: " + e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			CartActivity.this.progressDialog.dismiss();
			AlertDialog dialog = builder.create();
			dialog.show();
		}


	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		user = ParseUser.getCurrentUser();
		if (user == null) {
			Intent login = new Intent(getApplicationContext(), LoginActivity.class);
			login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(login);
			finish();
		}
		context = CartActivity.this;
		setContentView(R.layout.activity_cart);
		
		cartLv = (ListView)findViewById(R.id.cart_list);
		//totalLv = (ListView)findViewById(R.id.total_list);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			names = extras.getStringArrayList("names");
			prices = extras.getStringArrayList("prices");
			ids = extras.getStringArrayList("ids");
			total = extras.getString("total");
			sellerId = extras.getString("sellerId");
		}
		populateDetails();
		setUpButton();
	}


	private void setUpButton() {
		Button submit = (Button)findViewById(R.id.submit_order_button);
		submit.setOnClickListener(this);
		
		Button cancel = (Button)findViewById(R.id.cancel_order_button);
		cancel.setOnClickListener(this);
		
		builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.order_complete).setTitle("Thank You!").setCancelable(false);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				CartActivity.this.finish();
				Intent home = new Intent(getApplicationContext(), FindFoodActivity.class);
			    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			    startActivity(home);
			}
		});
	}


	private void populateDetails() {
		data = new ArrayList<Map<String,String>>();
		for (int i = 0; i < ids.size(); i++) {
			Map<String,String> rowMap = new HashMap<String,String>();
			rowMap.put(CART_COLS[0], names.get(i));
			rowMap.put(CART_COLS[1], prices.get(i));
			rowMap.put("ID", ids.get(i));
			data.add(rowMap);
		}
		cartLv.setAdapter(new SimpleAdapter(
				context, 
				data, 
				R.layout.cart_item, 
				CART_COLS, 
				new int[] {R.id.cart_name, R.id.cart_price}
				));
		TextView v = (TextView)findViewById(R.id.total_value);
		v.setText(total);
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == (R.id.submit_order_button)) {
			new RemoteDataTask().execute();
		} else if (id == (R.id.cancel_order_button)) {
			finish();
		}
		
	}


}
