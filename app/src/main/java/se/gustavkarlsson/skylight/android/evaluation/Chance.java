package se.gustavkarlsson.skylight.android.evaluation;

import android.support.annotation.NonNull;

public final class Chance implements Comparable<Chance> {
	private final Double value;

	// TODO convert these to singletons
	public static Chance unknown() {
		return new Chance(null);
	}

	public static Chance impossible() {
		return new Chance(0.0);
	}

	public static Chance of(double value) {
		return new Chance(value);
	}

	private Chance(Double value) {
		if (value == null) {
			this.value = null;
			return;
		}
		value = Math.max(0.0, value);
		value = Math.min(1.0, value);
		this.value = value;
	}

	public boolean isKnown() {
		return value != null;
	}

	public boolean isPossible() {
		return value != null && value > 0.01;
	}

	public Double getValue() {
		return value;
	}

	@Override
	public int compareTo(@NonNull Chance o) {
		if (!isKnown() && !o.isKnown()) {
			return 0;
		}
		if (!isKnown()) {
			return -1;
		}
		if (!o.isKnown()) {
			return 1;
		}
		return Double.compare(value, o.value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Chance chance = (Chance) o;

		return value != null ? value.equals(chance.value) : chance.value == null;

	}

	@Override
	public int hashCode() {
		return value != null ? value.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "Chance{" +
				"value=" + value +
				'}';
	}
}
