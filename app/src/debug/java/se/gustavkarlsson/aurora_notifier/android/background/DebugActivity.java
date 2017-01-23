package se.gustavkarlsson.aurora_notifier.android.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.databinding.ActivityDebugBinding;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.GeomagneticCoordinatesViewModel;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.KpIndexViewModel;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.SunPositionViewModel;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.WeatherViewModel;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmDebug;
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

		Switch toggle = (Switch) findViewById(R.id.debug_switch);
		final RealmDebug realmDebug = realm.where(RealmDebug.class).findFirst();
		toggle.setChecked(realmDebug.isEnabled());
		toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
				realm.executeTransaction(new Realm.Transaction() {
					@Override
					public void execute(Realm realm) {
						realmDebug.setEnabled(isChecked);
					}
				});
			}
		});

		final EditText setKpIndex = (EditText) findViewById(R.id.debug_set_kp_index);
		setKpIndex.setText("" + realmKpIndex.getKpIndex(), TextView.BufferType.EDITABLE);
		final EditText setDegreesFromClosestPole = (EditText) findViewById(R.id.debug_set_degrees_from_closest_pole);
		setDegreesFromClosestPole.setText("" + realmGeomagneticCoordinates.getDegreesFromClosestPole(), TextView.BufferType.EDITABLE);
		final EditText setSunZenithAngle = (EditText) findViewById(R.id.debug_set_sun_zenith_angle);
		setSunZenithAngle.setText("" + realmSunPosition.getZenithAngle(), TextView.BufferType.EDITABLE);
		final EditText setCloudPercentage = (EditText) findViewById(R.id.debug_set_cloud_percentage);
		setCloudPercentage.setText("" + realmWeather.getCloudPercentage(), TextView.BufferType.EDITABLE);

		Button setValuesButton = (Button) findViewById(R.id.debug_set_values);
		setValuesButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final RealmDebug realmDebug = realm.where(RealmDebug.class).findFirst();
				realm.executeTransaction(new Realm.Transaction() {
					@Override
					public void execute(Realm realm) {
						Float kpIndex = Float.valueOf(setKpIndex.getText().toString());
						Integer cloudPercentage = Integer.valueOf(setCloudPercentage.getText().toString());
						Float zenithAngle = Float.valueOf(setSunZenithAngle.getText().toString());
						Float degreesFromClosestPole = Float.valueOf(setDegreesFromClosestPole.getText().toString());
						realmDebug.setKpIndex(kpIndex);
						realmDebug.setCloudPercentage(cloudPercentage);
						realmDebug.setZenithAngle(zenithAngle);
						realmDebug.setDegreesFromClosestPole(degreesFromClosestPole);
					}
				});
			}
		});

		Button refreshButton = (Button) findViewById(R.id.debug_refresh);
		refreshButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				PollingService.requestUpdate(DebugActivity.this);
			}
		});

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
