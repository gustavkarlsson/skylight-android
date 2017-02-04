package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_chance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraEvaluationUpdateListener;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;

public class AuroraChanceFragment extends Fragment implements AuroraEvaluationUpdateListener {
	private static final String TAG = AuroraChanceFragment.class.getSimpleName();

	private View rootView;

	private LocationPresenter locationPresenter;
	private TimeSinceUpdatePresenter timeSinceUpdatePresenter;
	private ChancePresenter chancePresenter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		rootView = inflater.inflate(R.layout.fragment_aurora_chance, container, false);

		locationPresenter = new LocationPresenter((TextView) rootView.findViewById(R.id.location));
		timeSinceUpdatePresenter = new TimeSinceUpdatePresenter((TextView) rootView.findViewById(R.id.time_since_update), DateUtils.MINUTE_IN_MILLIS);
		chancePresenter = new ChancePresenter((TextView) rootView.findViewById(R.id.chance));
		return rootView;
	}

	@Override
	public void onUpdate(AuroraEvaluation evaluation) {
		locationPresenter.update(evaluation.getAddress());
		timeSinceUpdatePresenter.update(evaluation.getTimestampMillis());
		chancePresenter.update(evaluation.getChance());
		rootView.invalidate();
	}

	@Override
	public void onDestroyView() {
		Log.v(TAG, "onDestroyView");
		timeSinceUpdatePresenter.destroy();
		super.onDestroyView();
	}
}
