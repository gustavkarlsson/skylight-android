package se.gustavkarlsson.aurora_notifier.android.util;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import se.gustavkarlsson.aurora_notifier.android.R;

public class GooglePlayServicesUtils {
	public static final int REQUEST_CODE_GOOGLE_PLAY_SERVICES = 1972;

	private GooglePlayServicesUtils() {
	}

	public static boolean ensureAvailable(Activity activity) {
		GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
		int resultCode = availability.isGooglePlayServicesAvailable(activity);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (availability.isUserResolvableError(resultCode)) {
				handleUserResolvableError(activity, availability, resultCode);
			} else {
				showNotSupportedErrorAndExit(activity);
			}
			return false;
		}
		return true;
	}

	private static void handleUserResolvableError(Activity activity, GoogleApiAvailability availability, int resultCode) {
		availability.getErrorDialog(activity, resultCode, REQUEST_CODE_GOOGLE_PLAY_SERVICES,
				dialog -> showRationaleDialog(activity, availability, resultCode))
				.show();
	}

	private static void showRationaleDialog(Activity activity, GoogleApiAvailability availability, int resultCode) {
		new AlertDialog.Builder(activity)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(activity.getString(R.string.google_play_services_required))
				.setMessage(activity.getString(R.string.google_play_services_required_rationale))
				.setPositiveButton(android.R.string.yes, (dialog, which) -> handleUserResolvableError(activity, availability, resultCode))
				.setNegativeButton(R.string.close, (dialog, which) -> System.exit(1))
				.setCancelable(false)
				.show();
	}

	private static void showNotSupportedErrorAndExit(Context context) {
		new AlertDialog.Builder(context)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.google_play_services_not_supported)
				.setMessage(R.string.app_will_close)
				.setPositiveButton(R.string.close, (dialog, which) -> System.exit(2))
				.setCancelable(false)
				.show();
	}

	public static void showNotInstalledErrorAndExit(Context context) {
		new AlertDialog.Builder(context)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.google_play_services_could_not_be_installed)
				.setMessage(R.string.app_will_close)
				.setPositiveButton(R.string.close, (dialog, which) -> System.exit(3))
				.setCancelable(false)
				.show();
	}

	public static boolean googlePlayServicesFailedToInstall(int requestCode, int resultCode) {
		return requestCode == REQUEST_CODE_GOOGLE_PLAY_SERVICES && resultCode != Activity.RESULT_OK;
	}
}
