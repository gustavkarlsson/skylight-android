package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_chance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraEvaluationUpdateListener;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;

public class AuroraChanceFragment extends Fragment implements AuroraEvaluationUpdateListener {
	private static final String TAG = AuroraChanceFragment.class.getSimpleName();

	@BindView(R.id.fragment_aurora_chance_root_view)
	View rootView;

	@BindView(R.id.chance)
	TextView chanceTextView;

	@BindView(R.id.time_since_update)
	TextView timeSinceUpdateTextView;

	@BindView(R.id.location)
	TextView locationTextView;

	private LocationPresenter locationPresenter;
	private TimeSinceUpdatePresenter timeSinceUpdatePresenter;
	private ChancePresenter chancePresenter;

	private Unbinder unbinder;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_aurora_chance, container, false);
		unbinder = ButterKnife.bind(this, rootView);
		locationPresenter = new LocationPresenter(locationTextView);
		timeSinceUpdatePresenter = new TimeSinceUpdatePresenter(timeSinceUpdateTextView, DateUtils.MINUTE_IN_MILLIS);
		chancePresenter = new ChancePresenter(chanceTextView);
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
		unbinder.unbind();
		super.onDestroyView();
	}
}
