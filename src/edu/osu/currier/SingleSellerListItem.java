package edu.osu.currier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class SingleSellerListItem extends FragmentActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_single_seller_list_item);
		
		TextView txtSeller = (TextView) findViewById(R.id.seller_label);
		
		Intent i = getIntent();
		String seller = i.getStringExtra("seller");
		txtSeller.setText(seller);
		
	}

}
