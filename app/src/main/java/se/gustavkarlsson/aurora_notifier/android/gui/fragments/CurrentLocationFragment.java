package se.gustavkarlsson.aurora_notifier.android.gui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import io.realm.Realm;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.PollingService;
import se.gustavkarlsson.aurora_notifier.android.databinding.FragmentCurrentLocationBinding;
import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraEvaluator;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.AuroraEvaluationViewModel;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmGeomagneticCoordinates;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmKpIndex;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmSunPosition;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmWeather;

public class CurrentLocationFragment extends Fragment {
	private static final String TAG = CurrentLocationFragment.class.getSimpleName();

	private Realm realm;
	private AuroraEvaluationViewModel auroraEvaluationViewModel;
	private BroadcastReceiver broadcastReceiver;
	private SwipeRefreshLayout swipeView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		realm = Realm.getDefaultInstance();

		RealmKpIndex realmKpIndex = realm.where(RealmKpIndex.class).findFirst();
		RealmWeather realmWeather = realm.where(RealmWeather.class).findFirst();
		RealmSunPosition realmSunPosition = realm.where(RealmSunPosition.class).findFirst();
		RealmGeomagneticCoordinates realmGeomagneticCoordinates = realm.where(RealmGeomagneticCoordinates.class).findFirst();
		auroraEvaluationViewModel = new AuroraEvaluationViewModel(new AuroraEvaluator(realmWeather, realmSunPosition, realmKpIndex, realmGeomagneticCoordinates));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		FragmentCurrentLocationBinding binding = FragmentCurrentLocationBinding.inflate(inflater, container, false);
		binding.setAuroraEvaluation(auroraEvaluationViewModel);
		final View rootView = binding.getRoot();
		broadcastReceiver = createBroadcastReceiver();
		swipeView = createSwipeRefresh(rootView);
		setUpBottomSheetBehavior(rootView);
		return rootView;
	}

	private BroadcastReceiver createBroadcastReceiver() {
		return new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				auroraEvaluationViewModel.notifyChange();
				swipeView.setRefreshing(false);
				if (intent.hasExtra(PollingService.ACTION_UPDATE_FINISHED_MESSAGE)) {
					String message = intent.getStringExtra(PollingService.ACTION_UPDATE_FINISHED_MESSAGE);
					Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
				}
			}
		};
	}

	private SwipeRefreshLayout createSwipeRefresh(View rootView) {
		final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
		swipeView.setOnRefreshListener(() -> {
            swipeView.setRefreshing(true);
            PollingService.requestUpdate(getContext());
        });
		return swipeView;
	}

	private static void setUpBottomSheetBehavior(View rootView) {
		RelativeLayout bottomSheetLayout = (RelativeLayout) rootView.findViewById(R.id.linear_layout_bottom_sheet);
		final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
		bottomSheetLayout.setOnClickListener(view -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
		ensureSizeIsRecalculatedOnInteraction(bottomSheetBehavior);
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

		swipeView = null;
		broadcastReceiver = null;

		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy");
		realm.close();

		realm = null;
		auroraEvaluationViewModel = null;

		super.onDestroy();
	}

}
