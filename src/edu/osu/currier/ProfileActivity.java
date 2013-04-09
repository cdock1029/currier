package edu.osu.currier;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ProfileActivity extends Activity implements OnClickListener {

	// Values for fields at the time of saving profile attempt.
	private String mFirstName;
	private String mLastName;
	private String mEmail;
	private String mPhone;
	private String mAddress;

	//UI references.
	private EditText mFNameEditableField;
	private EditText mLNameEditableField;
	private EditText mEmailEditableField;
	private EditText mPhoneEditableField;
	private EditText mAddressEditableField;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		mFNameEditableField = (EditText)findViewById(R.id.prof_firstname);
		mLNameEditableField = (EditText)findViewById(R.id.prof_lastname);
		mEmailEditableField = (EditText)findViewById(R.id.prof_email);
		mPhoneEditableField = (EditText)findViewById(R.id.prof_phone);
		mAddressEditableField = (EditText)findViewById(R.id.prof_address);
		
		// Testing setting editable fields
		// Get the message from the intent
		//Intent intent = getIntent();
		//String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		//mFNameEditableField.setText(message);
		
		// Save Profile button
		View btnEdit = (Button)findViewById(R.id.btnEdit);
		btnEdit.setOnClickListener(this);
		
		// Discard Changes button
		View btnDiscard = (Button)findViewById(R.id.btnBack);
		btnDiscard.setOnClickListener(this);
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
			//finish();
			break;
		case R.id.btnBack:
			finish();
			break;
		}
	}

}