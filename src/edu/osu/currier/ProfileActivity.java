package edu.osu.currier;

import java.text.NumberFormat;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.RefreshCallback;

import edu.osu.currier.library.HelperFunctions;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ProfileActivity extends Activity implements OnClickListener {
	private static final String ACT = "ProfileActivity";
	private static final int EDIT_PROFILE = 1;

	//format currenty correctly
	static NumberFormat nf = NumberFormat.getCurrencyInstance(HelperFunctions.country.LOC);
	Dialog progressDialog;

	//Parse User.
	private static ParseUser mUser;
	private static RefreshCallback callback;
	private static Bundle bundle = null;

	//UI references.
	private static EditText mBalanceEditableField;
	private static EditText mFNameEditableField;
	private static EditText mLNameEditableField;
	private static EditText mEmailEditableField;
	private static EditText mPhoneEditableField;
	private static EditText mAddressEditableField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

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

		// Save Profile button
		View btnEdit = (Button)findViewById(R.id.btnEdit);
		btnEdit.setOnClickListener(ProfileActivity.this);

		// Discard Changes button
		View btnDiscard = (Button)findViewById(R.id.btnBack);
		btnDiscard.setOnClickListener(ProfileActivity.this);

		mUser = ParseUser.getCurrentUser();
		callback = new RefreshCallback() {
			@Override
			public void done(ParseObject object, ParseException e) {
				if (object != null) {
					mUser = (ParseUser) object;
				}
				//This needs to be called in here...because we don't want it to run until
				//User object has been updated to freshest data.
				ProfileActivity.populateViews();
				progressDialog.dismiss();
			}
		};

		if (mUser != null) { //Since this Activity shows current balance, we always want to refresh user..even though
			//user is cached across activities...
			progressDialog = ProgressDialog.show(ProfileActivity.this, "",
					"Loading...", true);
			mUser.refreshInBackground(callback);
		} else {
			// show the signup or login screen
			Intent login = new Intent(ProfileActivity.this, LoginActivity.class);
			// look for LoginActivity already running and clear out Act's above it.
			login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(login);
			//close FindFood screen
			finish();
		}
		Log.d(ACT,"Finishing onCreate...");
	}

	protected static void populateViews() {
		// Populate fields with data from database

		mBalanceEditableField.setText(nf.format(mUser.getNumber("balance")));
		mEmailEditableField.setText(mUser.getEmail());
		if (bundle == null) {
			mFNameEditableField.setText(mUser.getString("firstName"));
			mLNameEditableField.setText(mUser.getString("lastName"));
			mPhoneEditableField.setText(mUser.getString("phoneNumber"));
			mAddressEditableField.setText(mUser.getString("address"));
		} else {
			mFNameEditableField.setText(bundle.getString("firstName"));
			mLNameEditableField.setText(bundle.getString("lastName"));
			mPhoneEditableField.setText(bundle.getString("phoneNumber"));
			mAddressEditableField.setText(bundle.getString("address"));
			bundle = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_profile, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btnEdit) {
			Intent i = new Intent(ProfileActivity.this, EditProfileActivity.class);
			startActivityForResult(i, EDIT_PROFILE);
		} else if (id == R.id.btnBack) {
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == EDIT_PROFILE) {
			if (intent == null) {
				Log.d(ACT, "onActivityResult: null intent");
				return;
			}
			switch (resultCode) {
			case RESULT_OK:
				bundle = intent.getExtras();
				populateViews();
				break;
			case RESULT_CANCELED:
				break;
			}
		}
	}


}