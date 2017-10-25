package se.gustavkarlsson.skylight.android.services_impl.presenters

import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import se.gustavkarlsson.skylight.android.services.Presenter

class LocationActionBarPresenter(
	private val actionBar: android.support.v7.app.ActionBar,
	private val defaultName: String
) : Presenter<String?> {

    override fun present(value: String?) {
		async(UI) {
			actionBar.title = value ?: defaultName
		}
    }
}
