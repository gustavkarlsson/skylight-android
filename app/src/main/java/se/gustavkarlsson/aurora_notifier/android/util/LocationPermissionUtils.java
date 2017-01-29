package se.gustavkarlsson.aurora_notifier.android.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.realm.Requirements;

public class LocationPermissionUtils {
	private static final String TAG = LocationPermissionUtils.class.getSimpleName();

	private static final int REQUEST_CODE_LOCATION_PERMISSION = 1973;
	private static final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;

	private LocationPermissionUtils() {
	}

	public static boolean ensurePermission(Activity activity) {
		boolean hasPermission = ContextCompat.checkSelfPermission(activity, LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED;
		if (!hasPermission) {
			requestPermission(activity);
		}
		return hasPermission;
	}

	private static void requestPermission(Activity activity) {
		Log.i(TAG, LOCATION_PERMISSION + " permission missing. Requesting from user");
		if (ActivityCompat.shouldShowRequestPermissionRationale(activity, LOCATION_PERMISSION)) {
			showLocationRequestRationale(activity);
		} else {
			showLocationPermissionRequest(activity);
		}
	}

	private static void showLocationRequestRationale(Activity activity) {
		new AlertDialog.Builder(activity)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(activity.getString(R.string.location_permission_required_title))
				.setMessage(activity.getString(R.string.location_permission_required_description))
				.setPositiveButton(android.R.string.yes, (dialog, which) -> showLocationPermissionRequest(activity))
				.setNegativeButton(R.string.exit, (dialog, which) -> {
					Requirements.setFulfilled(false);
					System.exit(4);
				})
				.setCancelable(false)
				.show();
	}

	private static void showLocationPermissionRequest(Activity activity) {
		ActivityCompat.requestPermissions(activity, new String[]{LOCATION_PERMISSION}, REQUEST_CODE_LOCATION_PERMISSION);
	}
}
