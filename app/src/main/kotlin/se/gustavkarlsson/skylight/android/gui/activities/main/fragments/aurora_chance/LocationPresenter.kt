package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance

import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.TextView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import se.gustavkarlsson.skylight.android.services.Presenter

class LocationPresenter(
	private val locationTextView: TextView
) : Presenter<String?> {

    override fun present(value: String?) {
		async(UI) {
			if (value == null) {
				locationTextView.visibility = INVISIBLE
			} else {
				locationTextView.text = value
				locationTextView.visibility = VISIBLE
			}
		}
    }
}
