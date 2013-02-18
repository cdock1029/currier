package edu.osu.currier;


import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends Activity implements OnClickListener{
	private static final String ACT = "RegisterActivity";
	//Parse User.
	private ParseUser mUser;

	// Values for email, password, name at the time of the registration attempt.
	private String mFirstName;
	private String mLastName;
	private String mEmail;
	private String mPassword;
	private String mConfirm;

	//UI references.
	private EditText mEmailEditableField;
	private EditText mFNameEditableField;
	private EditText mLNameEditableField;
	private EditText mPasswordEditableField;
	private EditText mConfirmEditableField;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		//set view to register.xml
		setContentView(R.layout.activity_register);

		//link to login
		View loginScreen = (TextView) findViewById(R.id.link_to_login);

		//listening to login screen link
		loginScreen.setOnClickListener(this);

		//editable text fields
		mEmailEditableField = (EditText)findViewById(R.id.reg_email);
		mFNameEditableField = (EditText)findViewById(R.id.reg_firstname);
		mLNameEditableField = (EditText)findViewById(R.id.reg_lastname);
		mPasswordEditableField = (EditText)findViewById(R.id.reg_password);
		mConfirmEditableField = (EditText)findViewById(R.id.reg_confirm);

		//button
		View btnRegister = (Button)findViewById(R.id.btnRegister);
		btnRegister.setOnClickListener(this);
		Log.d(ACT, "finishing onCreate...");
	}

	private void createUser() {

		// Reset errors.
		this.mFNameEditableField.setError(null);
		this.mLNameEditableField.setError(null);
		this.mEmailEditableField.setError(null);
		this.mPasswordEditableField.setError(null);
		this.mConfirmEditableField.setError(null);

		//Store strings at time of registration attempt.
		this.mFirstName = this.mFNameEditableField.getText().toString();
		this.mLastName = this.mLNameEditableField.getText().toString();
		this.mEmail = this.mEmailEditableField.getText().toString();
		this.mPassword = this.mPasswordEditableField.getText().toString();
		this.mConfirm = this.mConfirmEditableField.getText().toString();

		boolean cancel = false;
		View focusView = null;

		//Check for password confirmation.
		if (TextUtils.isEmpty(mConfirm)) {
			this.mConfirmEditableField.setError(getString(R.string.error_field_required));
			focusView = mConfirmEditableField;
			cancel = true;
		} else if (!TextUtils.equals(mPassword, mConfirm)) {
			this.mConfirmEditableField.setError(getString(R.string.error_confirm_equal));
			this.mPasswordEditableField.setError(getString(R.string.error_confirm_equal));
			focusView = mConfirmEditableField;
			cancel = true;
		}

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			this.mPasswordEditableField.setError(getString(R.string.error_field_required));
			focusView = mPasswordEditableField;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordEditableField.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordEditableField;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailEditableField.setError(getString(R.string.error_field_required));
			focusView = mEmailEditableField;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailEditableField.setError(getString(R.string.error_invalid_email));
			focusView = mEmailEditableField;
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
			mUser = new ParseUser();
			mUser.setUsername(this.mEmail);
			mUser.setEmail(this.mEmail);
			mUser.setPassword(this.mPassword);
			mUser.put("firstName", this.mFirstName);
			mUser.put("lastName", this.mLastName);
			mUser.signUpInBackground(new SignUpCallback() {

				@Override
				public void done(ParseException e) {
					if (e == null) {
						//Worked
						Log.d(ACT, "User: " + mEmail + " has registered.");
						//changed: added below
						
						// Launch Dashboard Screen
                        Intent findfood = new Intent(getApplicationContext(), FindFoodActivity.class);
                        // Close all views before launching Dashboard
                        findfood.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(findfood);
                        // Close Registration Screen
						finish();
					} else {
						//look at ParseException
						Log.d(ACT, "Error: user " + mEmail + " failed registration.");
					}
					
				}
			});
		}
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
		case R.id.link_to_login:
			startActivity(new Intent(getApplicationContext(), LoginActivity.class));
			finish();
			break;
		case R.id.btnRegister:
			this.createUser();
			break;
		}

	}
}
