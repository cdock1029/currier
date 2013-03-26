package edu.osu.currier;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class MenuExpandableListAdapter extends BaseExpandableListAdapter {
	private LayoutInflater mInflater;
	private Context ctx;
	private List<MenuCategory> catList;
	
	public MenuExpandableListAdapter(List<MenuCategory> catList, Context context) {
		ctx = context;
		mInflater = LayoutInflater.from(context);
		this.catList = catList;
	}
	
	
	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
	
		View v = convertView;
		
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.menu_item, parent, false);
		}
		
		TextView itemName = (TextView) v.findViewById(R.id.itemName);
	    TextView itemDescr = (TextView) v.findViewById(R.id.itemDescr);
	     
	    MenuItem det = catList.get(groupPosition).getItemList().get(childPosition);
	     
	    itemName.setText(det.getName());
	    itemDescr.setText(det.getDescr());
	     
	    return v;
	
	}

	@Override
	public int getChildrenCount(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getGroup(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getGroupId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.menu_category, parent, false);
		}
		
		TextView groupName = (TextView)v.findViewById(R.id.groupName);
		TextView groupDesc = (TextView)v.findViewById(R.id.groupDescr);
		
		MenuCategory cat = catList.get(groupPosition);
		
		groupName.setText(cat.getName());
		groupDesc.setText(cat.getDescr());
		return v;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

}
