package se.gustavkarlsson.aurora_notifier.android.gui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.PollingService;
import se.gustavkarlsson.aurora_notifier.android.util.GooglePlayServicesUtils;
import se.gustavkarlsson.aurora_notifier.android.util.PermissionUtils;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		Log.v(TAG, "onResume");
		super.onResume();
		GooglePlayServicesUtils.ensureAvailable(this);
		if (PermissionUtils.hasLocationPermission(this)) {
			PollingService.requestUpdate(this); //TODO should this not happen in the fragment instead?
		} else {
			PermissionUtils.requestLocationPermission(this);
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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(TAG, "onOptionsItemSelected");
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
