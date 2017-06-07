package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance

import android.location.Address
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.TextView

class LocationPresenter(private val locationTextView: TextView) {

    fun update(address: Address?) {
        if (address == null) {
            locationTextView.visibility = INVISIBLE
        } else {
            locationTextView.visibility = VISIBLE
            val locationString = address.locality
            locationTextView.text = locationString
        }
    }
}
