package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors;

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
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity;
import se.gustavkarlsson.skylight.android.models.AuroraFactors;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.observers.DataObserver;
import se.gustavkarlsson.skylight.android.observers.ObservableData;

import static se.gustavkarlsson.skylight.android.dagger.modules.definitive.LatestAuroraReportObservableModule.LATEST_NAME;
import static se.gustavkarlsson.skylight.android.dagger.modules.replaceable.FragmentRootViewModule.FRAGMENT_ROOT_NAME;

public class AuroraFactorFragment extends Fragment implements DataObserver<AuroraReport> {
	private static final String TAG = AuroraFactorFragment.class.getSimpleName();

	@Inject
	@Named(FRAGMENT_ROOT_NAME)
	View rootView;

	@Inject
	GeomagActivityPresenter geomagActivityPresenter;

	@Inject
	GeomagLocationPresenter geomagLocationPresenter;

	@Inject
	VisibilityPresenter visibilityPresenter;

	@Inject
	DarknessPresenter darknessPresenter;

	@Inject
	@Named(LATEST_NAME)
	ObservableData<AuroraReport> latestAuroraReport;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		((MainActivity) getActivity()).getComponent()
				.getAuroraFactorsFragmentComponent(new FragmentRootViewModule(inflater, container, R.layout.fragment_aurora_factors))
				.inject(this);
		return rootView;
	}

	@Override
	public void onStart() {
		Log.v(TAG, "onStart");
		super.onStart();
		latestAuroraReport.addListener(this);
		updatePresenters(latestAuroraReport.getData());
	}

	@Override
	public void dataChanged(AuroraReport report) {
		Log.v(TAG, "dataChanged");
		getActivity().runOnUiThread(() -> updatePresenters(report));
	}

	private void updatePresenters(AuroraReport report) {
		AuroraFactors factors = report.getFactors();
		geomagActivityPresenter.update(factors.getGeomagActivity());
		geomagLocationPresenter.update(factors.getGeomagLocation());
		visibilityPresenter.update(factors.getVisibility());
		darknessPresenter.update(factors.getDarkness());
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
		rootView = null;
		geomagActivityPresenter = null;
		geomagLocationPresenter = null;
		visibilityPresenter = null;
		darknessPresenter = null;
		latestAuroraReport = null;
		super.onDestroyView();
	}
}
