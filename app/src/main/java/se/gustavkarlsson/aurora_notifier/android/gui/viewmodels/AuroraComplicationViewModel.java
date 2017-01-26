package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;

public class AuroraComplicationViewModel extends BaseObservable {
	private AuroraComplication complication;

	public AuroraComplicationViewModel(AuroraComplication complication) {
		this.complication = complication;
	}

	@Bindable
	public AuroraComplication getComplication() {
		return complication;
	}
}
