package se.gustavkarlsson.aurora_notifier.android.gui.activities;

import android.content.Context;
import android.support.v4.app.ActivityCompat;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.evaluation.Chance;
import se.gustavkarlsson.aurora_notifier.android.evaluation.PresentableChance;

public class AuroraChanceToColorConverter {
	private final Context context;

	public AuroraChanceToColorConverter(Context context) {
		this.context = context;
	}

	public int convert(Chance chance) {
		PresentableChance presentableChance = PresentableChance.fromChance(chance);
		switch (presentableChance) {
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
		throw new IllegalArgumentException("Unsupported aurora chance: " + presentableChance);
	}
}
