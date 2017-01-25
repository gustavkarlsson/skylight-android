package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import se.gustavkarlsson.aurora_notifier.android.evaluation.Complication;

public class ComplicationViewModel extends BaseObservable {

	private final Complication complication;

	ComplicationViewModel(Complication complication) {
		this.complication = complication;
	}

	@Bindable
	public Complication getComplication() {
		return complication;
	}
}
