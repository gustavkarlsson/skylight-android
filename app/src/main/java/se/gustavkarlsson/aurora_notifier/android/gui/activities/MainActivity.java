package se.gustavkarlsson.aurora_notifier.android.gui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import se.gustavkarlsson.aurora_notifier.android.BuildConfig;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.DebugActivity;
import se.gustavkarlsson.aurora_notifier.android.background.ScheduleUpdatesBootReceiver;
import se.gustavkarlsson.aurora_notifier.android.background.UpdateService;
import se.gustavkarlsson.aurora_notifier.android.realm.Requirements;
import se.gustavkarlsson.aurora_notifier.android.util.GooglePlayServicesUtils;
import se.gustavkarlsson.aurora_notifier.android.util.LocationPermissionUtils;

import static se.gustavkarlsson.aurora_notifier.android.util.GooglePlayServicesUtils.REQUEST_CODE_GOOGLE_PLAY_SERVICES;
import static se.gustavkarlsson.aurora_notifier.android.util.LocationPermissionUtils.LOCATION_PERMISSION;
import static se.gustavkarlsson.aurora_notifier.android.util.LocationPermissionUtils.REQUEST_CODE_LOCATION_PERMISSION;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	private boolean isGooglePlayServicesAvailable = false;
	private boolean hasLocationPermission = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		isGooglePlayServicesAvailable = GooglePlayServicesUtils.ensureAvailable(this);
		hasLocationPermission = LocationPermissionUtils.hasPermission(this);
		if (!hasLocationPermission) {
			LocationPermissionUtils.requestPermission(this);
		}
		tryStartUpdating();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG, "onActivityResult");
		if (requestCode == REQUEST_CODE_GOOGLE_PLAY_SERVICES) {
			isGooglePlayServicesAvailable = resultCode == Activity.RESULT_OK;
			if (!isGooglePlayServicesAvailable) {
				GooglePlayServicesUtils.showNotInstalledErrorAndExit(this);
			}
			tryStartUpdating();
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
						LocationPermissionUtils.requestPermission(this);
					}
					tryStartUpdating();
				}
			}
		}
	}

	private void tryStartUpdating() {
		if (isGooglePlayServicesAvailable && hasLocationPermission) {
			Requirements.setFulfilled(true);
			ScheduleUpdatesBootReceiver.setupUpdateScheduling(this);
			UpdateService.requestUpdate(this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(TAG, "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.v(TAG, "onPrepareOptionsMenu");
		if (!BuildConfig.DEBUG) {
			menu.removeItem(R.id.action_debug);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(TAG, "onOptionsItemSelected");
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_debug) {
			Intent intent = new Intent(this, DebugActivity.class);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
