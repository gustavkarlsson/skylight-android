package se.gustavkarlsson.aurora_notifier.android.gui.activities.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.parceler.Parcels;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import se.gustavkarlsson.aurora_notifier.android.BuildConfig;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.Updater;
import se.gustavkarlsson.aurora_notifier.android.cache.AuroraEvaluationCache;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.DaggerMainActivityComponent;
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraEvaluationUpdateListener;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.AuroraRequirementsCheckingActivity;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.DebugActivity;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.settings.SettingsActivity;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;

import static se.gustavkarlsson.aurora_notifier.android.AuroraNotifier.getApplicationComponent;
import static se.gustavkarlsson.aurora_notifier.android.background.Updater.RESPONSE_UPDATE_ERROR;
import static se.gustavkarlsson.aurora_notifier.android.background.Updater.RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE;
import static se.gustavkarlsson.aurora_notifier.android.background.Updater.RESPONSE_UPDATE_FINISHED;
import static se.gustavkarlsson.aurora_notifier.android.background.Updater.RESPONSE_UPDATE_FINISHED_EXTRA_EVALUATION;

public class MainActivity extends AuroraRequirementsCheckingActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	@Inject
	AuroraEvaluationCache auroraEvaluationCache;

	private int evaluationLifetimeMillis;
	private int backgroundUpdateTimeoutMillis;
	private SwipeToRefreshPresenter swipeToRefreshPresenter;
	private BottomSheetPresenter bottomSheetPresenter;
	private List<AuroraEvaluationUpdateListener> updateReceivers;
	private BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		DaggerMainActivityComponent.builder()
				.applicationComponent(getApplicationComponent(this))
				.build()
				.inject(this);
		evaluationLifetimeMillis = getResources().getInteger(R.integer.setting_foreground_evaluation_lifetime_millis);
		backgroundUpdateTimeoutMillis = getResources().getInteger(R.integer.setting_background_update_timeout_millis);
		swipeToRefreshPresenter = new SwipeToRefreshPresenter((SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout), this);
		bottomSheetPresenter = new BottomSheetPresenter(findViewById(R.id.bottom_sheet));
		updateReceivers = getUpdateReceivers();
		broadcastReceiver = createBroadcastReceiver();
		LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver), new IntentFilter(RESPONSE_UPDATE_FINISHED));
		LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver), new IntentFilter(RESPONSE_UPDATE_ERROR));
	}

	private List<AuroraEvaluationUpdateListener> getUpdateReceivers() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		List<AuroraEvaluationUpdateListener> updateReceivers = new LinkedList<>();
		updateReceivers.add((AuroraEvaluationUpdateListener) fragmentManager.findFragmentById(R.id.fragment_aurora_chance));
		updateReceivers.add((AuroraEvaluationUpdateListener) fragmentManager.findFragmentById(R.id.fragment_aurora_data));
		updateReceivers.add((AuroraEvaluationUpdateListener) fragmentManager.findFragmentById(R.id.fragment_aurora_complications));
		return updateReceivers;
	}

	private BroadcastReceiver createBroadcastReceiver() {
		return new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (RESPONSE_UPDATE_FINISHED.equals(action)) {
					Parcelable evaluationParcel = intent.getParcelableExtra(RESPONSE_UPDATE_FINISHED_EXTRA_EVALUATION);
					AuroraEvaluation evaluation = Parcels.unwrap(evaluationParcel);
					updateGui(evaluation);
				} else if (RESPONSE_UPDATE_ERROR.equals(action)) {
					String message = intent.getStringExtra(RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE);
					Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
				}
			}
		};
	}

	private void updateGui(AuroraEvaluation evaluation) {
		bottomSheetPresenter.onUpdate(evaluation.getComplications());
		for (AuroraEvaluationUpdateListener receiver : updateReceivers) {
			receiver.onUpdate(evaluation);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(TAG, "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.v(TAG, "onPrepareOptionsMenu");
		if (!BuildConfig.DEBUG) {
			menu.removeItem(R.id.action_debug);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(TAG, "onOptionsItemSelected");
		switch (item.getItemId()) {
			case R.id.action_debug: {
				Intent intent = new Intent(this, DebugActivity.class);
				startActivity(intent);
				return true;
			}
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
		AuroraEvaluation evaluation = getBestEvaluation();
		ensureRequirementsMet();
		updateGui(evaluation);
	}

	@Override
	protected void onRequirementsMet() {
		swipeToRefreshPresenter.enable();
		AuroraEvaluation evaluation = getBestEvaluation();
		long ageMillis = System.currentTimeMillis() - evaluation.getTimestampMillis();
		if (ageMillis > evaluationLifetimeMillis) {
			updateInBackground();
		}
	}

	private AuroraEvaluation getBestEvaluation() {
		AuroraEvaluation evaluation = auroraEvaluationCache.getCurrentLocation();
		if (evaluation != null) {
			return evaluation;
		}
		return AuroraEvaluation.createFallback();
	}

	private void updateInBackground() {
		AsyncTask.execute(() -> {
			Updater updater = new Updater(this, backgroundUpdateTimeoutMillis);
			updater.update();
		});
	}

	@Override
	public void onStop() {
		Log.v(TAG, "onStop");
		swipeToRefreshPresenter.disable();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.v(TAG, "onDestroy");
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}
}
