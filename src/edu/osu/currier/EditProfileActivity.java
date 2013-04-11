package edu.osu.currier;

import com.parse.ParseUser;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditProfileActivity extends Activity implements OnClickListener {
	private static final String ACT = "EditProfileActivity";
	
	//Parse User.
	private ParseUser mUser;
	
	// Values for fields at the time of saving profile attempt.
	private String mFirstName;
	private String mLastName;
	private String mPhone;
	private String mAddress;

	//UI references.
	private EditText mFNameEditableField;
	private EditText mLNameEditableField;
	private EditText mPhoneEditableField;
	private EditText mAddressEditableField;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		
		mUser = ParseUser.getCurrentUser();
		
		mFNameEditableField = (EditText)findViewById(R.id.prof_firstname);
		mLNameEditableField = (EditText)findViewById(R.id.prof_lastname);
		mPhoneEditableField = (EditText)findViewById(R.id.prof_phone);
		mAddressEditableField = (EditText)findViewById(R.id.prof_address);
		
		// Populate fields with data from database
		mFNameEditableField.setText(mUser.getString("firstName"));
		mLNameEditableField.setText(mUser.getString("lastName"));
		mPhoneEditableField.setText(mUser.getString("phoneNumber"));
		mAddressEditableField.setText(mUser.getString("address"));
		
		// Save Profile button
		View btnSave = (Button)findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);
		
		// Discard Changes button
		View btnDiscard = (Button)findViewById(R.id.btnDiscard);
		btnDiscard.setOnClickListener(this);
	}
	
	private void updateProfile() {
		
		// Reset errors.
		this.mFNameEditableField.setError(null);
		this.mLNameEditableField.setError(null);
		this.mPhoneEditableField.setError(null);
		this.mAddressEditableField.setError(null);

		//Store strings at time of saving profile attempt.
		this.mFirstName = this.mFNameEditableField.getText().toString();
		this.mLastName = this.mLNameEditableField.getText().toString();
		this.mPhone = this.mPhoneEditableField.getText().toString();
		this.mAddress = this.mAddressEditableField.getText().toString();
		
		boolean cancel = false;
		View focusView = null;
		
		// Check for a valid Address
		if (TextUtils.isEmpty(mAddress)) {
			mAddressEditableField.setError(getString(R.string.error_field_required));
			focusView = mAddressEditableField;
			cancel = true;
		}

		// Check for valid Phone Number
		if (TextUtils.isEmpty(mPhone)) {
			mPhoneEditableField.setError(getString(R.string.error_field_required));
			focusView = mPhoneEditableField;
			cancel = true;
		}
		
		// Check for a valid Last Name
		if (TextUtils.isEmpty(mLastName)) {
			mLNameEditableField.setError(getString(R.string.error_field_required));
			focusView = mLNameEditableField;
			cancel = true;
		}

		// Check for valid First Name
		if (TextUtils.isEmpty(mFirstName)) {
			mFNameEditableField.setError(getString(R.string.error_field_required));
			focusView = mFNameEditableField;
			cancel = true;
		}
		
		if (cancel) {
			//There was an error; don't attempt register..focus the first form field
			//with an error.
			focusView.requestFocus();
		} else {
			mUser.put("firstName", this.mFirstName);
			mUser.put("lastName", this.mLastName);
			mUser.put("phoneNumber", this.mPhone);
			mUser.put("address", this.mAddress);
			mUser.saveInBackground();
			
			Bundle bundle = new Bundle();
			bundle.putString("firstName", this.mFirstName);
			bundle.putString("lastName", this.mLastName); 
			bundle.putString("phoneNumber", this.mPhone);
			bundle.putString("address", this.mAddress);
			
			Intent profile = new Intent();
			profile.putExtras(bundle);
			setResult(Activity.RESULT_OK, profile);
			finish();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_edit_profile, menu);
		return true;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(ACT, "Pausing now....");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(ACT, "Resuming now....");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(ACT, "Stopping now...");
	}

	@Override
	public void onRestart() {
		super.onRestart();
		Log.d(ACT, "Restarting now...");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(ACT, "Destroying now...");
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btnSave:
			this.updateProfile();
			break;
		case R.id.btnDiscard:
			Intent profile = new Intent();
			//profile.addFlags(flags)
			setResult(Activity.RESULT_CANCELED, profile);
			//startActivity(profile);
			finish();
			break;
		}
	}
}