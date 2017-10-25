package se.gustavkarlsson.skylight.android.services_impl.presenters

import android.widget.TextView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import se.gustavkarlsson.skylight.android.services.Presenter

class LocationTextViewPresenter(
	private val locationTextView: TextView,
	private val defaultName: String
) : Presenter<String?> {

    override fun present(value: String?) {
		async(UI) {
			locationTextView.text = value ?: defaultName
		}
    }
}
