package se.gustavkarlsson.aurora_notifier.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.gustavkarlsson.aurora_notifier.android.databinding.FragmentCurrentLocationBinding;
import se.gustavkarlsson.aurora_notifier.android.models.KpIndexModel;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public class CurrentLocationFragment extends Fragment {

	private static final String TAG = CurrentLocationFragment.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		FragmentCurrentLocationBinding binding = FragmentCurrentLocationBinding.inflate(inflater, container, false);
		KpIndexModel kpIndexModel = new KpIndexModel();
		kpIndexModel.setKpIndex(new Timestamped<>(3f));
		binding.setKpIndex(kpIndexModel);
        return binding.getRoot();
    }
}
