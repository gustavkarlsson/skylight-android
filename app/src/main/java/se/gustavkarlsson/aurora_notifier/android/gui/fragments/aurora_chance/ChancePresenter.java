package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_chance;

import android.widget.TextView;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraChance;

class ChancePresenter {
	private final TextView chanceTextView;

	ChancePresenter(TextView chanceTextView) {
		this.chanceTextView = chanceTextView;
	}

	void update(AuroraChance chance) {
		chanceTextView.setText(chance.getResourceId());
	}
}
