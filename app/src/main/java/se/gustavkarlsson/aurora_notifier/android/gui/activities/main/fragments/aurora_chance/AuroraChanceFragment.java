package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_chance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChanceEvaluator;
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraEvaluationUpdateListener;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraChance;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;

public class AuroraChanceFragment extends Fragment implements AuroraEvaluationUpdateListener {
	private static final String TAG = AuroraChanceFragment.class.getSimpleName();

	private View rootView;
	private LocationPresenter locationPresenter;
	private TimeSinceUpdatePresenter timeSinceUpdatePresenter;
	private ChancePresenter chancePresenter;
	private AuroraChanceEvaluator evaluator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		rootView = inflater.inflate(R.layout.fragment_aurora_chance, container, false);
		locationPresenter = new LocationPresenter((TextView) rootView.findViewById(R.id.location));
		timeSinceUpdatePresenter = new TimeSinceUpdatePresenter((TextView) rootView.findViewById(R.id.time_since_update), DateUtils.MINUTE_IN_MILLIS);
		chancePresenter = new ChancePresenter((TextView) rootView.findViewById(R.id.chance));
		this.evaluator = e -> AuroraChance.UNKNOWN; // FIXME assign evaluator
		return rootView;
	}

	@Override
	public void onStart() {
		Log.v(TAG, "onStart");
		super.onStart();
		timeSinceUpdatePresenter.onStart();
	}

	@Override
	public void onUpdate(AuroraEvaluation evaluation) {
		Log.v(TAG, "onUpdate");
		locationPresenter.onUpdate(evaluation.getAddress());
		timeSinceUpdatePresenter.onUpdate(evaluation.getTimestampMillis());
		AuroraChance chance = evaluator.evaluate(evaluation);
		chancePresenter.onUpdate(chance);
		rootView.invalidate();
	}

	@Override
	public void onDestroyView() {
		Log.v(TAG, "onDestroyView");
		timeSinceUpdatePresenter.destroy();
		super.onDestroyView();
	}
}
