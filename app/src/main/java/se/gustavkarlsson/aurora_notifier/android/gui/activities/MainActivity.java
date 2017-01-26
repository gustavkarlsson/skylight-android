package se.gustavkarlsson.aurora_notifier.android.gui.activities;

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
import se.gustavkarlsson.aurora_notifier.android.background.PollingService;
import se.gustavkarlsson.aurora_notifier.android.util.GooglePlayServicesUtils;
import se.gustavkarlsson.aurora_notifier.android.util.PermissionUtils;

import static se.gustavkarlsson.aurora_notifier.android.util.PermissionUtils.LOCATION_PERMISSION;
import static se.gustavkarlsson.aurora_notifier.android.util.PermissionUtils.REQUEST_CODE_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		GooglePlayServicesUtils.ensureAvailable(this);
		if (!PermissionUtils.hasLocationPermission(this)) {
			PermissionUtils.requestLocationPermission(this);
		}
	}

	@Override
	protected void onResume() {
		Log.v(TAG, "onResume");
		super.onResume();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		Log.v(TAG, "onRequestPermissionsResult");
		if (REQUEST_CODE_FINE_LOCATION == requestCode) {
			for (int i = 0; i < permissions.length; i++) {
				String permission = permissions[i];
				int result = grantResults[i];
				if (LOCATION_PERMISSION.equals(permission)) {
					if (PackageManager.PERMISSION_GRANTED == result) {
						PollingService.requestUpdate(this);
					} else {
						PermissionUtils.requestLocationPermission(this);
					}
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG, "onActivityResult");
		if (GooglePlayServicesUtils.googlePlayServicesFailedToInstall(requestCode, resultCode)) {
			GooglePlayServicesUtils.showNotInstalledErrorAndExit(this);
		} else {
			super.onActivityResult(requestCode, resultCode, data);
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
