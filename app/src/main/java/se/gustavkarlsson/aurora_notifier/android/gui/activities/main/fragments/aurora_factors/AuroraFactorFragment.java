package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_factors;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraReportUpdateListener;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraFactors;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraReport;

public class AuroraFactorFragment extends Fragment implements AuroraReportUpdateListener {
	private static final String TAG = AuroraFactorFragment.class.getSimpleName();

	private View rootView;
	private SolarActivityPresenter solarActivityPresenter;
	private GeomagneticLocationPresenter geomagneticLocationPresenter;
	private WeatherPresenter weatherPresenter;
	private SunPositionPresenter sunPositionPresenter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		rootView = inflater.inflate(R.layout.fragment_aurora_factors, container, false);
		solarActivityPresenter = new SolarActivityPresenter((AuroraFactorView) rootView.findViewById(R.id.aurora_factor_solar_activity));
		geomagneticLocationPresenter = new GeomagneticLocationPresenter((AuroraFactorView) rootView.findViewById(R.id.aurora_factor_geomagnetic_location));
		weatherPresenter = new WeatherPresenter((AuroraFactorView) rootView.findViewById(R.id.aurora_factor_weather));
		sunPositionPresenter = new SunPositionPresenter((AuroraFactorView) rootView.findViewById(R.id.aurora_factor_sun_position));
		return rootView;
	}

	@Override
	public void onUpdate(AuroraReport report) {
		AuroraFactors factors = report.getFactors();
		solarActivityPresenter.onUpdate(factors.getSolarActivity());
		geomagneticLocationPresenter.onUpdate(factors.getGeomagneticLocation());
		weatherPresenter.onUpdate(factors.getWeather());
		sunPositionPresenter.onUpdate(factors.getSunPosition());
		rootView.invalidate();
	}
}
