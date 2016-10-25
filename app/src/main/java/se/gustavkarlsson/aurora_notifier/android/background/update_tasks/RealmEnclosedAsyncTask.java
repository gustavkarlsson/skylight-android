package se.gustavkarlsson.aurora_notifier.android.background.update_tasks;

import android.os.AsyncTask;

import io.realm.Realm;

abstract class RealmEnclosedAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    @Override
    protected final Result doInBackground(Params[] params) {
        Realm realm = Realm.getDefaultInstance();
        try {
            return doInBackgroundWithRealm(realm, params);
        } finally {
            if (!realm.isClosed()) {
                realm.close();
            }
        }
    }

    protected abstract Result doInBackgroundWithRealm(Realm realm, Params[] params);
}
