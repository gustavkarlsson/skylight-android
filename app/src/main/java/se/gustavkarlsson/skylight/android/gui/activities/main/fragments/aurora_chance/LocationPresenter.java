package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance;

import android.location.Address;
import android.widget.TextView;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class LocationPresenter {
	private final TextView locationTextView;

	public LocationPresenter(TextView locationTextView) {
		this.locationTextView = locationTextView;
	}

	void update(Address address) {
		if (address == null) {
			locationTextView.setVisibility(INVISIBLE);
		} else {
			locationTextView.setVisibility(VISIBLE);
			String locationString = address.getLocality();
			locationTextView.setText(locationString);
		}
	}
}
