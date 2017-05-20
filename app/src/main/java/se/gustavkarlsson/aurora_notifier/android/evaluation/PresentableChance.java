package se.gustavkarlsson.aurora_notifier.android.evaluation;

import se.gustavkarlsson.aurora_notifier.android.R;

public enum PresentableChance {
	UNKNOWN(R.string.aurora_chance_unknown),
	NONE(R.string.aurora_chance_none),
	LOW(R.string.aurora_chance_low),
	MEDIUM(R.string.aurora_chance_medium),
	HIGH(R.string.aurora_chance_high);

	final int resourceId;

	PresentableChance(int resourceId) {
		this.resourceId = resourceId;
	}

	public int getResourceId() {
		return resourceId;
	}

	public static PresentableChance fromChance(Chance chance) {
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
