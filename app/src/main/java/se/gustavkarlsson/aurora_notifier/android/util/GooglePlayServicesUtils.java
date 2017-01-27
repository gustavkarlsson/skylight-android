package se.gustavkarlsson.aurora_notifier.android.util;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import se.gustavkarlsson.aurora_notifier.android.R;

public class GooglePlayServicesUtils {
	private static final int REQUEST_CODE_GOOGLE_PLAY_SERVICES = 1972;

	private GooglePlayServicesUtils() {
	}

	public static void ensureAvailable(Activity activity) {
		GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
		int resultCode = availability.isGooglePlayServicesAvailable(activity);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (availability.isUserResolvableError(resultCode)) {
				availability.showErrorDialogFragment(activity, resultCode, REQUEST_CODE_GOOGLE_PLAY_SERVICES);
			} else {
				showNotSupportedErrorAndExit(activity);
			}
		}
	}

	private static void showNotSupportedErrorAndExit(Context context) {
		new AlertDialog.Builder(context)
				.setTitle(R.string.google_play_services_not_supported)
				.setMessage(R.string.app_will_close)
				.setPositiveButton(R.string.close, (dialog, which) -> System.exit(1))
				.setIcon(android.R.drawable.ic_dialog_alert)
				.show();
	}

	public static void showNotInstalledErrorAndExit(Context context) {
		new AlertDialog.Builder(context)
				.setTitle(R.string.google_play_services_could_not_be_installed)
				.setMessage(R.string.app_will_close)
				.setPositiveButton(R.string.close, (dialog, which) -> System.exit(2))
				.setIcon(android.R.drawable.ic_dialog_alert)
				.show();
	}

	public static boolean googlePlayServicesFailedToInstall(int requestCode, int resultCode) {
		return requestCode == REQUEST_CODE_GOOGLE_PLAY_SERVICES && resultCode != Activity.RESULT_OK;
	}
}
