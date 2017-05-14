package se.gustavkarlsson.aurora_notifier.android.evaluation;

import se.gustavkarlsson.aurora_notifier.android.R;

public enum AuroraChance {
	UNKNOWN(R.string.aurora_chance_unknown),
	NONE(R.string.aurora_chance_none),
	LOW(R.string.aurora_chance_low),
	MEDIUM(R.string.aurora_chance_medium),
	HIGH(R.string.aurora_chance_high);

	final int resourceId;

	AuroraChance(int resourceId) {
		this.resourceId = resourceId;
	}

	public int getResourceId() {
		return resourceId;
	}
}
