package edu.osu.currier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListViewAdapter extends ArrayAdapter<String>{
	Context context;
	int layoutResourceId;
	ArrayList<String> data = null;
	FindFoodActivity f = null;

	public ListViewAdapter(Context context, int layoutResourceId, List<String> data, FindFoodActivity f) {
		super(context,layoutResourceId,data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = (ArrayList<String>) data;
		this.f = f;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return false;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ListViewItemHolder holder = null;
		
		if(row == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.listview_item_row, parent, false);
			
			holder = new ListViewItemHolder();
			holder.text = (TextView) row.findViewById(R.id.listText);
			holder.text.setTextColor(android.graphics.Color.BLACK);
			holder.text.setTypeface(Typeface.SERIF);
			holder.text.setTextSize(20);
			
			holder.mapButton = (Button) row.findViewById(R.id.mapButton);
			holder.mapButton.setFocusable(false);
			holder.mapButton.setClickable(false);
			holder.mapButton.setTextColor(android.graphics.Color.BLACK);
			holder.mapButton.setTag(position);
			holder.mapButton.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
				    int position=(Integer) v.getTag();
				    
				    List<Address> address = null;
					try {
						address = new Geocoder(f, Locale.ENGLISH).getFromLocationName(f.sellerAddress.get(position), 1);
						double lat = address.get(0).getLatitude();
						double lon = address.get(0).getLongitude();
						f.map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			
			holder.menuButton = (Button) row.findViewById(R.id.menuButton);
			holder.menuButton.setFocusable(false);
			holder.menuButton.setClickable(false);
			holder.menuButton.setTextColor(android.graphics.Color.BLACK);
			holder.menuButton.setTag(position);
			holder.menuButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int position = (Integer) v.getTag();
					Intent menu = new Intent(context, SellerMenuActivity.class);
					String objectId = f.sellerId.get(position);
					menu.putExtra("objectId", objectId);
					menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(menu);
				}
			});
			
			row.setTag(holder);
		}
		else {
			holder = (ListViewItemHolder) row.getTag();
		}
		
		holder.text.setText(data.get(position));
		holder.menuButton.setText("Menu");
		holder.mapButton.setText("Zoom to");
		
		return row;
	}


	static class ListViewItemHolder {
		TextView text;
		Button mapButton;
		Button menuButton;
	}
}
