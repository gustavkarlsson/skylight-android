package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_chance;

import android.widget.TextView;

import se.gustavkarlsson.aurora_notifier.android.evaluation.PresentableChance;

class ChancePresenter {
	private final TextView chanceTextView;

	ChancePresenter(TextView chanceTextView) {
		this.chanceTextView = chanceTextView;
	}

	void onUpdate(PresentableChance chance) {
		chanceTextView.setText(chance.getResourceId());
	}
}
