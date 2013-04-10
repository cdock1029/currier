package edu.osu.currier;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import org.joda.money.Money;

public class CartActivity extends Activity {
	private List<String> names;
	private List<String> prices;
	private List<String> ids;
	private String total;
	private Dialog progress;
	private Context context;
	
	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
		
		
		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			//Toast.makeText(getBaseContext(), "total: " + total + " names: " + names + " prices: " + prices + " ids: " + ids, Toast.LENGTH_LONG).show();
		}
		
		
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = CartActivity.this;
		setContentView(R.layout.activity_cart); 
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			names = extras.getStringArrayList("names");
			prices = extras.getStringArrayList("prices");
			ids = extras.getStringArrayList("ids");
			total = extras.getString("total");
		}
		new RemoteDataTask().execute();
 		
	}
	

}
