package se.gustavkarlsson.aurora_notifier.android.gui.activities;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.ColorUtils;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.evaluation.Chance;

public class ChanceToColorConverter {
	private final int unknownColor;
	private final int lowestColor;
	private final int highestColor;

	public ChanceToColorConverter(Context context) {
		unknownColor = ActivityCompat.getColor(context, R.color.chance_unknown);
		lowestColor = ActivityCompat.getColor(context, R.color.chance_lowest);
		highestColor = ActivityCompat.getColor(context, R.color.chance_highest);
	}

	public int convert(Chance chance) {
		if (!chance.isKnown()) {
			return unknownColor;
		}

		return ColorUtils.blendARGB(lowestColor, highestColor, chance.getValue().floatValue());
	}
}
