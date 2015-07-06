package com.rest.rutracker.rutrackerrestclient.ui.activities;

import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rest.rutracker.rutrackerrestclient.data.api.ApiService;
import com.rest.rutracker.rutrackerrestclient.data.api.ApiServiceHelper;
import com.rest.rutracker.rutrackerrestclient.data.api.request.DataAuthRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.response.DataLoginResponse;

import pct.droid.R;


public class LoginActivity extends AppCompatActivity
		implements View.OnClickListener{

	public static final int RESULT_CODE_LOGINED 	= 1;
	public static final int RESULT_CODE_NOT_LOGINED = 2;
	public static final String RESULT_PARAM_NAME	= "extraName";
	public static final String EXTRA_LOGIN = "extraJid";

	private Button mSiginButton;
	private EditText mLoginEditText;
	private EditText mPasswordEditText;
	private String		mLogin;
	private String		mServer;
	private String		mPassword;
	private ProgressBar	mProgress;
	private boolean			mIsBound;
	private boolean			mIsNeedConnect;
	private boolean			mIsChangeProfile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mSiginButton	= (Button) findViewById(R.id.login_sign_in_button);
		mSiginButton.setOnClickListener(this);

		mLoginEditText		= (EditText) findViewById(R.id.login_form_name);
		mPasswordEditText	= (EditText) findViewById(R.id.login_form_password);
		mProgress			= (ProgressBar) findViewById(R.id.login_progress);

	}

	@Override
	public void onClick(View v) {
		if(!TextUtils.isEmpty(mLoginEditText.getText().toString()) &&
				!TextUtils.isEmpty(mPasswordEditText.getText().toString())){
			disableFields();
			ApiServiceHelper.getAuth(new DataAuthRequest(mLoginEditText.getText().toString()
					, mPasswordEditText.getText().toString()), new ResultReceiver(new Handler()) {
				@Override
				protected void onReceiveResult(int resultCode, Bundle resultData) {
					if (!resultData.containsKey(ApiService.ERROR_KEY)) {
						DataLoginResponse response
								= (DataLoginResponse) resultData.getSerializable(ApiService.RESPONSE_OBJECT_KEY);
						if (response.isAuth()) {
							onFinish();
						} else {
							showMessage("Неверный логин или пароль");
							enableFields();
						}
					}
				}
			});
		}
	}

	private void disableFields() {
		mProgress.setVisibility(View.VISIBLE);
		mLoginEditText.setEnabled(false);
		mPasswordEditText.setEnabled(false);
		mSiginButton.setEnabled(false);
	}

	private void onFinish(){

		Intent intent = new Intent();
		intent.putExtra(EXTRA_LOGIN, mLogin);

		setResult(RESULT_OK, intent);
		finish();
	}

	private void enableFields() {
		mProgress.setVisibility(View.GONE);
		mSiginButton.setEnabled(true);
		mLoginEditText.setEnabled(true);
		mPasswordEditText.setEnabled(true);
	}

	private void showMessage(String msg){
		//tODO: Toast
		Toast.makeText(this, msg, Toast.LENGTH_SHORT);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent homeIntent = new Intent(this, MainActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		setResult(RESULT_CANCELED, homeIntent);
		finish();
	}
}
