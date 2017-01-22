package se.gustavkarlsson.aurora_notifier.android.gui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import io.realm.Realm;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.PollingService;
import se.gustavkarlsson.aurora_notifier.android.databinding.ActivityDebugBinding;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.GeomagneticCoordinatesViewModel;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.KpIndexViewModel;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.SunPositionViewModel;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.WeatherViewModel;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmGeomagneticCoordinates;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmKpIndex;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmSunPosition;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmWeather;

public class DebugActivity extends AppCompatActivity {
	private static final String TAG = DebugActivity.class.getSimpleName();

	private Realm realm;
	private KpIndexViewModel kpIndexViewModel;
	private WeatherViewModel weatherViewModel;
	private SunPositionViewModel sunPositionViewModel;
	private GeomagneticCoordinatesViewModel geomagneticCoordinatesViewModel;
	private BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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

		ActivityDebugBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_debug);
		broadcastReceiver = createBroadcastReceiver();
		bindViewModels(binding);
	}

	private BroadcastReceiver createBroadcastReceiver() {
		return new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				kpIndexViewModel.notifyChange();
				weatherViewModel.notifyChange();
				sunPositionViewModel.notifyChange();
				geomagneticCoordinatesViewModel.notifyChange();
				if (intent.hasExtra(PollingService.ACTION_UPDATE_FINISHED_MESSAGE)) {
					String message = intent.getStringExtra(PollingService.ACTION_UPDATE_FINISHED_MESSAGE);
					Toast.makeText(DebugActivity.this, message, Toast.LENGTH_SHORT).show();
				}
			}
		};
	}

	private void bindViewModels(ActivityDebugBinding binding) {
		binding.setKpIndex(kpIndexViewModel);
		binding.setWeather(weatherViewModel);
		binding.setSunPosition(sunPositionViewModel);
		binding.setGeomagneticCoordinates(geomagneticCoordinatesViewModel);
	}

	@Override
	public void onStart() {
		Log.v(TAG, "onStart");
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver),
				new IntentFilter(PollingService.ACTION_UPDATE_FINISHED)
		);
	}

	@Override
	public void onStop() {
		Log.v(TAG, "onStop");
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
		super.onStop();
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy");
		realm.close();

		realm = null;
		broadcastReceiver = null;

		super.onDestroy();
	}
}
