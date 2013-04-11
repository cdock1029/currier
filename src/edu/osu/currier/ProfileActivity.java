package edu.osu.currier;

import java.text.NumberFormat;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import edu.osu.currier.library.HelperFunctions;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ProfileActivity extends Activity implements OnClickListener {
	private static final String ACT = "ProfileActivity";
	//format currenty correctly
	static NumberFormat nf = NumberFormat.getCurrencyInstance(HelperFunctions.country.LOC);
	
	//Parse User.
	private ParseUser mUser;

	//UI references.
	private EditText mBalanceEditableField;
	private EditText mFNameEditableField;
	private EditText mLNameEditableField;
	private EditText mEmailEditableField;
	private EditText mPhoneEditableField;
	private EditText mAddressEditableField;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mUser = ParseUser.getCurrentUser();
		
		if (mUser != null) {
			setContentView(R.layout.activity_profile);
			
			//mOjbect = ParseUser.getQuery();
			
			mBalanceEditableField = (EditText)findViewById(R.id.prof_balance);
			mFNameEditableField = (EditText)findViewById(R.id.prof_firstname);
			mLNameEditableField = (EditText)findViewById(R.id.prof_lastname);
			mEmailEditableField = (EditText)findViewById(R.id.prof_email);
			mPhoneEditableField = (EditText)findViewById(R.id.prof_phone);
			mAddressEditableField = (EditText)findViewById(R.id.prof_address);
			
			// Set field to be uneditable in this activity
			mBalanceEditableField.setKeyListener(null);
			mFNameEditableField.setKeyListener(null);
			mLNameEditableField.setKeyListener(null);
			mEmailEditableField.setKeyListener(null);
			mPhoneEditableField.setKeyListener(null);
			mAddressEditableField.setKeyListener(null);
			
			mUser.fetchInBackground(new GetCallback() {
				@Override
				public void done(ParseObject object, ParseException e) {
					if (object != null) {
						mUser = (ParseUser) object;
					}
					// Populate fields with data from database
					mBalanceEditableField.setText(nf.format(mUser.getNumber("balance")));
					mFNameEditableField.setText(mUser.getString("firstName"));
					mLNameEditableField.setText(mUser.getString("lastName"));
					mEmailEditableField.setText(mUser.getEmail());
					mPhoneEditableField.setText(mUser.getString("phoneNumber"));
					mAddressEditableField.setText(mUser.getString("address"));
					
					// Save Profile button
					View btnEdit = (Button)findViewById(R.id.btnEdit);
					btnEdit.setOnClickListener(ProfileActivity.this);
					
					// Discard Changes button
					View btnDiscard = (Button)findViewById(R.id.btnBack);
					btnDiscard.setOnClickListener(ProfileActivity.this);
				}
			});
		} else {
			// show the signup or login screen
			Intent login = new Intent(getApplicationContext(), LoginActivity.class);
			// look for LoginActivity already running and clear out Act's above it.
			login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(login);
			//close FindFood screen
			finish();
		}
		
		Log.d(ACT,"Finishing onCreate...");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_profile, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btnEdit:
			startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
			finish();
			break;
		case R.id.btnBack:
			finish();
			break;
		}
	}

}