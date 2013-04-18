/**
 * Handles Logging the user in. Saves Logged in user for use throughout application.
 * Links to Register activity for new users. Connects to Parse online database.
 * User is cached to elminate need to sign in for subsequent uses of the app.
 */
package edu.osu.currier;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {
	private static final String ACT = "LoginActivity";
	Dialog progressDialog;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	//UI references.
	private EditText mEmailEditableField;
	private EditText mPasswordEditableField;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//set default screen to login.xml
		setContentView(R.layout.activity_login);

		//link to register
		View registerLink = (TextView) findViewById(R.id.link_to_register);

		//Listening to register new account link
		registerLink.setOnClickListener(this);

		mEmailEditableField = (EditText)findViewById(R.id.login_email);
		mPasswordEditableField = (EditText)findViewById(R.id.login_password);

		//button
		View btnLogin = (Button)findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(this);

		Log.d(ACT, "finishing onCreate...");
	}

	/**
	 * Function that is called when login button is pressed.
	 * Error-checks fields for corret format and all filled in.
	 * Alerts user if errors found. Sends data to Parse. Creates intent for FindFoodActivity.
	 */
	private void attemptLogin() {
		// Reset errors.
		this.mEmailEditableField.setError(null);
		this.mPasswordEditableField.setError(null);

		//Store strings at time of login attempt.
		this.mEmail = this.mEmailEditableField.getText().toString();
		this.mPassword = this.mPasswordEditableField.getText().toString();

		boolean cancel = false;
		View focusView = null;

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

		if (cancel) {
			
			focusView.requestFocus();
		} else {
			ParseUser.logInInBackground(mEmail, mPassword, new LogInCallback() {

				@Override
				public void done(ParseUser user, ParseException e) {
					progressDialog.dismiss();
					if (user != null) {
						//Worked
						Log.d(ACT, "User: " + mEmail + " has logged in.");
						// Launch Dashboard Screen
                        Intent findfood = new Intent(getApplicationContext(), FindFoodActivity.class);

                        // Close all views before launching Dashboard
                        findfood.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(findfood);
						//close login screen
						finish();
					} else {
						//log in failed.
						Log.d(ACT, "Error: user " + mEmail + " failed login.");
						Toast.makeText(getApplicationContext(), "Error: Login failed. Please check your credentials.", Toast.LENGTH_LONG).show();
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
		int id = v.getId();
		if (id == R.id.link_to_register) {
			startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
			finish();
		} else if (id == R.id.btnLogin) {
			progressDialog = ProgressDialog.show(LoginActivity.this, "",
					"Loading...", true);
			attemptLogin();
		}
	}
}
