package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_chance;

import android.location.Address;
import android.view.View;
import android.widget.TextView;

class LocationPresenter {
	private final TextView locationTextView;

	LocationPresenter(TextView locationTextView) {
		this.locationTextView = locationTextView;
	}

	void update(Address address) {
		if (address == null) {
			locationTextView.setVisibility(View.INVISIBLE);
		} else {
			locationTextView.setVisibility(View.VISIBLE);
			String locationString = address.getLocality();
			locationTextView.setText(locationString);
		}
	}
}
