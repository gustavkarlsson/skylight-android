package se.gustavkarlsson.aurora_notifier.android.gui.activities.main;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;

import java.util.List;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;

class BottomSheetPresenter {
	private final BottomSheetBehavior bottomSheetBehavior;
	private final boolean defaultHideable;

	BottomSheetPresenter(View bottomSheetView) {
		bottomSheetBehavior = createBottomSheetBehavior(bottomSheetView);
		defaultHideable = bottomSheetBehavior.isHideable();
		bottomSheetView.setOnClickListener(new ExpandOnClickListener());
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
