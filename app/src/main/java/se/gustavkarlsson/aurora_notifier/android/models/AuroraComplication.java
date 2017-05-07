package se.gustavkarlsson.aurora_notifier.android.models;

import android.support.annotation.NonNull;

import org.parceler.Parcel;

@Parcel
public class AuroraComplication implements Comparable<AuroraComplication> {
	AuroraChance chance;
	int titleStringResource;
	int descriptionStringResource;

	AuroraComplication() {
	}

	public AuroraComplication(AuroraChance chance, int titleStringResource, int descriptionStringResource) {
		this.chance = chance;
		this.titleStringResource = titleStringResource;
		this.descriptionStringResource = descriptionStringResource;
	}

	public AuroraChance getChance() {
		return chance;
	}

	public int getTitleStringResource() {
		return titleStringResource;
	}

	public int getDescriptionStringResource() {
		return descriptionStringResource;
	}

	@Override
	public int compareTo(@NonNull AuroraComplication other) {
		return chance.compareTo(other.getChance());
	}

	// TODO implement equals to conform with compareTo (Generally, the value of compareTo should return zero if and only if equals returns true)

	@Override
	public String toString() {
		return "AuroraComplication{" +
				"chance=" + chance +
				", titleStringResource=" + titleStringResource +
				", descriptionStringResource=" + descriptionStringResource +
				'}';
	}
}
