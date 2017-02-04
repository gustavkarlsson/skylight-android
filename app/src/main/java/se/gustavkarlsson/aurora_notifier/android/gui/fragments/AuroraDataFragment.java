package se.gustavkarlsson.aurora_notifier.android.gui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraEvaluationUpdateListener;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;

public class AuroraDataFragment extends Fragment implements AuroraEvaluationUpdateListener {
	private static final String TAG = AuroraDataFragment.class.getSimpleName();

	private Unbinder unbinder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_aurora_data, container, false);
		unbinder = ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onDestroyView() {
		Log.v(TAG, "onDestroyView");
		unbinder.unbind();
		super.onDestroyView();
	}

	@Override
	public void onUpdate(AuroraEvaluation evaluation) {

	}
}
