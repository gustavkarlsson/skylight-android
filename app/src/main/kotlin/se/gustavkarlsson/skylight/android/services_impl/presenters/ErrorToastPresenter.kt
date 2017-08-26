package se.gustavkarlsson.skylight.android.services_impl.presenters

import android.content.Context
import dagger.Reusable
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.longToast
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Inject

@Reusable
class ErrorToastPresenter
@Inject
constructor(
        private val theContext: Context
) : Presenter<UserFriendlyException> {
    override fun present(value: UserFriendlyException) {
        async(UI) {
            theContext.longToast(value.stringResourceId)
        }
    }
}
