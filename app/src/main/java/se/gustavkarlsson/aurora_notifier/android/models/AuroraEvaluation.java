package se.gustavkarlsson.aurora_notifier.android.models;

import android.location.Address;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagneticLocation;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SolarActivity;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SunPosition;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;

import static java.util.Collections.singletonList;
import static java8.util.stream.StreamSupport.stream;

@Parcel
public class AuroraEvaluation {
	long timestampMillis;
	Address address;
	AuroraFactors factors;
	List<AuroraComplication> complications;
	AuroraChance chance;

	AuroraEvaluation() {
	}

	public AuroraEvaluation(long timestampMillis, Address address, AuroraFactors factors, Collection<AuroraComplication> complications) {
		this.timestampMillis = timestampMillis;
		this.address = address;
		this.factors = factors;
		this.complications = sortComplications(complications);
		this.chance = calculateChance(complications);
	}

	private ArrayList<AuroraComplication> sortComplications(Collection<AuroraComplication> complications) {
		if (complications.isEmpty()) {
			return new ArrayList<>();
		}
		ArrayList<AuroraComplication> sorted = new ArrayList<>(complications);
		Collections.sort(sorted);
		return sorted;
	}

	private static AuroraChance calculateChance(Collection<AuroraComplication> complications) {
		return stream(complications)
				.map(AuroraComplication::getChance)
				.reduce((c1, c2) -> c1.ordinal() < c2.ordinal() ? c1 : c2)
				.orElse(AuroraChance.HIGH);
	}

	public static AuroraEvaluation createFallback() {
		AuroraFactors factors = new AuroraFactors(
				new SolarActivity(0),
				new GeomagneticLocation(0),
				new SunPosition(0),
				new Weather(0)
		);
		AuroraComplication unknownComplication = new AuroraComplication(
				AuroraChance.UNKNOWN,
				R.string.complication_no_data_title,
				R.string.complication_no_data_desc);
		return new AuroraEvaluation(0, null, factors, singletonList(unknownComplication));
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

	public List<AuroraComplication> getComplications() {
		return complications;
	}

	public AuroraChance getChance() {
		return chance;
	}

	@Override
	public String toString() {
		return "AuroraEvaluation{" +
				"timestampMillis=" + timestampMillis +
				", address=" + address +
				", factors=" + factors +
				", complications=" + complications +
				", chance=" + chance +
				'}';
	}
}
