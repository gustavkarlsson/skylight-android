package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.gui.AuroraReportUpdateListener;
import se.gustavkarlsson.skylight.android.models.AuroraFactors;
import se.gustavkarlsson.skylight.android.models.AuroraReport;

public class AuroraFactorFragment extends Fragment implements AuroraReportUpdateListener {
	private static final String TAG = AuroraFactorFragment.class.getSimpleName();

	private View rootView;
	private GeomagActivityPresenter geomagActivityPresenter;
	private GeomagLocationPresenter geomagLocationPresenter;
	private VisibilityPresenter visibilityPresenter;
	private DarknessPresenter darknessPresenter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		rootView = inflater.inflate(R.layout.fragment_aurora_factors, container, false);
		geomagActivityPresenter = new GeomagActivityPresenter((AuroraFactorView) rootView.findViewById(R.id.aurora_factor_geomag_activity));
		geomagLocationPresenter = new GeomagLocationPresenter((AuroraFactorView) rootView.findViewById(R.id.aurora_factor_geomag_location));
		visibilityPresenter = new VisibilityPresenter((AuroraFactorView) rootView.findViewById(R.id.aurora_factor_visibility));
		darknessPresenter = new DarknessPresenter((AuroraFactorView) rootView.findViewById(R.id.aurora_factor_darkness));
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