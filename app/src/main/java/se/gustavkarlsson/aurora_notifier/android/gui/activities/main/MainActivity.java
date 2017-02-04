package se.gustavkarlsson.aurora_notifier.android.gui.activities.main;

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
import android.support.v4.app.FragmentManager;
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
import se.gustavkarlsson.aurora_notifier.android.BuildConfig;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.DebugActivity;
import se.gustavkarlsson.aurora_notifier.android.background.UpdateService;
import se.gustavkarlsson.aurora_notifier.android.background.Updater;
import se.gustavkarlsson.aurora_notifier.android.caching.PersistentCache;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.DaggerMainActivityComponent;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.PersistentCacheModule;
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraEvaluationUpdateListener;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;

import static se.gustavkarlsson.aurora_notifier.android.background.UpdateService.CACHE_KEY_EVALUATION;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	private static final String STATE_AURORA_EVALUATION = "STATE_AURORA_EVALUATION";

	private final ServiceConnection updaterConnection = new MainActivity.UpdaterConnection();

	@Inject
	PersistentCache<Parcelable> persistentCache;

	@BindView(R.id.swipe_refresh_layout)
	SwipeRefreshLayout swipeRefreshLayout;

	@BindView(R.id.bottom_sheet)
	View bottomSheetView;

	private AuroraEvaluation evaluation;
	private List<AuroraEvaluationUpdateListener> updateReceivers;
	private LocalBroadcastManager broadcastManager;
	private BroadcastReceiver broadcastReceiver;
	private BottomSheetPresenter bottomSheetPresenter;

	private Updater updater;
	private boolean updaterBound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		DaggerMainActivityComponent.builder()
				.persistentCacheModule(new PersistentCacheModule(this))
				.build()
				.inject(this);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		evaluation = getBestEvaluation(savedInstanceState);
		updateReceivers = getUpdateReceivers();
		broadcastManager = LocalBroadcastManager.getInstance(this);
		broadcastReceiver = createBroadcastReceiver();
		bottomSheetPresenter = new BottomSheetPresenter(bottomSheetView);
		update(evaluation);
		setupSwipeToRefresh();
	}

	private AuroraEvaluation getBestEvaluation(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			Parcelable parcelable = savedInstanceState.getParcelable(STATE_AURORA_EVALUATION);
			return Parcels.unwrap(parcelable);
		}
		if (persistentCache.exists(CACHE_KEY_EVALUATION)) {
			Parcelable parcelable = persistentCache.get(CACHE_KEY_EVALUATION);
			AuroraEvaluation evaluation = Parcels.unwrap(parcelable);
			if (evaluation != null) {
				return evaluation;
			}
		}
		return AuroraEvaluation.createFallback();
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

	private void setupSwipeToRefresh() {
		swipeRefreshLayout.setOnRefreshListener(() -> {
			if (updaterBound) {
				swipeRefreshLayout.setRefreshing(true);
				AsyncTask.execute(updater::update);
			}
		});
	}

	private void update(AuroraEvaluation evaluation) {
		bottomSheetPresenter.update(evaluation.getComplications());
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
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		Log.v(TAG, "onDestroy");

		swipeRefreshLayout.setOnRefreshListener(null);
		try {
			persistentCache.close();
		} catch (IOException e) {
			Log.e(TAG, "Failed to close cache", e);
		}
		super.onDestroy();
	}

	private class UpdaterConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.v(TAG, "onServiceConnected");
			UpdateService.UpdaterBinder updaterBinder = (UpdateService.UpdaterBinder) service;
			updater = updaterBinder.getUpdater();
			updaterBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.v(TAG, "onServiceDisconnected");
			updaterBound = false;
		}

	}
}
