package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import se.gustavkarlsson.aurora_notifier.android.realm.RealmKpIndex;

public class KpIndexViewModel extends BaseObservable {
	private final RealmKpIndex realmKpIndex;

	public KpIndexViewModel(RealmKpIndex realmKpIndex) {
		this.realmKpIndex = realmKpIndex;
	}

	@Bindable
	public String getKpIndex() {
		return realmKpIndex.getKpIndex() == null ? "-" : String.valueOf(realmKpIndex.getKpIndex());
	}

	@Bindable
	public String getTimestamp() {
		return realmKpIndex.getTimestamp() == null ? "-" : String.valueOf(realmKpIndex.getTimestamp());
	}
}
