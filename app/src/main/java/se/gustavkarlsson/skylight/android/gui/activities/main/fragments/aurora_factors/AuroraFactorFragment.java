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
import se.gustavkarlsson.skylight.android.gui.AuroraReportUpdateListener;
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity;
import se.gustavkarlsson.skylight.android.models.AuroraFactors;
import se.gustavkarlsson.skylight.android.models.AuroraReport;

import static se.gustavkarlsson.skylight.android.dagger.modules.replaceable.FragmentRootViewModule.FRAGMENT_ROOT_NAME;

public class AuroraFactorFragment extends Fragment implements AuroraReportUpdateListener {
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		((MainActivity) getActivity()).getComponent()
				.getAuroraFactorsFragmentComponent(new FragmentRootViewModule(inflater, container, R.layout.fragment_aurora_factors))
				.inject(this);
		return rootView;
	}

	@Override
	public void onDestroyView() {
		Log.v(TAG, "onDestroyView");
		rootView = null;
		geomagActivityPresenter = null;
		geomagLocationPresenter = null;
		visibilityPresenter = null;
		darknessPresenter = null;
		super.onDestroyView();
	}

	@Override
	public void onUpdate(AuroraReport report) {
		AuroraFactors factors = report.getFactors();
		geomagActivityPresenter.onUpdate(factors.getGeomagActivity());
		geomagLocationPresenter.onUpdate(factors.getGeomagLocation());
		visibilityPresenter.onUpdate(factors.getVisibility());
		darknessPresenter.onUpdate(factors.getDarkness());
		rootView.invalidate();
	}
}
