package se.gustavkarlsson.aurora_notifier.android.gui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.Realm;
import io.realm.RealmObject;
import se.gustavkarlsson.aurora_notifier.android.databinding.FragmentCurrentLocationBinding;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.GeomagneticCoordinatesViewModel;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.KpIndexViewModel;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.RealmViewModel;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.SunPositionViewModel;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.ViewModelUpdater;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.WeatherViewModel;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmGeomagneticCoordinates;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmKpIndex;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmSunPosition;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmWeather;

public class CurrentLocationFragment extends Fragment {
	private static final String TAG = CurrentLocationFragment.class.getSimpleName();

	private Realm realm;
	private KpIndexViewModel kpIndexViewModel;
	private WeatherViewModel weatherViewModel;
	private SunPositionViewModel sunPositionViewModel;
	private GeomagneticCoordinatesViewModel geomagneticCoordinatesViewModel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		realm = Realm.getDefaultInstance();

		RealmKpIndex realmKpIndex = realm.where(RealmKpIndex.class).findFirst();
		kpIndexViewModel = new KpIndexViewModel(realmKpIndex);
		setupChangeListener(realmKpIndex, kpIndexViewModel);

		RealmWeather realmWeather = realm.where(RealmWeather.class).findFirst();
		weatherViewModel = new WeatherViewModel(realmWeather);
		setupChangeListener(realmWeather, weatherViewModel);

		RealmSunPosition realmSunPosition = realm.where(RealmSunPosition.class).findFirst();
		sunPositionViewModel = new SunPositionViewModel(realmSunPosition);
		setupChangeListener(realmSunPosition, sunPositionViewModel);

		RealmGeomagneticCoordinates realmGeomagneticCoordinates = realm.where(RealmGeomagneticCoordinates.class).findFirst();
		geomagneticCoordinatesViewModel = new GeomagneticCoordinatesViewModel(realmGeomagneticCoordinates);
		setupChangeListener(realmGeomagneticCoordinates, geomagneticCoordinatesViewModel);
	}

	private <T extends RealmObject> void setupChangeListener(T realmObject, RealmViewModel<T> viewModel) {
		ViewModelUpdater listener = new ViewModelUpdater(viewModel);
		realmObject.addChangeListener(listener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		FragmentCurrentLocationBinding binding = FragmentCurrentLocationBinding.inflate(inflater, container, false);
		binding.setKpIndex(kpIndexViewModel);
		binding.setWeather(weatherViewModel);
		binding.setSunPosition(sunPositionViewModel);
		binding.setGeomagneticCoordinates(geomagneticCoordinatesViewModel);
		return binding.getRoot();
	}

	@Override
	public void onDestroy() {
		realm.close();
		super.onDestroy();
	}

}
