package se.gustavkarlsson.aurora_notifier.android.background;

import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class KpIndexService extends WakefulIntentService {

	private static final String TAG = KpIndexService.class.getSimpleName();

    public static final String ACTION_UPDATE = "se.gustavkarlsson.aurora_notifier.android.action.UPDATE";

	public KpIndexService() {
        super("KpIndexService");
    }

	@Override
	protected void doWakefulWork(Intent intent) {
		Log.v(TAG, "doWakefulWork");
		if (intent != null) {
			final String action = intent.getAction();
			if (ACTION_UPDATE.equals(action)) {
				update();
			}
		}
	}

	private void update() {
		Log.v(TAG, "update");
		// TODO Get Kp index from service and broadcast/notify
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
