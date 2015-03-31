package com.example.locationservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent i = new Intent(MainActivity.this, LocationServiceManager.class);
		startService(i);
		
		Intent intent = new Intent("SystemState");
		sendBroadcast(intent);
	}

}
