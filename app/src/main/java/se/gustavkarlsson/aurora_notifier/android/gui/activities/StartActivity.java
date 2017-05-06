package se.gustavkarlsson.aurora_notifier.android.gui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.Task;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.UpdateScheduler;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.main.MainActivity;
import se.gustavkarlsson.aurora_notifier.android.realm.Requirements;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class StartActivity extends AppCompatActivity {
	private static final String TAG = StartActivity.class.getSimpleName();

	public static final int REQUEST_CODE_LOCATION_PERMISSION = 1973;
	public static final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		Task<Void> gpsAvaliable = GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);
		gpsAvaliable.addOnFailureListener(e -> showGooglePlayServicesNotInstalledErrorAndExit());
		gpsAvaliable.addOnSuccessListener(aVoid -> checkLocationPermission(this::startMain, this::requestLocationPermission));
	}

	private void showGooglePlayServicesNotInstalledErrorAndExit() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.error_google_play_services_could_not_be_installed)
				.setMessage(R.string.app_will_close)
				.setPositiveButton(R.string.exit, (dialog, which) -> {
					Requirements.setFulfilled(false);
					System.exit(3);
				})
				.setCancelable(false)
				.show();
	}

	private void checkLocationPermission(Runnable onSuccess, Runnable onFailure) {
		boolean hasPermission = ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED;
		if (!hasPermission) {
			onFailure.run();
		} else {
			onSuccess.run();
		}
	}

	public void requestLocationPermission() {
		Log.i(TAG, LOCATION_PERMISSION + " permission missing. Requesting from user");
		if (ActivityCompat.shouldShowRequestPermissionRationale(this, LOCATION_PERMISSION)) {
			showLocationRequestRationale();
		} else {
			showLocationPermissionRequest();
		}
	}

	private void showLocationRequestRationale() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(getString(R.string.location_permission_required_title))
				.setMessage(getString(R.string.location_permission_required_description))
				.setPositiveButton(android.R.string.yes, (dialog, which) -> showLocationPermissionRequest())
				.setNegativeButton(R.string.exit, (dialog, which) -> {
					Requirements.setFulfilled(false);
					System.exit(4);
				})
				.setCancelable(false)
				.show();
	}

	private void showLocationPermissionRequest() {
		ActivityCompat.requestPermissions(this, new String[]{LOCATION_PERMISSION}, REQUEST_CODE_LOCATION_PERMISSION);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		Log.v(TAG, "onRequestPermissionsResult");
		if (REQUEST_CODE_LOCATION_PERMISSION == requestCode) {
			for (int i = 0; i < permissions.length; i++) {
				String permission = permissions[i];
				int result = grantResults[i];
				if (LOCATION_PERMISSION.equals(permission)) {
					if (PERMISSION_GRANTED != result) {
						requestLocationPermission();
					} else {
						startMain();
					}
				}
			}
		}
	}

	private void startMain() {
		Requirements.setFulfilled(true);
		UpdateScheduler.setupUpdateScheduling(this, true);
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
