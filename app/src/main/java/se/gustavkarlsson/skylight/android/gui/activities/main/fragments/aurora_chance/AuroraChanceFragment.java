package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;
import javax.inject.Named;

import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.FragmentRootViewModule;
import se.gustavkarlsson.skylight.android.evaluation.Chance;
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.skylight.android.evaluation.ChanceLevel;
import se.gustavkarlsson.skylight.android.gui.AuroraReportUpdateListener;
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity;
import se.gustavkarlsson.skylight.android.models.AuroraReport;

import static se.gustavkarlsson.skylight.android.dagger.modules.replaceable.FragmentRootViewModule.FRAGMENT_ROOT_NAME;

public class AuroraChanceFragment extends Fragment implements AuroraReportUpdateListener {
	private static final String TAG = AuroraChanceFragment.class.getSimpleName();

	@Inject
	ChanceEvaluator<AuroraReport> evaluator;

	@Inject
	@Named(FRAGMENT_ROOT_NAME)
	View rootView;

	@Inject
	LocationPresenter locationPresenter;

	@Inject
	TimeSinceUpdatePresenter timeSinceUpdatePresenter;

	@Inject
	ChancePresenter chancePresenter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		((MainActivity) getActivity()).getComponent()
				.getAuroraChanceFragmentComponent(new FragmentRootViewModule(inflater, container, R.layout.fragment_aurora_chance))
				.inject(this);
		return rootView;
	}

	@Override
	public void onStart() {
		Log.v(TAG, "onStart");
		super.onStart();
		timeSinceUpdatePresenter.onStart();
	}

	@Override
	public void onUpdate(AuroraReport report) {
		Log.v(TAG, "onUpdate");
		locationPresenter.onUpdate(report.getAddress());
		timeSinceUpdatePresenter.onUpdate(report.getTimestampMillis());
		Chance chance = evaluator.evaluate(report);
		chancePresenter.onUpdate(ChanceLevel.fromChance(chance));
		rootView.invalidate();
	}

	@Override
	public void onDestroyView() {
		Log.v(TAG, "onDestroyView");
		timeSinceUpdatePresenter.destroy();
		evaluator = null;
		rootView = null;
		locationPresenter = null;
		timeSinceUpdatePresenter = null;
		chancePresenter = null;
		super.onDestroyView();
	}
}
