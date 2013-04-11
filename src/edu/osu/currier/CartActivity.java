package edu.osu.currier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.money.Money;

public class CartActivity extends Activity {
	private List<String> names;
	private List<String> prices;
	private List<String> ids;
	private String total;
	private Dialog progressDialog;
	private Context context;
	private ListView cartLv;
	
	static final String[] CART_COLS = {"NAME", "PRICE"};
	static List<Map<String,String>> data;
	
	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			CartActivity.this.progressDialog = ProgressDialog.show(CartActivity.this, "",
					"Loading...", true);
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			CartActivity.this.progressDialog.dismiss();
			Toast.makeText(getBaseContext(), "Thank You For Your Order!", Toast.LENGTH_LONG).show();
		}
		
		
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		}
		populateDetails();
		// add footer (contains the button) to listview
//		View footerView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.cart_footer,null,false);
//		cartLv.addFooterView(footerView);
		new RemoteDataTask().execute();
 		
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
	

}
