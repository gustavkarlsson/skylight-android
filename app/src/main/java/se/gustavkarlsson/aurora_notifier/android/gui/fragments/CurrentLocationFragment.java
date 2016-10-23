package se.gustavkarlsson.aurora_notifier.android.gui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import se.gustavkarlsson.aurora_notifier.android.background.AuroraPollingService;
import se.gustavkarlsson.aurora_notifier.android.databinding.FragmentCurrentLocationBinding;
import se.gustavkarlsson.aurora_notifier.android.gui.models.KpIndexModel;
import se.gustavkarlsson.aurora_notifier.android.parcels.AuroraUpdate;

public class CurrentLocationFragment extends Fragment {

	private static final String TAG = CurrentLocationFragment.class.getSimpleName();

	private KpIndexModel kpIndexModel;
	private BroadcastReceiver auroraUpdateReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		kpIndexModel = new KpIndexModel();
		auroraUpdateReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				AuroraUpdate auroraUpdate = Parcels.unwrap(intent.getParcelableExtra(AuroraUpdate.TAG));
				kpIndexModel.setValue(auroraUpdate.getKpIndex());
				kpIndexModel.setTimestamp(auroraUpdate.getKpIndexTimestamp());
			}
		};
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		FragmentCurrentLocationBinding binding = FragmentCurrentLocationBinding.inflate(inflater, container, false);
		binding.setKpIndex(kpIndexModel);
        return binding.getRoot();
    }

	@Override
	public void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(auroraUpdateReceiver, new IntentFilter(AuroraPollingService.ACTION_UPDATED));
	}

	public void onPause() {
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(auroraUpdateReceiver);
		super.onPause();
	}
}
