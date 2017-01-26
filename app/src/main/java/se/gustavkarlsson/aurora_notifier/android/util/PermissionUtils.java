package se.gustavkarlsson.aurora_notifier.android.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.R;

public class PermissionUtils {
	private static final String TAG = PermissionUtils.class.getSimpleName();

	public static final int REQUEST_CODE_FINE_LOCATION = 1973;
	public static final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;

	private PermissionUtils() {
	}

	public static boolean hasLocationPermission(final Context context) {
		return ContextCompat.checkSelfPermission(context, LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED;
	}

	public static void requestLocationPermission(final Activity activity) {
		Log.i(TAG, LOCATION_PERMISSION + " permission missing. Requesting from user");
		if (ActivityCompat.shouldShowRequestPermissionRationale(activity, LOCATION_PERMISSION)) {
			showLocationRequestRationale(activity);
		} else {
			showLocationPermissionRequest(activity);
		}
	}

	private static void showLocationRequestRationale(final Activity activity) {
		new AlertDialog.Builder(activity)
				.setTitle(activity.getString(R.string.location_permission_required))
				.setMessage(activity.getString(R.string.location_permission_required_rationale))
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, (dialog, which) -> showLocationPermissionRequest(activity))
				.setNegativeButton(R.string.close, (dialog, which) -> System.exit(3))
				.setCancelable(false)
				.show();
	}

	private static void showLocationPermissionRequest(Activity activity) {
		ActivityCompat.requestPermissions(activity, new String[]{LOCATION_PERMISSION}, REQUEST_CODE_FINE_LOCATION);
	}
}
