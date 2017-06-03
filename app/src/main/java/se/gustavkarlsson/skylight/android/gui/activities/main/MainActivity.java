package se.gustavkarlsson.skylight.android.gui.activities.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.threeten.bp.Clock;
import org.threeten.bp.Duration;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.background.Updater;
import se.gustavkarlsson.skylight.android.cache.LastReportCache;
import se.gustavkarlsson.skylight.android.dagger.components.MainActivityComponent;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ActivityModule;
import se.gustavkarlsson.skylight.android.gui.AuroraReportUpdateListener;
import se.gustavkarlsson.skylight.android.gui.activities.AuroraRequirementsCheckingActivity;
import se.gustavkarlsson.skylight.android.gui.activities.settings.SettingsActivity;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.observers.DataObserver;
import se.gustavkarlsson.skylight.android.observers.ObservableData;

import static se.gustavkarlsson.skylight.android.Skylight.getApplicationComponent;
import static se.gustavkarlsson.skylight.android.background.UpdateJob.BACKGROUND_UPDATE_TIMEOUT;
import static se.gustavkarlsson.skylight.android.background.Updater.RESPONSE_UPDATE_ERROR;
import static se.gustavkarlsson.skylight.android.background.Updater.RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE;

public class MainActivity extends AuroraRequirementsCheckingActivity implements DataObserver<AuroraReport> {
	private static final String TAG = MainActivity.class.getSimpleName();

	private static final Duration REPORT_LIFETIME = Duration.ofMinutes(15);

	@Inject
	Updater updater;

	@Inject
	LastReportCache lastReportCache;

	@Inject
	LocalBroadcastManager broadcastManager;

	@Inject
	Clock clock;

	@Inject
	SwipeToRefreshPresenter swipeToRefreshPresenter;

	@Inject
	@Named("Latest")
	ObservableData<AuroraReport> lastAuroraReport;

	private MainActivityComponent component;

	private List<AuroraReportUpdateListener> updateReceivers;
	private BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		component = getApplicationComponent().getMainActivityComponent(new ActivityModule(this));
		setContentView(R.layout.activity_main);
		component.inject(this);

		updateReceivers = getUpdateReceivers();
		broadcastReceiver = createBroadcastReceiver();
		// TODO Keep daggerifying
	}

	private List<AuroraReportUpdateListener> getUpdateReceivers() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		List<AuroraReportUpdateListener> updateReceivers = new LinkedList<>();
		updateReceivers.add((AuroraReportUpdateListener) fragmentManager.findFragmentById(R.id.fragment_aurora_chance));
		updateReceivers.add((AuroraReportUpdateListener) fragmentManager.findFragmentById(R.id.fragment_aurora_factors));
		return updateReceivers;
	}

	private BroadcastReceiver createBroadcastReceiver() {
		return new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (RESPONSE_UPDATE_ERROR.equals(action)) {
					String message = intent.getStringExtra(RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE);
					Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
				}
			}
		};
	}

	private void updateGui(AuroraReport report) {
		runOnUiThread(() -> {
			for (AuroraReportUpdateListener receiver : updateReceivers) {
				receiver.onUpdate(report);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(TAG, "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(TAG, "onOptionsItemSelected");
		switch (item.getItemId()) {
			case R.id.action_settings: {
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStart() {
		Log.v(TAG, "onStart");
		super.onStart();
		broadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(RESPONSE_UPDATE_ERROR));
		lastAuroraReport.addListener(this);
		AuroraReport report = getBestReport();
		swipeToRefreshPresenter.disable();
		ensureRequirementsMet();
		updateGui(report);
	}

	@Override
	public void dataChanged(AuroraReport newReport) {
		updateGui(newReport);
	}

	@Override
	protected void onRequirementsMet() {
		swipeToRefreshPresenter.enable();
		AuroraReport report = getBestReport();
		long ageMillis = clock.millis() - report.getTimestampMillis();
		if (ageMillis > REPORT_LIFETIME.toMillis()) {
			updateInBackground();
		}
	}

	private AuroraReport getBestReport() {
		AuroraReport lastReport = lastReportCache.get();
		if (lastReport != null) {
			return lastReport;
		}
		return AuroraReport.createFallback();
	}

	private void updateInBackground() {
		AsyncTask.execute(() -> updater.update(BACKGROUND_UPDATE_TIMEOUT.toMillis()));
	}

	@Override
	protected void onStop() {
		Log.v(TAG, "onStop");
		super.onStop();
		lastAuroraReport.removeListener(this);
		broadcastManager.unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void onDestroy() {
		Log.v(TAG, "onDestroy");
		updater = null;
		lastReportCache = null;
		broadcastManager = null;
		swipeToRefreshPresenter = null;
		updateReceivers = null;
		broadcastReceiver = null;
		component = null;
		lastAuroraReport = null;
		super.onDestroy();
	}

	public MainActivityComponent getComponent() {
		return component;
	}
}
