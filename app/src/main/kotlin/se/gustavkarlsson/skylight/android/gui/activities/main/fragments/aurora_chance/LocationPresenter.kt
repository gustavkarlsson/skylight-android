package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance

import android.location.Address
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.TextView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async

class LocationPresenter(private val locationTextView: TextView) {

    fun present(address: Address?) {
		async(UI) {
			if (address == null) {
				locationTextView.visibility = INVISIBLE
			} else {
				locationTextView.visibility = VISIBLE
				val locationString = address.locality
				locationTextView.text = locationString
			}
		}
    }
}
