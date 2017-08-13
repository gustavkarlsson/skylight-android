package se.gustavkarlsson.skylight.android.services_impl.presenters

import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.TextView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import se.gustavkarlsson.skylight.android.services.Presenter

class LocationTextViewPresenter(
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
