package se.gustavkarlsson.aurora_notifier.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.gustavkarlsson.aurora_notifier.android.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CurrentLocationFragment extends Fragment {

	private static final String TAG = CurrentLocationFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_current_location, container, false);
    }
}
