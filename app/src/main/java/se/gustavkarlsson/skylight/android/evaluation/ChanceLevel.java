package se.gustavkarlsson.skylight.android.evaluation;

import se.gustavkarlsson.skylight.android.R;

public enum ChanceLevel {
	// Warning. These ordinals relate directly to pref_trigger_level_values
	UNKNOWN(R.string.aurora_chance_unknown),
	NONE(R.string.aurora_chance_none),
	LOW(R.string.aurora_chance_low),
	MEDIUM(R.string.aurora_chance_medium),
	HIGH(R.string.aurora_chance_high);

	final int resourceId;

	ChanceLevel(int resourceId) {
		this.resourceId = resourceId;
	}

	public int getResourceId() {
		return resourceId;
	}

	public static ChanceLevel fromChance(Chance chance) {
		if (!chance.isKnown()) {
			return UNKNOWN;
		}
		if (!chance.isPossible()) {
			return NONE;
		}

		double value = chance.getValue();
		if (value < 0.33) {
			return LOW;
		} else if (value < 0.66) {
			return MEDIUM;
		} else {
			return HIGH;
		}
	}
}