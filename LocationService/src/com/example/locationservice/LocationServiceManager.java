package com.example.locationservice;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationServiceManager extends Service implements
		LocationListener, ConnectionCallbacks, OnConnectionFailedListener {

	private static final String TAG = "app";

	private Location mLastLocation;

	private GoogleApiClient mGoogleApiClient;

	private boolean mRequestingLocationUpdates = false;

	private LocationRequest mLocationRequest;

	private static int UPDATE_INTERVAL = 5000; // 5 sec
	private static int FATEST_INTERVAL = 2500; // 2.5 sec
	private static int DISPLACEMENT = 0; // in meters

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (checkPlayServices()) {
			buildGoogleApiClient();
			createLocationRequest();

			if (mGoogleApiClient != null) {
				mGoogleApiClient.connect();
				displayLocation();
			}
		}
		return START_STICKY;
	}

	private void displayLocation() {

		mLastLocation = LocationServices.FusedLocationApi
				.getLastLocation(mGoogleApiClient);

		if (mLastLocation != null) {
			double latitude = mLastLocation.getLatitude();
			double longitude = mLastLocation.getLongitude();

			Toast.makeText(getApplicationContext(),
					latitude + ", " + longitude, 500).show();
			togglePeriodicLocationUpdates();
		} else {
			Toast.makeText(getApplicationContext(), "Failed to get Location ",
					500).show();
		}
	}

	private void togglePeriodicLocationUpdates() {
		if (!mRequestingLocationUpdates) {
			mRequestingLocationUpdates = true;
			startLocationUpdates();

		}

	}

	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
	}

	protected void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FATEST_INTERVAL);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				return false;
			} else {
				Toast.makeText(getApplicationContext(),
						"This device is not supported.", Toast.LENGTH_LONG)
						.show();
				return false;
			}
		}
		return true;
	}

	protected void startLocationUpdates() {

		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);

	}

	protected void stopLocationUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(
				mGoogleApiClient, this);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
				+ result.getErrorCode());
	}

	@Override
	public void onConnected(Bundle bundle) {

		displayLocation();

		if (mRequestingLocationUpdates) {
			startLocationUpdates();
		}
	}

	@Override
	public void onConnectionSuspended(int status) {
		mGoogleApiClient.connect();
	}

	@Override
	public void onLocationChanged(Location location) {
		mLastLocation = location;

		Toast.makeText(
				getApplicationContext(),
				"Updated: " + location.getLatitude() + ", "
						+ location.getLongitude(), 500).show();

		// Displaying the new location on UI
		displayLocation();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(getApplicationContext(), "Stopped", 500).show();
		if (mGoogleApiClient.isConnected()) {
			stopLocationUpdates();
			mGoogleApiClient.disconnect();
		}
	}

}
