package se.gustavkarlsson.skylight.android.models;

import android.location.Address;

import org.parceler.Parcel;

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
