package edu.osu.currier;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class SellerListFragment extends ListFragment {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// storing string resources into Array
		String[] values = new String[] {"Good Food", "Best in Town", "Naan but the Best", "Curry in a Hurry"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);


	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Do something with the data
		String item = (String) getListAdapter().getItem(position);
	    Toast.makeText(getActivity(), item + " selected", Toast.LENGTH_LONG).show();

	}
}

