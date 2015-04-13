package com.matesnetwork.cogdemov2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class Splash extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	boolean flag = false;
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	splashthread th;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		 th = new splashthread();
		th.start();

	}

	public class splashthread extends Thread {
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
				if (CheckNetworkConnection
						.isConnectionAvailable(getApplicationContext())) {
					flag = false;
					Intent in = new Intent(Splash.this, MainActivity.class);
					startActivity(in);
					finish();
				} else {
					flag = true;
					finish();
				}

			}
			super.run();
		}

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if (flag) {
			Toast.makeText(getApplicationContext(), "No internet connection",
					1000).show();
		}
		super.onStop();
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		
		super.onBackPressed();
	}
}
