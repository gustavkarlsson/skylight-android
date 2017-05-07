package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_chance;

import android.location.Address;
import android.widget.TextView;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

class LocationPresenter {
	private final TextView locationTextView;

	LocationPresenter(TextView locationTextView) {
		this.locationTextView = locationTextView;
	}

	void onUpdate(Address address) {
		if (address == null) {
			locationTextView.setVisibility(INVISIBLE);
		} else {
			locationTextView.setVisibility(VISIBLE);
			String locationString = address.getLocality();
			locationTextView.setText(locationString);
		}
	}
}
