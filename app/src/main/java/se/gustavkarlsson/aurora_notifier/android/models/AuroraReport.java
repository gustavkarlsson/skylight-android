package se.gustavkarlsson.aurora_notifier.android.models;

import android.location.Address;

import org.parceler.Parcel;

import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagActivity;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagLocation;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SunPosition;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;

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
				new GeomagActivity(0),
				new GeomagLocation(0),
				new SunPosition(0),
				new Weather(0)
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
