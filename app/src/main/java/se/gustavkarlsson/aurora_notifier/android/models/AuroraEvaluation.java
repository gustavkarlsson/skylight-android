package se.gustavkarlsson.aurora_notifier.android.models;

import android.location.Address;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
