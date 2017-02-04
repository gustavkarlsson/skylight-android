package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_chance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraEvaluationUpdateListener;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;

public class AuroraChanceFragment extends Fragment implements AuroraEvaluationUpdateListener {
	private static final String TAG = AuroraChanceFragment.class.getSimpleName();

	private static final long UPDATE_TIME_RESOLUTION_MILLIS = DateUtils.MINUTE_IN_MILLIS;

	@BindView(R.id.fragment_aurora_chance_root_view)
	View rootView;

	@BindView(R.id.chance)
	TextView chanceTextView;

	@BindView(R.id.time_since_update)
	TextView timeSinceUpdateTextView;

	@BindView(R.id.location)
	TextView locationTextView;

	private Unbinder unbinder;
	private Timer timeUpdateTimer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_aurora_chance, container, false);
		unbinder = ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onUpdate(AuroraEvaluation evaluation) {
		if (evaluation == null) {
			return;
		}
		updateLocationView(evaluation);
		scheduleTimeSinceUpdateRefresh(evaluation.getTimestampMillis());
		updateTimeSinceUpdate(evaluation.getTimestampMillis());
		chanceTextView.setText(evaluation.getChance().getResourceId());
		rootView.invalidate();
	}

	private void updateLocationView(AuroraEvaluation evaluation) {
		if (evaluation == null) {
			locationTextView.setVisibility(View.INVISIBLE);
		} else {
			locationTextView.setVisibility(View.VISIBLE);
			String locationString = evaluation.getAddress().getLocality();
			locationTextView.setText(locationString);
		}
	}

	private void scheduleTimeSinceUpdateRefresh(long updateTimestampMillis) {
		if (timeUpdateTimer != null) {
			timeUpdateTimer.cancel();
		}
		timeUpdateTimer = new Timer();
		timeUpdateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				timeSinceUpdateTextView.post(() -> updateTimeSinceUpdate(updateTimestampMillis));
			}
		}, 1000L, UPDATE_TIME_RESOLUTION_MILLIS);
	}

	private void updateTimeSinceUpdate(long updateTimeMillis) {
		if (isJustNow(updateTimeMillis)) {
			timeSinceUpdateTextView.setText(R.string.just_now);
			return;
		}
		CharSequence text = formatRelativeTime(updateTimeMillis);
		timeSinceUpdateTextView.setText(text);
		timeSinceUpdateTextView.invalidate();
	}

	private static boolean isJustNow(long timeMillis) {
		long ageMillis = System.currentTimeMillis() - timeMillis;
		return ageMillis <= UPDATE_TIME_RESOLUTION_MILLIS;
	}

	private static CharSequence formatRelativeTime(long startTimeMillis) {
		return DateUtils.getRelativeTimeSpanString(startTimeMillis, System.currentTimeMillis(), UPDATE_TIME_RESOLUTION_MILLIS);
	}

	@Override
	public void onDestroyView() {
		Log.v(TAG, "onDestroyView");
		timeUpdateTimer.cancel();
		unbinder.unbind();
		super.onDestroyView();
	}
}
