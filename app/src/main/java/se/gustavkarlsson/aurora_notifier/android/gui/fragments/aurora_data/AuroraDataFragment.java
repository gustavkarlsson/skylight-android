package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_data;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraEvaluationUpdateListener;
import se.gustavkarlsson.aurora_notifier.android.gui.views.AuroraDataView;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraData;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;

public class AuroraDataFragment extends Fragment implements AuroraEvaluationUpdateListener {
	private static final String TAG = AuroraDataFragment.class.getSimpleName();

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
		rootView = inflater.inflate(R.layout.fragment_aurora_data, container, false);

		solarActivityPresenter = new SolarActivityPresenter((AuroraDataView) getActivity().findViewById(R.id.aurora_data_solar_activity));
		geomagneticLocationPresenter = new GeomagneticLocationPresenter((AuroraDataView) getActivity().findViewById(R.id.aurora_data_geomagnetic_location));
		weatherPresenter = new WeatherPresenter((AuroraDataView) getActivity().findViewById(R.id.aurora_data_weather));
		sunPositionPresenter = new SunPositionPresenter((AuroraDataView) getActivity().findViewById(R.id.aurora_data_sun_position));
		return rootView;
	}

	@Override
	public void onUpdate(AuroraEvaluation evaluation) {
		AuroraData data = evaluation.getData();
		solarActivityPresenter.update(data.getSolarActivity());
		geomagneticLocationPresenter.update(data.getGeomagneticLocation());
		weatherPresenter.update(data.getWeather());
		sunPositionPresenter.update(data.getSunPosition());
		rootView.invalidate();
	}
}
