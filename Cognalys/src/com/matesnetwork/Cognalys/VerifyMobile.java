package com.matesnetwork.Cognalys;

import java.lang.reflect.Method;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.internal.telephony.ITelephony;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class VerifyMobile extends Activity {
	String mobilenumber;
	String code;
	String cipher;
	String keymatch;
	String access_toke;
	String otp_number;
	String app_id;
	boolean flag;
	SharedPreferences pref;
	TextView load;
	ProgressDialog dialog;
	public static int REQUEST_CODE = 969;
	ClipboardManager clip;
	android.text.ClipboardManager manager;
	RelativeLayout loadinglayout;
	String cliptext = null;
	LinearLayout verifywithotpnumber;
	EditText miscallnum;
	boolean bool = false;
	TextView verifywithclipboard;
	boolean booleans = false;
	CountDownTimer countDownTimer;
	ImageView view;
	String callnumber = null;
	GPSTracker gps;
	boolean call_state = false;
	PhoneStateListener callStateListener;
	TelephonyManager telephonyManager;
	String imei = "", mcc = "", mnc = "", latitude = "0.0", longitude = "0.0",
			brand_name = "", os = "", model_num = "", gmail_id = "";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verify_mobile);
		dialog = new ProgressDialog(VerifyMobile.this);
		dialog.setMessage("Verifying, Please Wait...");
		dialog.setCancelable(false);
		pref = PreferenceManager.getDefaultSharedPreferences(VerifyMobile.this);
		dialog.setCanceledOnTouchOutside(false);
		load = (TextView) findViewById(R.id.progress);
		view = (ImageView) findViewById(R.id.loading);
		loadinglayout = (RelativeLayout) findViewById(R.id.loadinglayout);
		final Bundle bundle = getIntent().getExtras();
		verifywithotpnumber = (LinearLayout) findViewById(R.id.clipboard);
		verifywithclipboard = (TextView) findViewById(R.id.verifywithclipboard);
		miscallnum = (EditText) findViewById(R.id.misscallnum);
		Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
		rotation.setRepeatCount(Animation.INFINITE);
		view.startAnimation(rotation);
		TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		Account[] accounts = AccountManager.get(this).getAccountsByType(
				"com.google");

		for (Account account : accounts) {

			gmail_id = account.name;

		}
		gps = new GPSTracker(VerifyMobile.this);
		imei = mngr.getDeviceId();

		String networkOperator = mngr.getNetworkOperator();

		if (networkOperator != null) {
			mcc = networkOperator.substring(0, 3);
			mnc = networkOperator.substring(3);
		}
		if (CheckNetworkConnection
				.isConnectionAvailable(getApplicationContext())) {
			if (gps.canGetLocation()) {
				Location l = gps.getLocation();

				if (l != null) {
					latitude = "" + l.getLatitude();
					longitude = "" + l.getLongitude();
				}
			}
		}
		brand_name = Build.BRAND;
		model_num = Build.MODEL;
		os = android.os.Build.VERSION.RELEASE;

		countDownTimer = new CountDownTimer(30000, 1000) {

			public void onTick(long millisUntilFinished) {
				load.setText(millisUntilFinished / 1000 + " S");
			}

			public void onFinish() {
				// Intent in = new Intent();
				// in.putExtra("message", Constants.SIX);
				// setResult(REQUEST_CODE, in);
				// finish();
				if (booleans) {
					loadinglayout.setVisibility(View.GONE);
					verifywithotpnumber.setVisibility(View.VISIBLE);
				} else {
					Intent in = new Intent();
					in.putExtra("message", Constants.SEVEN);
					in.putExtra("result", 107);
					setResult(REQUEST_CODE, in);
					finish();
				}

				flag = true;
			}
		}.start();
		boolean clip_check = pref.getBoolean("clip_check", false);
		if (!clip_check) {
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			if (currentapiVersion < 12) {
				manager = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				manager.setText("");

			} else {
				clip = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				ClipData clips = ClipData.newPlainText("", "");
				clip.setPrimaryClip(clips);
				clip.setText("");

			}
			Editor edit = pref.edit();
			edit.putBoolean("clip_check", true);
			edit.commit();
		}
		// int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		// if (currentapiVersion < 12) {
		// manager = (android.text.ClipboardManager)
		// getSystemService(CLIPBOARD_SERVICE);
		// cliptext = manager.getText().toString();
		// if (isNumeric(cliptext)) {
		// miscallnum.setText(cliptext);
		// }
		// } else {
		// clip = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		// ClipData data = clip.getPrimaryClip();
		// ClipData.Item item = null;
		// if (data != null) {
		// item = data.getItemAt(0);
		// }
		// if (item != null) {
		// cliptext = item.getText().toString();
		// if (isNumeric(cliptext)) {
		// miscallnum.setText(cliptext);
		// }
		//
		// }
		// }

		verifywithclipboard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String ed_text = miscallnum.getText().toString().trim();
				if (ed_text.length() == 0 || ed_text.equals("")
						|| ed_text == null) {
					miscallnum
							.setError("Please enter the number or copy from call log");
				} else {
					String temp = miscallnum.getText().toString();
					temp = temp.replace("+", "");
					temp = temp.replace("-", "");
					temp = temp.replace("(", "");
					temp = temp.replace(")", "");
					temp = temp.replace(" ", "");
					if (temp.contains(cipher)) {
						Intent in = new Intent();
						in.putExtra("message", Constants.FOUR);
						in.putExtra("result", 104);
						setResult(REQUEST_CODE, in);
						// Intent i = new Intent("com.matesnetwork.cognalys");
						// i.putExtra("mobilenumber", mobile);
						// i.putExtra("app_user_id", app_user_id);
						// sendBroadcast(i);
						telephonyManager.listen(callStateListener,
								PhoneStateListener.LISTEN_NONE);
						finish();

						if (CheckNetworkConnection
								.isConnectionAvailable(VerifyMobile.this)) {
							Verifynumber num = new Verifynumber();
							num.execute();
							// dialog.show();
						} else {
							Toast.makeText(VerifyMobile.this,
									"No Internet Connection",
									Toast.LENGTH_SHORT).show();
							ExitThread thread = new ExitThread();
							thread.start();
						}

					} else {
						Toast.makeText(getApplicationContext(),
								"Wrong phone number", Toast.LENGTH_SHORT)
								.show();
					}

				}

			}
		});

		// TODO Auto-generated method stub
		if (!(bundle == null)) {
			app_id = bundle.getString("app_id");
			access_toke = bundle.getString("access_token");
			mobilenumber = bundle.getString("mobile");
			if (CheckNetworkConnection.isConnectionAvailable(VerifyMobile.this)) {
				Asynctask asy = new Asynctask();
				asy.execute();
			} else {
				Toast.makeText(VerifyMobile.this, "No Internet Connection",
						Toast.LENGTH_SHORT).show();
				ExitThread thread = new ExitThread();
				thread.start();
			}

		} else {
			Intent in = new Intent();
			in.putExtra("message", Constants.THREE);
			in.putExtra("result", 103);
			setResult(REQUEST_CODE, in);
			finish();
		}

	}

	public class Asynctask extends AsyncTask<Void, Void, Void> {
		String status = null;
		String error;

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ServiceHandler sh = new ServiceHandler();

			// Making a request to url and getting response
			String url = Constants.BASE_URL + "?app_id=" + app_id
					+ "&access_token=" + access_toke + "&mobile="
					+ mobilenumber + "&imei=" + imei + "&mcc=" + mcc + "&mnc="
					+ mnc + "&lat=" + latitude + "&brand_name=" + brand_name
					+ "&os_version=" + os + "&model_number=" + model_num
					+ "&lon=" + longitude + "&gmail_id=" + gmail_id;
			System.out.println(url);
			String jsonStr = sh.makeServiceCall(Constants.BASE_URL + "?app_id="
					+ app_id + "&access_token=" + access_toke + "&mobile="
					+ mobilenumber + "&imei=" + imei + "&mcc=" + mcc + "&mnc="
					+ mnc + "&lat=" + latitude + "&brand_name=" + brand_name
					+ "&os_version=" + os + "&model_number=" + model_num
					+ "&lon=" + longitude + "&gmail_id=" + gmail_id,
					ServiceHandler.GET);

			JSONObject obj = null;
			try {
				obj = new JSONObject(jsonStr);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				status = obj.getString("status");
				if (status != null) {

					if (status.equalsIgnoreCase("failed")) {

						JSONArray arr = obj.getJSONArray("codes");
						Log.d("errors", "error code = " + arr.toString());
						// error = json.getString("404");
						Intent in = new Intent();
						in.putExtra("message", Constants.ONE);
						in.putExtra("result", 101);
						Log.d("More Details",
								"https://www.cognalys.com/androidlibraryerrors/");
						setResult(REQUEST_CODE, in);

						finish();

					} else {
						Log.d("verification",
								"verification api calls succesfully");
						booleans = true;
						cipher = obj.getString("cipher");

						cipher = cipher.substring(12, 23);
						System.out.println(cipher);

						otp_number = "+" + obj.getString("mobile");
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (status != null) {

				if (status.equalsIgnoreCase("failed")) {

					Intent in = new Intent();
					in.putExtra("message", Constants.ONE);
					in.putExtra("result", 101);
					setResult(REQUEST_CODE, in);
					finish();

				} else if (status.equalsIgnoreCase("success")) {

					telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					callStateListener = new PhoneStateListener() {
						public void onCallStateChanged(int state,
								String incomingNumber) {
							// TODO React to incoming call.

							if (state == TelephonyManager.CALL_STATE_RINGING) {
								String number = incomingNumber.substring(4, 10);

								if (cipher.contains(number)) {
									callnumber = incomingNumber;
									call_state = true;

									try {
										Class c = Class
												.forName(telephonyManager
														.getClass().getName());
										Method m = c
												.getDeclaredMethod("getITelephony");
										m.setAccessible(true);
										final ITelephony telephonyService = (ITelephony) m
												.invoke(telephonyManager);
										// telephonyService.silenceRinger();
										telephonyService.endCall();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}

							}
							if (state == TelephonyManager.CALL_STATE_OFFHOOK) {

							}

							if (state == TelephonyManager.CALL_STATE_IDLE) {
								if (call_state) {
									if (!flag) {
										// Mythread th = new Mythread();
										// th.start();
										getCallDetails();

										loadinglayout
												.setVisibility(View.VISIBLE);
									}
									call_state = false;
								}

							}
						}
					};
					telephonyManager.listen(callStateListener,
							PhoneStateListener.LISTEN_CALL_STATE);
				}
			} else {
				Intent in = new Intent();
				in.putExtra("message", Constants.EIGHT);
				in.putExtra("result", 108);
				setResult(REQUEST_CODE, in);
				finish();
			}
		}

	}

	@SuppressWarnings("deprecation")
	private Boolean getCallDetails() {
		Boolean flags = false;

		Cursor managedCursor = null;
		try {
			managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null,
					null, null);
		} catch (Exception e) {
			// TODO: handle exception

			loadinglayout.setVisibility(View.GONE);
			verifywithotpnumber.setVisibility(View.VISIBLE);
			if (callnumber != null) {
				miscallnum.setText(callnumber);
			}

		}
		try {

			int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
			int count = managedCursor.getCount();
			managedCursor.moveToPosition(count - 5);

			while (managedCursor.moveToNext()) {
				String phNumber = managedCursor.getString(number);
				if (phNumber.contains(cipher.substring(6, 10))) {

					Log.i("numbers", otp_number);
					if (CheckNetworkConnection
							.isConnectionAvailable(VerifyMobile.this)) {
						Verifynumber num = new Verifynumber();
						num.execute();
						Intent in = new Intent();
						in.putExtra("message", Constants.FOUR);
						in.putExtra("result", 104);
						setResult(REQUEST_CODE, in);
						telephonyManager.listen(callStateListener,
								PhoneStateListener.LISTEN_NONE);
						finish();

					} else {
						Toast.makeText(VerifyMobile.this,
								"No Internet Connection", Toast.LENGTH_SHORT)
								.show();
						ExitThread th = new ExitThread();
						th.start();
					}

					flags = true;
					bool = false;

					Log.i("log", phNumber);

				} else {
					// Toast.makeText(getApplicationContext(), "no",
					// 1000).show();
					//
					flags = false;

				}

			}
		} catch (Exception e) {
			// TODO: handle exception

			if (callnumber != null) {
				miscallnum.setText(callnumber);
			}
		}
		if (!flags) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {

				}
			});

		} else {
			flag = false;
			countDownTimer.cancel();
		}

		return flags;
	}

	public class Mythread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int index;
			for (index = 0; index < 2; index++) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (index == 2) {
				// getCallDetails();

			}
			super.run();
		}
	}

	public class Verifynumber extends AsyncTask<Void, Void, Void> {
		String status = null;
		String app_user_id;
		String mobile;

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ServiceHandler handler = new ServiceHandler();
			// String url = Constants.BASE_URL2 + "?app_id=" + app_id
			// + "&access_token=" + access_toke + "&mobile=" + otp_number
			// + "&imei=" + imei + "&mcc=" + mcc + "&mnc=" + mnc + "&lat="
			// + latitude + "&brand_name=" + brand_name + "&os_version="
			// + os + "&model_number=" + model_num + "&lon=" + longitude
			// + "&gmail_id=" + gmail_id;
			// Log.d("url", url);
			String json = handler.makeServiceCall(
					Constants.BASE_URL2 + "?app_id=" + app_id
							+ "&access_token=" + access_toke + "&mobile="
							+ otp_number + "&imei=" + imei + "&mcc=" + mcc
							+ "&mnc=" + mnc + "&lat=" + latitude
							+ "&brand_name=" + brand_name + "&os_version=" + os
							+ "&model_number=" + model_num + "&lon="
							+ longitude + "&gmail_id=" + gmail_id,
					ServiceHandler.GET);

			try {
				JSONObject obj = new JSONObject(json);
				status = obj.getString("status");
				if (status.equalsIgnoreCase("failed")) {
					JSONArray arr = obj.getJSONArray("codes");
					Log.d("errors", "error code = " + arr.toString());
					Log.d("More Details",
							"https://www.cognalys.com/androidlibraryerrors/");

				} else {
					mobile = obj.getString("mobile");
					app_user_id = obj.getString("app_user_id");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			if (status == null) {
				if (CheckNetworkConnection
						.isConnectionAvailable(VerifyMobile.this)) {
					Verifynumber num = new Verifynumber();
					num.execute();
				} else {
					Toast.makeText(VerifyMobile.this, "No Internet Connection",
							Toast.LENGTH_SHORT).show();
					ExitThread th = new ExitThread();
					th.start();
				}

			} else {

				if (status.equalsIgnoreCase("success")) {
					Intent in = new Intent();
					in.putExtra("message", Constants.FOUR);
					in.putExtra("result", 104);
					setResult(REQUEST_CODE, in);
					Intent i = new Intent("com.matesnetwork.cognalys");
					i.putExtra("mobilenumber", mobile);
					i.putExtra("app_user_id", app_user_id);
					sendBroadcast(i);
					telephonyManager.listen(callStateListener,
							PhoneStateListener.LISTEN_NONE);
					finish();
					flag = true;
				} else {
					// Intent in = new Intent();
					// in.putExtra("message", Constants.FIVE);
					// setResult(REQUEST_CODE, in);
					// finish();
					Toast.makeText(getApplicationContext(),
							"WRONG MOBILE NUMBER", Toast.LENGTH_SHORT).show();
					flag = false;
				}
			}

		}

	}

	public static String getCountryCode(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String numWithCode = Iso2Phone.getPhone(tm.getNetworkCountryIso());
		return numWithCode;

	}

	public static boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent in = new Intent();
		in.putExtra("message", Constants.SIX);
		in.putExtra("result", 106);
		setResult(REQUEST_CODE, in);
		finish();
	}

	public class ExitThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int index;
			for (index = 0; index < 3; index++) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (index == 3) {
				Intent in = new Intent();
				in.putExtra("message", Constants.EIGHT);
				in.putExtra("result", 108);
				setResult(REQUEST_CODE, in);
				finish();
			}
			super.run();
		}
	}

	public String hexToString(String txtInHex) {
		byte[] txtInByte = new byte[txtInHex.length() / 2];
		int j = 0;
		for (int i = 0; i < txtInHex.length(); i += 2) {
			txtInByte[j++] = Byte.parseByte(txtInHex.substring(i, i + 2), 16);
		}
		return new String(txtInByte);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion < 12) {
			manager = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			cliptext = manager.getText().toString();
			cliptext = cliptext.replace(" ", "");
			cliptext = cliptext.replace("+", "");
			cliptext = cliptext.replace("(", "");
			cliptext = cliptext.replace(")", "");
			cliptext = cliptext.replace("-", "");
			if (isNumeric(cliptext)) {
				miscallnum.setText(cliptext);

			}
		} else {
			clip = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			ClipData data = clip.getPrimaryClip();
			ClipData.Item item = null;
			if (data != null) {
				item = data.getItemAt(0);
			}
			if (item != null) {
				cliptext = item.getText().toString();
				cliptext = cliptext.replace(" ", "");
				cliptext = cliptext.replace("+", "");
				cliptext = cliptext.replace("(", "");
				cliptext = cliptext.replace(")", "");
				cliptext = cliptext.replace("-", "");
				if (isNumeric(cliptext)) {
					miscallnum.setText(cliptext);

				}

			}
		}
		super.onResume();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion < 12) {
			manager = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			cliptext = manager.getText().toString();
			cliptext = cliptext.replace(" ", "");
			cliptext = cliptext.replace("+", "");
			cliptext = cliptext.replace("(", "");
			cliptext = cliptext.replace(")", "");
			cliptext = cliptext.replace("-", "");
			if (isNumeric(cliptext)) {
				miscallnum.setText(cliptext);

			}
		} else {
			clip = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			ClipData data = clip.getPrimaryClip();
			ClipData.Item item = null;
			if (data != null) {
				item = data.getItemAt(0);
			}
			if (item != null) {
				cliptext = item.getText().toString();
				cliptext = cliptext.replace(" ", "");
				cliptext = cliptext.replace("+", "");
				cliptext = cliptext.replace("(", "");
				cliptext = cliptext.replace(")", "");
				cliptext = cliptext.replace("-", "");
				if (isNumeric(cliptext)) {
					miscallnum.setText(cliptext);

				}

			}
		}
		super.onRestart();
	}

}
