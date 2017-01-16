package se.gustavkarlsson.aurora_notifier.android.gui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.realm.Realm;
import se.gustavkarlsson.aurora_notifier.android.R;
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
import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraEvaluator;

public class CurrentLocationFragment extends Fragment {
	private static final String TAG = CurrentLocationFragment.class.getSimpleName();

	private Realm realm;
	private KpIndexViewModel kpIndexViewModel;
	private WeatherViewModel weatherViewModel;
	private SunPositionViewModel sunPositionViewModel;
	private GeomagneticCoordinatesViewModel geomagneticCoordinatesViewModel;
	private AuroraEvaluationViewModel auroraEvaluationViewModel;
	private BroadcastReceiver broadcastReceiver;
	private SwipeRefreshLayout swipeView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
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

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		FragmentCurrentLocationBinding binding = FragmentCurrentLocationBinding.inflate(inflater, container, false);
		View rootView = binding.getRoot();
		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				kpIndexViewModel.notifyChange();
				weatherViewModel.notifyChange();
				sunPositionViewModel.notifyChange();
				geomagneticCoordinatesViewModel.notifyChange();
				auroraEvaluationViewModel.notifyChange();
				swipeView.setRefreshing(false);
				if (intent.hasExtra(PollingService.ACTION_UPDATE_FINISHED_MESSAGE)) {
					String message = intent.getStringExtra(PollingService.ACTION_UPDATE_FINISHED_MESSAGE);
					Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
				}
			}
		};
		swipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);
		swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				swipeView.setRefreshing(true);
				PollingService.requestUpdate(getContext());
			}
		});
		binding.setAuroraEvaluation(auroraEvaluationViewModel);
		binding.setKpIndex(kpIndexViewModel);
		binding.setWeather(weatherViewModel);
		binding.setSunPosition(sunPositionViewModel);
		binding.setGeomagneticCoordinates(geomagneticCoordinatesViewModel);
		return rootView;
	}

	@Override
	public void onStart() {
		Log.v(TAG, "onStart");
		super.onStart();
		LocalBroadcastManager.getInstance(getContext()).registerReceiver((broadcastReceiver),
				new IntentFilter(PollingService.ACTION_UPDATE_FINISHED)
		);
	}

	@Override
	public void onStop() {
		Log.v(TAG, "onStop");
		LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		Log.v(TAG, "onDestroyView");
		swipeView.setOnRefreshListener(null);
		broadcastReceiver.abortBroadcast();

		swipeView = null;
		broadcastReceiver = null;

		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy");
		realm.close();

		realm = null;
		kpIndexViewModel = null;
		weatherViewModel = null;
		sunPositionViewModel = null;
		geomagneticCoordinatesViewModel = null;
		auroraEvaluationViewModel = null;
		broadcastReceiver = null;
		swipeView = null;

		super.onDestroy();
	}

}
