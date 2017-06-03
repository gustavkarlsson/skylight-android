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
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.observers.DataObserver;
import se.gustavkarlsson.skylight.android.observers.ObservableData;

import static se.gustavkarlsson.skylight.android.dagger.modules.definitive.LatestAuroraReportObservableModule.LATEST_NAME;
import static se.gustavkarlsson.skylight.android.dagger.modules.replaceable.FragmentRootViewModule.FRAGMENT_ROOT_NAME;

public class AuroraChanceFragment extends Fragment implements DataObserver<AuroraReport> {
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

	@Inject
	@Named(LATEST_NAME)
	ObservableData<AuroraReport> latestAuroraReport;

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
		latestAuroraReport.addListener(this);
		update(latestAuroraReport.getData());
		timeSinceUpdatePresenter.onStart();
	}

	@Override
	public void dataChanged(AuroraReport report) {
		Log.v(TAG, "dataChanged");
		getActivity().runOnUiThread(() -> update(report));
	}

	private void update(AuroraReport report) {
		locationPresenter.onUpdate(report.getAddress());
		timeSinceUpdatePresenter.onUpdate(report.getTimestampMillis());
		Chance chance = evaluator.evaluate(report);
		chancePresenter.onUpdate(ChanceLevel.fromChance(chance));
	}

	@Override
	public void onStop() {
		Log.v(TAG, "onStop");
		latestAuroraReport.removeListener(this);
		super.onStop();
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
		latestAuroraReport = null;
		super.onDestroyView();
	}
}
