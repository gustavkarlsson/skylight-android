package se.gustavkarlsson.aurora_notifier.android.gui.fragments;

import android.content.Context;
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
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraEvaluationProvider;
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraEvaluationUpdateReceiver;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.MainActivity;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;

public class AuroraChanceFragment extends Fragment implements AuroraEvaluationUpdateReceiver {
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
	private AuroraEvaluationProvider evaluationProvider;
	private Timer timeUpdateTimer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_aurora_chance, container, false);
		unbinder = ButterKnife.bind(this, rootView);
		update(getEvaluation());
		return rootView;
	}

	@Override
	public void onAttach(Context context) {
		Log.v(TAG, "onAttach");
		super.onAttach(context);
		evaluationProvider = (MainActivity) context;
	}

	@Override
	public void update(AuroraEvaluation evaluation) {
		if (evaluation == null) {
			return;
		}
		updateLocationView(evaluation);
		scheduleTimeSinceUpdateRefresh();
		updateTimeSinceUpdate(getEvaluation());
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

	private void scheduleTimeSinceUpdateRefresh() {
		if (timeUpdateTimer != null) {
			timeUpdateTimer.cancel();
		}
		timeUpdateTimer = new Timer();
		timeUpdateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				timeSinceUpdateTextView.post(() -> {
					AuroraEvaluation evaluation = getEvaluation();
					if (evaluation != null) {
						updateTimeSinceUpdate(evaluation);
					}
				});
			}
		}, 1000L, UPDATE_TIME_RESOLUTION_MILLIS);
	}

	private void updateTimeSinceUpdate(AuroraEvaluation evaluation) {
		if (isJustNow(evaluation.getTimestampMillis())) {
			timeSinceUpdateTextView.setText(R.string.just_now);
			return;
		}
		CharSequence text = formatRelativeTime(evaluation.getTimestampMillis());
		timeSinceUpdateTextView.setText(text);
		timeSinceUpdateTextView.invalidate();
	}

	private static boolean isJustNow(long timestampMillis) {
		long ageMillis = System.currentTimeMillis() - timestampMillis;
		return ageMillis <= UPDATE_TIME_RESOLUTION_MILLIS;
	}

	private static CharSequence formatRelativeTime(long startTimeMillis) {
		return DateUtils.getRelativeTimeSpanString(startTimeMillis, System.currentTimeMillis(), UPDATE_TIME_RESOLUTION_MILLIS);
	}

	private AuroraEvaluation getEvaluation() {
		return evaluationProvider == null ? null : evaluationProvider.getEvaluation();
	}

	@Override
	public void onDestroyView() {
		Log.v(TAG, "onDestroyView");
		timeUpdateTimer.cancel();
		unbinder.unbind();
		super.onDestroyView();
	}
}
