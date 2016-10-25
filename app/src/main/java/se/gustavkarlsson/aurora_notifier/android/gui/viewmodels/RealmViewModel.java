package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.BaseObservable;

import io.realm.RealmObject;


public abstract class RealmViewModel<T extends RealmObject> extends BaseObservable {
	private final T value;

	RealmViewModel(T value) {
		this.value = value;
	}

	T getValue() {
		return value;
	}
}
