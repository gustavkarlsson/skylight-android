package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance;

import android.widget.TextView;

import se.gustavkarlsson.skylight.android.evaluation.ChanceLevel;

class ChancePresenter {
	private final TextView chanceTextView;

	ChancePresenter(TextView chanceTextView) {
		this.chanceTextView = chanceTextView;
	}

	void onUpdate(ChanceLevel chanceLevel) {
		chanceTextView.setText(chanceLevel.getResourceId());
	}
}
