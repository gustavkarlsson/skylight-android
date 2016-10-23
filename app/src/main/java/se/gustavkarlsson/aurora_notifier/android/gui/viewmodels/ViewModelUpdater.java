package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.BaseObservable;

import io.realm.RealmChangeListener;
import io.realm.RealmObject;

public class ViewModelUpdater implements RealmChangeListener<RealmObject> {
    private final BaseObservable viewModel;

    public ViewModelUpdater(BaseObservable viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void onChange(RealmObject realmObject) {
        viewModel.notifyChange();
    }
}
