package se.gustavkarlsson.skylight.android.dagger.modules.definitive

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.Module
import dagger.Provides
import org.jetbrains.anko.longToast
import se.gustavkarlsson.skylight.android.background.RESPONSE_UPDATE_ERROR
import se.gustavkarlsson.skylight.android.background.RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE
import se.gustavkarlsson.skylight.android.dagger.UPDATE_ERROR_NAME
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope
import javax.inject.Named

@Module
class UpdateErrorBroadcastReceiverModule {

    @Provides
    @Named(UPDATE_ERROR_NAME)
    @ActivityScope
    fun provideUpdateErrorBroadcastReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (RESPONSE_UPDATE_ERROR == action) {
                    val message = intent.getStringExtra(RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE)
					context.longToast(message)
                }
            }
        }
    }

}
