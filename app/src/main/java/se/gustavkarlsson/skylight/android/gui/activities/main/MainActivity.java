package se.gustavkarlsson.skylight.android.gui.activities.main;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.threeten.bp.Clock;
import org.threeten.bp.Duration;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;

import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.Skylight;
import se.gustavkarlsson.skylight.android.background.Updater;
import se.gustavkarlsson.skylight.android.dagger.components.MainActivityComponent;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ActivityModule;
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.skylight.android.gui.activities.AuroraRequirementsCheckingActivity;
import se.gustavkarlsson.skylight.android.gui.activities.settings.SettingsActivity;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.observers.ObservableValue;

import static se.gustavkarlsson.skylight.android.dagger.Names.BACKGROUND_UPDATE_TIMEOUT_NAME;
import static se.gustavkarlsson.skylight.android.dagger.Names.CACHED_THREAD_POOL_NAME;
import static se.gustavkarlsson.skylight.android.dagger.Names.FOREGROUND_REPORT_LIFETIME_NAME;
import static se.gustavkarlsson.skylight.android.dagger.Names.LATEST_NAME;
import static se.gustavkarlsson.skylight.android.dagger.Names.UPDATE_ERROR_NAME;

public class MainActivity extends AuroraRequirementsCheckingActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	@Inject
	Updater updater;

	@Inject
	LocalBroadcastManager broadcastManager;

	@Inject
	Clock clock;

	@Inject
	SwipeToRefreshPresenter swipeToRefreshPresenter;

	@Inject
	@Named(LATEST_NAME)
	ObservableValue<AuroraReport> latestAuroraReport;

	@Inject
	ChanceEvaluator<AuroraReport> auroraChanceEvaluator;

	@Inject
	@Named(UPDATE_ERROR_NAME)
	BroadcastReceiver broadcastReceiver;

	@Inject
	@Named(CACHED_THREAD_POOL_NAME)
	ExecutorService cachedTreadPool;

	@Inject
	@Named(BACKGROUND_UPDATE_TIMEOUT_NAME)
	Duration backgoundUpdateTimeout;

	@Inject
	@Named(FOREGROUND_REPORT_LIFETIME_NAME)
	Duration foregroundReportLifetime;

	private MainActivityComponent component;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		component = Skylight.Companion.getApplicationComponent().getMainActivityComponent(new ActivityModule(this));
		setContentView(R.layout.activity_main);
		component.inject(this);
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
		broadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(Updater.Companion.getRESPONSE_UPDATE_ERROR()));
		swipeToRefreshPresenter.disable();
		ensureRequirementsMet();
	}

	@Override
	protected void onRequirementsMet() {
		swipeToRefreshPresenter.enable();
		AuroraReport latestReport = latestAuroraReport.getValue();
		if (needsUpdate(latestReport)) {
			updateInBackground();
		}
	}

	private boolean needsUpdate(AuroraReport report) {
		boolean hasExpired = clock.millis() - report.getTimestampMillis() > foregroundReportLifetime.toMillis();
		boolean isUnknown = !auroraChanceEvaluator.evaluate(report).isKnown();
		return hasExpired || isUnknown;
	}

	private void updateInBackground() {
		cachedTreadPool.execute(() -> updater.update(backgoundUpdateTimeout));
	}

	@Override
	protected void onStop() {
		Log.v(TAG, "onStop");
		super.onStop();
		broadcastManager.unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void onDestroy() {
		Log.v(TAG, "onDestroy");
		updater = null;
		broadcastManager = null;
		swipeToRefreshPresenter = null;
		broadcastReceiver = null;
		component = null;
		latestAuroraReport = null;
		clock = null;
		super.onDestroy();
	}

	public MainActivityComponent getComponent() {
		return component;
	}
}
