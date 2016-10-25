package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.Bindable;

import se.gustavkarlsson.aurora_notifier.android.realm.RealmKpIndex;

public class KpIndexViewModel extends RealmViewModel<RealmKpIndex> {
	public KpIndexViewModel(RealmKpIndex value) {
		super(value);
	}

	@Bindable
	public String getKpIndex() {
		return getValue().getKpIndex() == null ? "-" : "" + getValue().getKpIndex() + " @ " + getValue().getTimestamp();
	}
}
