package se.gustavkarlsson.aurora_notifier.android.gui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.AuroraPollingService;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1972;
	private static final int REQUEST_FINE_LOCATION = 1973;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		super.onResume();
		GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
		int returnCode = availability.isGooglePlayServicesAvailable(this);
		if (returnCode != ConnectionResult.SUCCESS) {
			if (availability.isUserResolvableError(returnCode)) {
				availability.showErrorDialogFragment(this, returnCode, REQUEST_GOOGLE_PLAY_SERVICES);
			} else {
				new AlertDialog.Builder(this)
						.setTitle(R.string.google_play_services_not_supported)
						.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								System.exit(1);
							}
						})
						.setIcon(android.R.drawable.ic_dialog_alert)
						.show();
			}
		}

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			Log.i(TAG, "Fine location permission missing. Requesting from user");
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_GOOGLE_PLAY_SERVICES) {
			if (resultCode != Activity.RESULT_OK) {
				new AlertDialog.Builder(this)
						.setTitle(R.string.google_play_services_not_installed)
						.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								System.exit(2);
							}
						})
						.setIcon(android.R.drawable.ic_dialog_alert)
						.show();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == REQUEST_FINE_LOCATION) {
			if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
				new AlertDialog.Builder(this)
						.setTitle(R.string.location_fine_permission_not_given)
						.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								System.exit(3);
							}
						})
						.setIcon(android.R.drawable.ic_dialog_alert)
						.show();
			}
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void sendUpdateRequest(View view) {
		Intent updateIntent = AuroraPollingService.createUpdateIntent(this);
		WakefulIntentService.sendWakefulWork(this, updateIntent);
	}
}
