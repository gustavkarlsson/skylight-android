package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.ColorUtils;

import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.evaluation.Chance;

class ChanceToColorConverter {
	private final int unknownColor;
	private final int lowestColor;
	private final int highestColor;

	ChanceToColorConverter(Context context) {
		unknownColor = ActivityCompat.getColor(context, R.color.chance_unknown);
		lowestColor = ActivityCompat.getColor(context, R.color.chance_lowest);
		highestColor = ActivityCompat.getColor(context, R.color.chance_highest);
	}

	int convert(Chance chance) {
		if (!chance.isKnown()) {
			return unknownColor;
		}

		return ColorUtils.blendARGB(lowestColor, highestColor, chance.getValue().floatValue());
	}
}
