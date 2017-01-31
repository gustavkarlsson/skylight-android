package se.gustavkarlsson.aurora_notifier.android.background.providers;

import android.location.Address;

public interface AddressProvider {
	Address getAddress(double latitude, double longitude, long timeoutMillis);
}
