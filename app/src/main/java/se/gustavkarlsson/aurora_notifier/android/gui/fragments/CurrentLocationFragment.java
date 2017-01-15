package se.gustavkarlsson.aurora_notifier.android.gui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.Realm;
import se.gustavkarlsson.aurora_notifier.android.background.PollingService;
import se.gustavkarlsson.aurora_notifier.android.databinding.FragmentCurrentLocationBinding;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.AuroraEvaluationViewModel;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.GeomagneticCoordinatesViewModel;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.KpIndexViewModel;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.SunPositionViewModel;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.WeatherViewModel;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmGeomagneticCoordinates;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmKpIndex;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmSunPosition;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmWeather;
import se.gustavkarlsson.aurora_notifier.android.util.AuroraEvaluator;

public class CurrentLocationFragment extends Fragment {
	private static final String TAG = CurrentLocationFragment.class.getSimpleName();

	private Realm realm;
	private KpIndexViewModel kpIndexViewModel;
	private WeatherViewModel weatherViewModel;
	private SunPositionViewModel sunPositionViewModel;
	private GeomagneticCoordinatesViewModel geomagneticCoordinatesViewModel;
	private AuroraEvaluationViewModel auroraEvaluationViewModel;
	private BroadcastReceiver broadcastReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		realm = Realm.getDefaultInstance();

		RealmKpIndex realmKpIndex = realm.where(RealmKpIndex.class).findFirst();
		kpIndexViewModel = new KpIndexViewModel(realmKpIndex);

		RealmWeather realmWeather = realm.where(RealmWeather.class).findFirst();
		weatherViewModel = new WeatherViewModel(realmWeather);

		RealmSunPosition realmSunPosition = realm.where(RealmSunPosition.class).findFirst();
		sunPositionViewModel = new SunPositionViewModel(realmSunPosition);

		RealmGeomagneticCoordinates realmGeomagneticCoordinates = realm.where(RealmGeomagneticCoordinates.class).findFirst();
		geomagneticCoordinatesViewModel = new GeomagneticCoordinatesViewModel(realmGeomagneticCoordinates);

		auroraEvaluationViewModel = new AuroraEvaluationViewModel(new AuroraEvaluator(realmWeather, realmSunPosition, realmKpIndex, realmGeomagneticCoordinates));

		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				kpIndexViewModel.notifyChange();
				weatherViewModel.notifyChange();
				sunPositionViewModel.notifyChange();
				geomagneticCoordinatesViewModel.notifyChange();
				auroraEvaluationViewModel.notifyChange();
			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		FragmentCurrentLocationBinding binding = FragmentCurrentLocationBinding.inflate(inflater, container, false);
		binding.setAuroraEvaluation(auroraEvaluationViewModel);
		binding.setKpIndex(kpIndexViewModel);
		binding.setWeather(weatherViewModel);
		binding.setSunPosition(sunPositionViewModel);
		binding.setGeomagneticCoordinates(geomagneticCoordinatesViewModel);
		return binding.getRoot();
	}

	@Override
	public void onStart() {
		super.onStart();
		LocalBroadcastManager.getInstance(getContext()).registerReceiver((broadcastReceiver),
				new IntentFilter(PollingService.ACTION_UPDATE_FINISHED)
		);
	}

	@Override
	public void onStop() {
		LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
		super.onStop();
	}

	@Override
	public void onDestroy() {
		realm.close();
		super.onDestroy();
	}

}
