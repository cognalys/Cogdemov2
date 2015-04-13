package com.matesnetwork.cogdemov2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.matesnetwork.Cognalys.VerifyMobile;

public class MainActivity extends ActionBarActivity {
	Button test;
	EditText mobilenum;
	EditText countrycode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		test = (Button) findViewById(R.id.test);
		mobilenum = (EditText) findViewById(R.id.editText1);
		countrycode = (EditText) findViewById(R.id.editText2);
		countrycode.setText(VerifyMobile
				.getCountryCode(getApplicationContext()));
		test.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String mobile = countrycode.getText().toString()
						+ mobilenum.getText().toString();
				Intent in = new Intent(MainActivity.this, VerifyMobile.class);

				in.putExtra("app_id", "YOUR_APP_ID_HERE");
				in.putExtra("access_token",
						"YOUR_ACCESS_TOKEN_HERE");
				in.putExtra("mobile", mobile);
				if (mobile.length() == 0) {
					countrycode.setError("Please enter mobile number");
				} else {
					if (CheckNetworkConnection
							.isConnectionAvailable(getApplicationContext())) {
						startActivityForResult(in, VerifyMobile.REQUEST_CODE);
					} else {
						Toast.makeText(getApplicationContext(),
								"no internet connection", Toast.LENGTH_SHORT)
								.show();
					}
				}

			}
		});
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);

		if (arg0 == VerifyMobile.REQUEST_CODE) {
			String message = arg2.getStringExtra("message");
			int result = arg2.getIntExtra("result", 0);

			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
					.show();
			Toast.makeText(getApplicationContext(), "" + result,
					Toast.LENGTH_SHORT).show();

		}
	}

}
