package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance;

import android.widget.TextView;

import se.gustavkarlsson.skylight.android.evaluation.ChanceLevel;

public class ChancePresenter {
	private final TextView chanceTextView;

	public ChancePresenter(TextView chanceTextView) {
		this.chanceTextView = chanceTextView;
	}

	void onUpdate(ChanceLevel chanceLevel) {
		chanceTextView.setText(chanceLevel.getResourceId());
	}
}
