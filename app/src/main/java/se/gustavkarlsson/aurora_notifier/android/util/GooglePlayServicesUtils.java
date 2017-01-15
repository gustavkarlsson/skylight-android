package se.gustavkarlsson.aurora_notifier.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import se.gustavkarlsson.aurora_notifier.android.R;

public class GooglePlayServicesUtils {
	private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1972;

	public static void ensureAvailable(Activity activity) {
		GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
		int connectionResult = availability.isGooglePlayServicesAvailable(activity);
		if (connectionResult != ConnectionResult.SUCCESS) {
			if (availability.isUserResolvableError(connectionResult)) {
				availability.showErrorDialogFragment(activity, connectionResult, REQUEST_GOOGLE_PLAY_SERVICES);
			} else {
				showNotSupportedErrorAndExit(activity);
			}
		}
	}

	private static void showNotSupportedErrorAndExit(Context context) {
		new AlertDialog.Builder(context)
                .setTitle(R.string.google_play_services_not_supported)
                .setMessage(R.string.app_will_close)
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(1);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
	}

	public static void showNotInstalledErrorAndExit(Context context) {
		new AlertDialog.Builder(context)
				.setTitle(R.string.google_play_services_was_not_installed)
				.setMessage(R.string.app_will_close)
				.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						System.exit(2);
					}
				})
				.setIcon(android.R.drawable.ic_dialog_alert)
				.show();
	}

	public static boolean googlePlayServicesFailedToInstall(int requestCode, int resultCode) {
		return requestCode == REQUEST_GOOGLE_PLAY_SERVICES && resultCode != Activity.RESULT_OK;
	}
}
