package se.gustavkarlsson.aurora_notifier.android.background;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;

import se.gustavkarlsson.aurora_notifier.android.R;

class UpdateJob extends Job {

	@NonNull
	@Override
	protected Result onRunJob(Params params) {
		int timeoutMillis = getContext().getResources().getInteger(R.integer.background_update_timeout_millis);
		Updater updater = new Updater(getContext(), timeoutMillis);
		boolean successful = updater.update();
		return successful ? Result.SUCCESS : Result.FAILURE;
	}
}
