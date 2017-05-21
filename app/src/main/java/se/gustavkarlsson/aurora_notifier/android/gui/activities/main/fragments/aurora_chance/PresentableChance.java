package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_chance;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.evaluation.Chance;

enum PresentableChance {
	UNKNOWN(R.string.aurora_chance_unknown),
	NONE(R.string.aurora_chance_none),
	LOW(R.string.aurora_chance_low),
	MEDIUM(R.string.aurora_chance_medium),
	HIGH(R.string.aurora_chance_high);

	final int resourceId;

	PresentableChance(int resourceId) {
		this.resourceId = resourceId;
	}

	int getResourceId() {
		return resourceId;
	}

	static PresentableChance fromChance(Chance chance) {
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
