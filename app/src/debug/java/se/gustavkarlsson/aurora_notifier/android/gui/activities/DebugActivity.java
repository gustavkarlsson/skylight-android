package se.gustavkarlsson.aurora_notifier.android.gui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import io.realm.Realm;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.realm.DebugSettings;

public class DebugActivity extends AppCompatActivity {
	private static final String TAG = DebugActivity.class.getSimpleName();

	private Realm realm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);
		realm = Realm.getDefaultInstance();
		DebugSettings debugSettings = DebugSettings.get(realm);

		ToggleButton enabledButton = (ToggleButton) findViewById(R.id.debug_enabled);
		enabledButton.setChecked(debugSettings.isEnabled());

		EditText kpIndexEditText = (EditText) findViewById(R.id.debug_kp_index);
		kpIndexEditText.setText("" + debugSettings.getKpIndex());

		EditText geomagneticLocationEditText = (EditText) findViewById(R.id.debug_geomagnetic_location);
		geomagneticLocationEditText.setText("" + debugSettings.getDegreesFromGeomagneticPole());

		EditText cloudPercentageEditText = (EditText) findViewById(R.id.debug_cloud_percentage);
		cloudPercentageEditText.setText("" + debugSettings.getCloudPercentage());

		EditText sunPositionEditText = (EditText) findViewById(R.id.debug_sun_position);
		sunPositionEditText.setText("" + debugSettings.getSunPosition());

		Button setValuesButton = (Button) findViewById(R.id.debug_set_values);
		setValuesButton.setOnClickListener(v -> {
			realm.executeTransaction(r -> {

				debugSettings.setEnabled(enabledButton.isChecked());

				String kpIndexString = kpIndexEditText.getText().toString();
				if (kpIndexString != null && !kpIndexString.isEmpty()) {
					debugSettings.setKpIndex(Float.valueOf(kpIndexString));
				}

				String geomagneticLocationString = geomagneticLocationEditText.getText().toString();
				if (geomagneticLocationString != null && !geomagneticLocationString.isEmpty()) {
					debugSettings.setDegreesFromGeomagneticPole(Float.valueOf(geomagneticLocationString));
				}

				String cloudPercentageString = cloudPercentageEditText.getText().toString();
				if (cloudPercentageString != null && !cloudPercentageString.isEmpty()) {
					debugSettings.setCloudPercentage(Integer.valueOf(cloudPercentageString));
				}

				String sunPositionString = sunPositionEditText.getText().toString();
				if (sunPositionString != null && !sunPositionString.isEmpty()) {
					debugSettings.setSunPosition(Float.valueOf(sunPositionString));
				}
			});
		});
	}

	@Override
	public void onStart() {
		Log.v(TAG, "onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		Log.v(TAG, "onStop");
		super.onStop();
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy");
		realm.close();
		super.onDestroy();
	}
}
