package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.BaseObservable;

import io.realm.RealmObject;


abstract class RealmViewModel<T extends RealmObject> extends BaseObservable {
	private final T object;

	RealmViewModel(T object) {
		this.object = object;
	}

	T getObject() {
		return object;
	}
}
