package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_data;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraEvaluationUpdateListener;
import se.gustavkarlsson.aurora_notifier.android.gui.views.AuroraDataView;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraData;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;

public class AuroraDataFragment extends Fragment implements AuroraEvaluationUpdateListener {
	private static final String TAG = AuroraDataFragment.class.getSimpleName();

	@BindView(R.id.fragment_aurora_data_root_view)
	View rootView;

	@BindView(R.id.aurora_data_solar_activity)
	AuroraDataView solarActivityView;

	@BindView(R.id.aurora_data_geomagnetic_location)
	AuroraDataView geomagneticLocationView;

	@BindView(R.id.aurora_data_weather)
	AuroraDataView weatherView;

	@BindView(R.id.aurora_data_sun_position)
	AuroraDataView sunPositionView;

	private SolarActivityPresenter solarActivityPresenter;
	private GeomagneticLocationPresenter geomagneticLocationPresenter;
	private WeatherPresenter weatherPresenter;
	private SunPositionPresenter sunPositionPresenter;

	private Unbinder unbinder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_aurora_data, container, false);
		unbinder = ButterKnife.bind(this, rootView);

		// TODO set these in layout instead
		solarActivityView.setImage(R.drawable.ic_timeline_white_96dp);
		geomagneticLocationView.setImage(R.drawable.ic_timeline_white_96dp);
		weatherView.setImage(R.drawable.ic_timeline_white_96dp);
		sunPositionView.setImage(R.drawable.ic_timeline_white_96dp);

		solarActivityPresenter = new SolarActivityPresenter(solarActivityView);
		geomagneticLocationPresenter = new GeomagneticLocationPresenter(geomagneticLocationView);
		weatherPresenter = new WeatherPresenter(weatherView);
		sunPositionPresenter = new SunPositionPresenter(sunPositionView);
		return rootView;
	}

	@Override
	public void onDestroyView() {
		Log.v(TAG, "onDestroyView");
		unbinder.unbind();
		super.onDestroyView();
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
