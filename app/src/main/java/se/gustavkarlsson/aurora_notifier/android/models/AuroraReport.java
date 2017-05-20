package se.gustavkarlsson.aurora_notifier.android.models;

import android.location.Address;

import org.parceler.Parcel;

import se.gustavkarlsson.aurora_notifier.android.models.factors.Darkness;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagActivity;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagLocation;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Visibility;

@Parcel
public class AuroraReport {
	long timestampMillis;
	Address address;
	AuroraFactors factors;

	AuroraReport() {
	}

	public AuroraReport(long timestampMillis, Address address, AuroraFactors factors) {
		this.timestampMillis = timestampMillis;
		this.address = address;
		this.factors = factors;
	}

	public static AuroraReport createFallback() {
		AuroraFactors factors = new AuroraFactors(
				new GeomagActivity(null),
				new GeomagLocation(null),
				new Darkness(null),
				new Visibility(null)
		);
		return new AuroraReport(0, null, factors);
	}

	public long getTimestampMillis() {
		return timestampMillis;
	}

	public Address getAddress() {
		return address;
	}

	public AuroraFactors getFactors() {
		return factors;
	}

	@Override
	public String toString() {
		return "AuroraReport{" +
				"timestampMillis=" + timestampMillis +
				", address=" + address +
				", factors=" + factors +
				'}';
	}
}
