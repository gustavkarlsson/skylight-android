package se.gustavkarlsson.aurora_notifier.android.gui.activities;

import android.content.Context;
import android.support.v4.app.ActivityCompat;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance;

public class AuroraChanceToColorConverter {
	private final Context context;

	public AuroraChanceToColorConverter(Context context) {
		this.context = context;
	}

	public int convert(AuroraChance auroraChance) {
		switch (auroraChance) {
			case UNKNOWN:
				return ActivityCompat.getColor(context, R.color.factor_unknown);
			case NONE:
				return ActivityCompat.getColor(context, R.color.factor_none);
			case LOW:
				return ActivityCompat.getColor(context, R.color.factor_low);
			case MEDIUM:
				return ActivityCompat.getColor(context, R.color.factor_medium);
			case HIGH:
				return ActivityCompat.getColor(context, R.color.factor_high);
		}
		throw new IllegalArgumentException("Unsupported aurora chance: " + auroraChance);
	}
}
