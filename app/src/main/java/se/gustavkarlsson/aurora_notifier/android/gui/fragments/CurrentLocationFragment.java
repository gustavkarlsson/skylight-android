package se.gustavkarlsson.aurora_notifier.android.gui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import io.realm.Realm;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.AuroraPollingService;
import se.gustavkarlsson.aurora_notifier.android.databinding.FragmentCurrentLocationBinding;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.KpIndexViewModel;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.ViewModelUpdater;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.WeatherViewModel;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmKpIndex;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmWeather;

public class CurrentLocationFragment extends Fragment {
	private static final String TAG = CurrentLocationFragment.class.getSimpleName();

	private Realm realm;
	private KpIndexViewModel kpIndexViewModel;
	private WeatherViewModel weatherViewModel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		realm = Realm.getDefaultInstance();

		RealmKpIndex realmKpIndex = realm.where(RealmKpIndex.class).findFirst();
		kpIndexViewModel = new KpIndexViewModel(realmKpIndex);
		ViewModelUpdater realmKpIndexListener = new ViewModelUpdater(kpIndexViewModel);
		realmKpIndex.addChangeListener(realmKpIndexListener);

		RealmWeather realmWeather = realm.where(RealmWeather.class).findFirst();
		weatherViewModel = new WeatherViewModel(realmWeather);
		ViewModelUpdater realmWeatherListener = new ViewModelUpdater(weatherViewModel);
		realmWeather.addChangeListener(realmWeatherListener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		FragmentCurrentLocationBinding binding = FragmentCurrentLocationBinding.inflate(inflater, container, false);
		binding.setKpIndex(kpIndexViewModel);
		binding.setWeather(weatherViewModel);
		return binding.getRoot();
	}

	@Override
	public void onDestroy() {
		realm.close();
		super.onDestroy();
	}

}
