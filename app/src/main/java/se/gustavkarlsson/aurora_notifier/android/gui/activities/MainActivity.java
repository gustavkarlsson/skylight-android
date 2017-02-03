package se.gustavkarlsson.aurora_notifier.android.gui.activities;

import android.support.v4.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import se.gustavkarlsson.aurora_notifier.android.BuildConfig;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.DebugActivity;
import se.gustavkarlsson.aurora_notifier.android.background.UpdateService;
import se.gustavkarlsson.aurora_notifier.android.background.Updater;
import se.gustavkarlsson.aurora_notifier.android.caching.PersistentCache;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.DaggerMainActivityComponent;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.PersistentCacheModule;
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraEvaluationProvider;
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraEvaluationUpdateReceiver;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraChance;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraData;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.models.data.GeomagneticLocation;
import se.gustavkarlsson.aurora_notifier.android.models.data.SolarActivity;
import se.gustavkarlsson.aurora_notifier.android.models.data.SunPosition;
import se.gustavkarlsson.aurora_notifier.android.models.data.Weather;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class MainActivity extends AppCompatActivity implements AuroraEvaluationProvider {
	private static final String TAG = MainActivity.class.getSimpleName();

	private static final String STATE_AURORA_EVALUATION = "STATE_AURORA_EVALUATION";
	private static final String CACHE_KEY_EVALUATION = "CACHE_KEY_EVALUATION";

	private final ServiceConnection updaterConnection = new MainActivity.UpdaterConnection();

	@Inject
	PersistentCache<Parcelable> evaluationPersistentCache;

	@BindView(R.id.swipe_refresh_layout)
	SwipeRefreshLayout swipeRefreshLayout;

	@BindView(R.id.bottom_sheet)
	View bottomSheetView;

	private int evaluationLifeMillis;
	private AuroraEvaluation evaluation;
	private List<AuroraEvaluationUpdateReceiver> updateReceivers = emptyList();
	private LocalBroadcastManager broadcastManager;
	private BroadcastReceiver broadcastReceiver;
	private BottomSheetBehavior bottomSheetBehavior;

	private Updater updater;
	private boolean updaterBound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		DaggerMainActivityComponent.builder()
				.persistentCacheModule(new PersistentCacheModule(this, getResources().getInteger(R.integer.cache_size_bytes)))
				.build()
				.inject(this);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		evaluationLifeMillis = getResources().getInteger(R.integer.foreground_evaluation_life_millis);
		evaluation = getSavedEvaluation(savedInstanceState);
		updateReceivers = findUpdateReceivers();
		broadcastManager = LocalBroadcastManager.getInstance(this);
		broadcastReceiver = createBroadcastReceiver();
		bottomSheetBehavior = createBottomSheetBehavior(bottomSheetView);
		update(evaluation);
		setupSwipeToRefresh();
	}

	private AuroraEvaluation getSavedEvaluation(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			Parcelable parcelable = savedInstanceState.getParcelable(STATE_AURORA_EVALUATION);
			return Parcels.unwrap(parcelable);
		}
		if (evaluationPersistentCache.exists(CACHE_KEY_EVALUATION)) {
			Parcelable parcelable = evaluationPersistentCache.get(CACHE_KEY_EVALUATION);
			AuroraEvaluation evaluation = Parcels.unwrap(parcelable);
			if (evaluation != null) {
				return evaluation;
			}
		}
		return createUnknownEvaluation();
	}

	// TODO Move this somewhere else
	private static AuroraEvaluation createUnknownEvaluation() {
		AuroraData data = new AuroraData(
				new SolarActivity(0),
				new GeomagneticLocation(0),
				new SunPosition(0),
				new Weather(0)
		);
		AuroraComplication unknownComplication = new AuroraComplication(
				AuroraChance.UNKNOWN,
				R.string.complication_no_data_title,
				R.string.complication_no_data_desc);
		return new AuroraEvaluation(0, null, data, singletonList(unknownComplication));
	}

	private List<AuroraEvaluationUpdateReceiver> findUpdateReceivers() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		List<AuroraEvaluationUpdateReceiver> updateReceivers = new LinkedList<>();
		updateReceivers.add((AuroraEvaluationUpdateReceiver) fragmentManager.findFragmentById(R.id.fragment_aurora_chance));
		updateReceivers.add((AuroraEvaluationUpdateReceiver) fragmentManager.findFragmentById(R.id.fragment_aurora_data));
		updateReceivers.add((AuroraEvaluationUpdateReceiver) fragmentManager.findFragmentById(R.id.fragment_aurora_complications));
		return updateReceivers;
	}

	private BroadcastReceiver createBroadcastReceiver() {
		return new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (swipeRefreshLayout != null) {
					swipeRefreshLayout.setRefreshing(false);
				}
				String action = intent.getAction();
				if (UpdateService.RESPONSE_UPDATE_FINISHED.equals(action)) {
					Parcelable evaluationParcel = intent.getParcelableExtra(UpdateService.RESPONSE_UPDATE_FINISHED_EXTRA_EVALUATION);
					evaluation = Parcels.unwrap(evaluationParcel);
					update(evaluation);
				} else if (UpdateService.RESPONSE_UPDATE_ERROR.equals(action)) {
					String message = intent.getStringExtra(UpdateService.RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE);
					Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
				}
			}
		};
	}

	private static BottomSheetBehavior createBottomSheetBehavior(View bottomSheetView) {
		final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
		ensureSizeIsRecalculatedOnInteraction(bottomSheetBehavior);
		return bottomSheetBehavior;
	}

	//Workaround for bug described in http://stackoverflow.com/a/40267305/940731
	private static void ensureSizeIsRecalculatedOnInteraction(BottomSheetBehavior bottomSheetBehavior) {
		bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull final View bottomSheet, int newState) {
				bottomSheet.post(() -> {
					bottomSheet.requestLayout();
					bottomSheet.invalidate();
				});
			}

			@Override
			public void onSlide(@NonNull View bottomSheet, float slideOffset) {
			}
		});
	}

	private void setupSwipeToRefresh() {
		swipeRefreshLayout.setOnRefreshListener(() -> {
			if (updaterBound) {
				swipeRefreshLayout.setRefreshing(true);
				AsyncTask.execute(updater::update);
			}
		});
	}

	@OnClick(R.id.bottom_sheet)
	void onClick() {
		if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
			bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
		}
	}

	private void update(AuroraEvaluation evaluation) {
		updateBottomSheetState(evaluation);
		for (AuroraEvaluationUpdateReceiver receiver : updateReceivers) {
			receiver.update(evaluation);
		}
	}

	private void updateBottomSheetState(AuroraEvaluation evaluation) {
		if (evaluation.getComplications().isEmpty()) {
			bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
		} else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
			bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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
		int id = item.getItemId();

		if (id == R.id.action_debug) {
			Intent intent = new Intent(this, DebugActivity.class);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStart() {
		Log.v(TAG, "onStart");
		super.onStart();
		broadcastManager.registerReceiver((broadcastReceiver),
				new IntentFilter(UpdateService.RESPONSE_UPDATE_FINISHED));
		broadcastManager.registerReceiver((broadcastReceiver),
				new IntentFilter(UpdateService.RESPONSE_UPDATE_ERROR));
		bindUpdater();
	}

	private void bindUpdater() {
		Intent intent = new Intent(this, UpdateService.class);
		bindService(intent, updaterConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onStop() {
		Log.v(TAG, "onStop");
		swipeRefreshLayout.setRefreshing(false);
		broadcastManager.unregisterReceiver(broadcastReceiver);
		unbindUpdater();
		super.onStop();
	}

	private void unbindUpdater() {
		if (updaterBound) {
			unbindService(updaterConnection);
			updaterBound = false;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.v(TAG, "onSaveInstanceState");
		Parcelable parcel = Parcels.wrap(evaluation);
		outState.putParcelable(STATE_AURORA_EVALUATION, parcel);
		Parcelable parcelable = Parcels.wrap(evaluation);
		evaluationPersistentCache.set(CACHE_KEY_EVALUATION, parcelable);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		Log.v(TAG, "onDestroy");

		swipeRefreshLayout.setOnRefreshListener(null);
		try {
			evaluationPersistentCache.close();
		} catch (IOException e) {
			Log.e(TAG, "Failed to close cache", e);
		}
		super.onDestroy();
	}

	@Override
	public AuroraEvaluation getEvaluation() {
		return evaluation;
	}

	private boolean evaluationExpired() {
		long expiryTime = evaluation.getTimestampMillis() + evaluationLifeMillis;
		return System.currentTimeMillis() > expiryTime;
	}

	private class UpdaterConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.v(TAG, "onServiceConnected");
			UpdateService.UpdaterBinder updaterBinder = (UpdateService.UpdaterBinder) service;
			updater = updaterBinder.getUpdater();
			updaterBound = true;
			if (evaluationExpired()) {
				AsyncTask.execute(updater::update);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.v(TAG, "onServiceDisconnected");
			updaterBound = false;
		}

	}
}
