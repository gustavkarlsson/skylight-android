package se.gustavkarlsson.skylight.android.services_impl.presenters

import android.content.Context
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.longToast
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

class ToastErrorPresenter(private val theContext: Context) : Presenter<UserFriendlyException> {
	override fun present(value: UserFriendlyException) {
		async(UI) {
			theContext.longToast(value.stringResourceId)
		}
	}
}
