package se.gustavkarlsson.aurora_notifier.android.background.update_tasks;

import android.os.AsyncTask;

import io.realm.Realm;

abstract class RealmEnclosedAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    @Override
    protected final Result doInBackground(Params[] params) {
        try (Realm realm = Realm.getDefaultInstance()) {
            return doInBackgroundWithRealm(realm, params);
        }
    }

    protected abstract Result doInBackgroundWithRealm(Realm realm, Params[] params);
}
