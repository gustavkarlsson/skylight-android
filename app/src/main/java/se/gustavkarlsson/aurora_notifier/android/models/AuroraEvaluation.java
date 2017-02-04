package se.gustavkarlsson.aurora_notifier.android.models;

import android.location.Address;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.models.data.GeomagneticLocation;
import se.gustavkarlsson.aurora_notifier.android.models.data.SolarActivity;
import se.gustavkarlsson.aurora_notifier.android.models.data.SunPosition;
import se.gustavkarlsson.aurora_notifier.android.models.data.Weather;

import static java.util.Collections.singletonList;
import static java8.util.stream.StreamSupport.stream;

@Parcel
public class AuroraEvaluation {
	long timestampMillis;
	Address address;
	AuroraData data;
	List<AuroraComplication> complications;
	AuroraChance chance;

	AuroraEvaluation() {
	}

	public AuroraEvaluation(long timestampMillis, Address address, AuroraData data, Collection<AuroraComplication> complications) {
		this.timestampMillis = timestampMillis;
		this.address = address;
		this.data = data;
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
		AuroraData data = new AuroraData(
				new SolarActivity(0),
				new GeomagneticLocation(0),
				new SunPosition(0),
				new Weather(0)
		);
		AuroraComplication unknownComplication = new AuroraComplication(
				AuroraChance.UNKNOWN,
				R.string.complication_no_data_title,
				R.string.complication_no_data_desc);
		return new AuroraEvaluation(0, null, data, singletonList(unknownComplication));
	}

	public long getTimestampMillis() {
		return timestampMillis;
	}

	public Address getAddress() {
		return address;
	}

	public AuroraData getData() {
		return data;
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
				", data=" + data +
				", complications=" + complications +
				", chance=" + chance +
				'}';
	}
}
