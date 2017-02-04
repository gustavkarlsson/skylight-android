package se.gustavkarlsson.aurora_notifier.android.gui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.ScheduleUpdatesBootReceiver;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.main.MainActivity;
import se.gustavkarlsson.aurora_notifier.android.realm.Requirements;
import se.gustavkarlsson.aurora_notifier.android.util.GooglePlayServicesUtils;
import se.gustavkarlsson.aurora_notifier.android.util.LocationPermissionUtils;

import static se.gustavkarlsson.aurora_notifier.android.util.GooglePlayServicesUtils.REQUEST_CODE_GOOGLE_PLAY_SERVICES;


public class StartActivity extends AppCompatActivity {
	private static final String TAG = StartActivity.class.getSimpleName();

	public static final int REQUEST_CODE_LOCATION_PERMISSION = 1973;
	public static final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;

	private boolean isGooglePlayServicesAvailable = false;
	private boolean hasLocationPermission = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		isGooglePlayServicesAvailable = GooglePlayServicesUtils.ensureAvailable(this);
		hasLocationPermission = LocationPermissionUtils.ensurePermission(this);
		tryStart();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG, "onActivityResult");
		if (requestCode == REQUEST_CODE_GOOGLE_PLAY_SERVICES) {
			isGooglePlayServicesAvailable = resultCode == Activity.RESULT_OK;
			if (!isGooglePlayServicesAvailable) {
				GooglePlayServicesUtils.showNotInstalledErrorAndExit(this);
			} else {
				tryStart();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		Log.v(TAG, "onRequestPermissionsResult");
		if (REQUEST_CODE_LOCATION_PERMISSION == requestCode) {
			for (int i = 0; i < permissions.length; i++) {
				String permission = permissions[i];
				int result = grantResults[i];
				if (LOCATION_PERMISSION.equals(permission)) {
					hasLocationPermission = PackageManager.PERMISSION_GRANTED == result;
					if (!hasLocationPermission) {
						LocationPermissionUtils.ensurePermission(this);
					} else {
						tryStart();
					}
				}
			}
		}
	}

	private void tryStart() {
		if (isGooglePlayServicesAvailable && hasLocationPermission) {
			Requirements.setFulfilled(true);
			ScheduleUpdatesBootReceiver.setupUpdateScheduling(this);
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		}
	}
}
