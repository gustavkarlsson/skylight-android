package se.gustavkarlsson.aurora_notifier.android.gui.activities.main;

import android.support.design.widget.BottomSheetBehavior;
import android.view.View;

import java.util.List;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;

class BottomSheetPresenter {
	private final BottomSheetBehavior bottomSheetBehavior;
	private final boolean defaultHideable;

	BottomSheetPresenter(View bottomSheetView) {
		bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
		defaultHideable = bottomSheetBehavior.isHideable();
		bottomSheetView.setOnClickListener(new ExpandOnClickListener());
	}

	void onUpdate(List<AuroraComplication> complications) {
		if (complications.isEmpty()) {
			bottomSheetBehavior.setHideable(true);
			bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
		} else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
			bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
			bottomSheetBehavior.setHideable(defaultHideable);
		}
	}

	private class ExpandOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
				bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
			}
		}
	}
}
